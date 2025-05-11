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


package top.osjf.sdk.spring.runner;

import com.alibaba.qlexpress4.annotation.QLFunction;
import top.osjf.sdk.spring.annotation.Sdk;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An annotation processor that processes classes annotated with {@link Sdk}.
 * It generates properties files containing mappings of method names to their
 * respective values.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@SupportedAnnotationTypes(RunnerProcessor.SDK_ENUM)
public class RunnerProcessor extends AbstractProcessor {

    public static final String SDK_ENUM = "top.osjf.sdk.spring.annotation.Sdk";

    private static final String PROPERTIES_NAME = "qlexpress4.properties";

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        Map<String, String> properties = new HashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(Sdk.class)) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                Name typeName = typeElement.getQualifiedName();
                for (Element enclosedElement : typeElement.getEnclosedElements()) {
                    if (enclosedElement instanceof ExecutableElement) {
                        Name methodName = enclosedElement.getSimpleName();
                        String v = getValue(typeName, methodName);
                        QLFunction annotation = enclosedElement.getAnnotation(QLFunction.class);
                        if (annotation != null) {
                            for (String value : annotation.value()) {
                                properties.put(value, v);
                            }
                        } else {
                            properties.put(getKey(typeName, methodName), v);
                        }
                    }
                }
            }
        }
        createProperties(properties);
        return true;
    }

    /**
     * Creates properties files in both class output and source output locations.
     *
     * @param properties the properties to be written to the properties files
     */
    private void createProperties(Map<String, String> properties) {
        try {
            createProperties0(StandardLocation.CLASS_OUTPUT, properties);
            createProperties0(StandardLocation.SOURCE_OUTPUT, properties);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Helper method to create a properties file in the specified location.
     *
     * @param standardLocation the standard location where the properties file will be created
     * @param properties       the properties to be written to the properties file
     * @throws Throwable if an error occurs while creating or writing to the properties file
     */
    private void createProperties0(StandardLocation standardLocation, Map<String, String> properties) throws Throwable {
        FileObject co = filer.createResource(standardLocation, "", PROPERTIES_NAME);
        try (PrintWriter writer = new PrintWriter(co.openWriter())) {
            properties.forEach((k, v) -> writer.write(k + "=" + v + "\n"));
        }
    }

    /**
     * Generates a key for the properties file based on the type name and method name.
     *
     * @param typeName   the qualified name of the type
     * @param methodName the simple name of the method
     * @return a string representing the key in the format "typeName@methodName"
     */
    private static String getKey(Name typeName, Name methodName) {
        return typeName + "@" + methodName;
    }

    /**
     * Generates a value for the properties file based on the type name and method name.
     *
     * @param typeName   the qualified name of the type
     * @param methodName the simple name of the method
     * @return a string representing the value in the format "typeName.methodName"
     */
    private static String getValue(Name typeName, Name methodName) {
        return typeName + "." + methodName;
    }
}
