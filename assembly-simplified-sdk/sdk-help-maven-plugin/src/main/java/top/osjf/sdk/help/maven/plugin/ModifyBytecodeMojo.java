/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.osjf.sdk.help.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import top.osjf.sdk.core.util.CollectionUtils;

import javax.tools.JavaFileObject;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@Mojo(name = "modify-bytecode", defaultPhase = LifecyclePhase.COMPILE)
public class ModifyBytecodeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        executeInternal();
    }

    private void executeInternal() throws MojoExecutionException, MojoFailureException {
        try {
            List<File> classFiles = new ArrayList<>();
            String outputDirectory = project.getBuild().getOutputDirectory();
            File[] files = new File(outputDirectory).listFiles(f -> f.isDirectory() || isJavaClassFile(f));
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        collectionClassFilesUsingDirectory(file, classFiles);
                    } else {
                        if (isJavaClassFile(file)) {
                            classFiles.add(file);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(classFiles)) resolveClassFiles(classFiles);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private boolean isJavaClassFile(File f) {
        return f.getName().endsWith(JavaFileObject.Kind.CLASS.extension);
    }

    private String getClassName(File classFile) {
        return classFile.getPath().replace(project.getBuild().getOutputDirectory() + "/", "")
                .replace("/", ".")
                .replace(JavaFileObject.Kind.CLASS.extension, "");
    }

    private void resolveClassFiles(List<File> classFiles) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URLClassLoader urlClassLoader = new URLClassLoader
                (new URL[]{new File(project.getBuild().getOutputDirectory()).toURI().toURL()}, classLoader);
        for (File classFile : classFiles) {
            String className = getClassName(classFile);
            try {
                Class<?> clazz = urlClassLoader.loadClass(className);
                getLog().info("Load class [ " + clazz + " ].");
            } catch (Throwable e) {
                getLog().info("[ " + className + " ] load failed [" + e.getMessage() + "]");
            }

        }
        urlClassLoader.close();
    }

    private void collectionClassFilesUsingDirectory(File directory, List<File> classFiles) {
        File[] files = directory.listFiles(f -> isJavaClassFile(f) || f.isDirectory());
        if (files != null) {
            for (File file0 : files) {
                if (isJavaClassFile(file0)) {
                    classFiles.add(file0);
                } else {
                    collectionClassFilesUsingDirectory(file0, classFiles);
                }
            }
        }
    }
}
