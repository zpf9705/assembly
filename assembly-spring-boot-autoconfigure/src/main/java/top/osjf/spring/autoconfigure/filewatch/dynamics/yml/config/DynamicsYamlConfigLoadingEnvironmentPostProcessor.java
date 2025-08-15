/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.spring.autoconfigure.filewatch.dynamics.yml.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileUrlResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * The implementation {@code DynamicsYamlConfigLoadingEnvironmentPostProcessor} that
 * implement the loading of relevant custom configurations from the specified
 * configuration {@code file-watch.dynamics-yaml-loading.loading-conditions[%s].bind-path}
 * during the initial startup of the application to overwrite the packaging configuration
 * of the current application, ensuring consistency with the changes detected by
 * {@link DynamicsYamlConfigLoadingFileWatchListener} after project restart.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class DynamicsYamlConfigLoadingEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicsYamlConfigLoadingEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        postProcessEnvironmentInternal(environment);
    }

    private void postProcessEnvironmentInternal(ConfigurableEnvironment environment) {
        if (!environment.getProperty("file-watch.enable", boolean.class, false)) {
            return;
        }
        final String prefix = "file-watch.dynamics-yaml-loading.loading-conditions[%s].bind-path";
        int index = 0;
        List<String> bindPaths = new ArrayList<>();
        String bindPathRefer;
        do {
            bindPathRefer = environment.getProperty(String.format(prefix, index));
            if (bindPathRefer != null) {
                bindPaths.add(bindPathRefer);
                index++;
            }
        } while (bindPathRefer != null);
        if (bindPaths.isEmpty()) {
            return;
        }
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        for (String bindPath : bindPaths) {
            for (File file :
                    Optional.ofNullable(new File(bindPath)
                                    .listFiles(file -> ConfigLoadingCondition.isYamlFile(file.getName())))
                            .map(Arrays::asList).orElse(Collections.emptyList())) {
                try {
                    List<PropertySource<?>> propertySources
                            = loader.load(file.getName(), new FileUrlResource(file.toURI().toURL()));
                    if (!propertySources.isEmpty()) {
                        for (PropertySource<?> propertySource : propertySources) {
                            mutablePropertySources.addFirst(propertySource);
                        }
                    }
                }
                catch (MalformedURLException ex) {
                    LOGGER.error("URL {} specification is not valid", bindPath, ex);
                }
                catch (IOException ex) {
                    LOGGER.error("Failed to load {}", file.getPath(), ex);
                }
            }
        }
    }
}
