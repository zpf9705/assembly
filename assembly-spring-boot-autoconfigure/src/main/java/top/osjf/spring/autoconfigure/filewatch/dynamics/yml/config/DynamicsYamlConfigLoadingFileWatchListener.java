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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.lang.NonNull;
import org.springframework.util.SystemPropertyUtils;
import top.osjf.filewatch.AmapleWatchEvent;
import top.osjf.filewatch.AmpleFileWatchListener;
import top.osjf.filewatch.FileWatchException;
import top.osjf.filewatch.TriggerKind;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The implementation of this listener is used to dynamically listen for changes in YAML
 * configuration files in a specified folder and dynamically write/update them to
 * {@link ConfigurableEnvironment#getPropertySources()}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class DynamicsYamlConfigLoadingFileWatchListener extends AmpleFileWatchListener implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicsYamlConfigLoadingFileWatchListener.class);

    private ApplicationContext applicationContext;

    private ConfigurableEnvironment environment;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Environment env = applicationContext.getEnvironment();
        if (env instanceof ConfigurableEnvironment) {
            this.environment = (ConfigurableEnvironment) env;
        }
        else {
            throw new IllegalStateException("Not instanceof org.springframework.core.env.ConfigurableEnvironment" +
                    " by callback org.springframework.context.EnvironmentAware");
        }
    }

    @Override
    protected boolean supportsInternal(AmapleWatchEvent event) {
        return ConfigLoadingConditionUtils.isYamlFile(event.context().toString());
    }

    @Override
    protected void onWatchEventInternal(AmapleWatchEvent event) {
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        if (event.isHopeEvent(TriggerKind.ENTRY_DELETE)) {
            PropertySource<?> removedPropertySource = mutablePropertySources.remove(event.context().toString());
            if (removedPropertySource != null) {
                LOGGER.info("PropertySource [{}] has been removed", removedPropertySource.getName());
            }
            return;
        }
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        try {
            List<PropertySource<?>> propertySources = loader.load(event.context().toString(),
                    new FileUrlResource(event.getFullPath().toString()));
            if (propertySources.isEmpty()) {
                return;
            }
            List<String> updatePropertyNames = new ArrayList<>();

            // Iterate through all property sources.
            for (PropertySource<?> propertySource : propertySources) {
                OriginTrackedMapPropertySource mapPropertySource =
                        (OriginTrackedMapPropertySource) propertySource;
                Map<String, Object> source = mapPropertySource.getSource();

                // Check each configuration item.
                boolean canAdd = false;
                for (Map.Entry<String, Object> entry : source.entrySet()) {
                    String propertyName = entry.getKey();
                    Object propertyValue = ((OriginTrackedValue) entry.getValue()).getValue();
                    if (propertyValue != null) {
                        // Config change detection.
                        if (environment.containsProperty(propertyName)) {
                            String pvs = propertyValue.toString();
                            // Process placeholders.
                            if (pvs.startsWith(SystemPropertyUtils.PLACEHOLDER_PREFIX)
                            && pvs.endsWith(SystemPropertyUtils.PLACEHOLDER_SUFFIX)) {
                                propertyValue = environment.resolveRequiredPlaceholders(pvs);
                            }
                            Object oldPropertyValue = environment.getProperty(propertyName, propertyValue.getClass());
                            if (!Objects.equals(propertyValue, oldPropertyValue)) {
                                updatePropertyNames.add(propertyName);
                                LOGGER.info("[ORIGIN CONFIG] Detected a configuration change in the " +
                                                "configuration source file [{}]:\n" +
                                                "• {}: {} → {} (Trigger Mode：UPDATED)",
                                        event.getFullPath(), propertyName, oldPropertyValue, propertyValue);
                            }
                        }
                        else {
                            LOGGER.info("[ORIGIN CONFIG] Detected a configuration change in the " +
                                            "configuration source file [{}]:\n" +
                                            "• {}: {} → {} (Trigger Mode：CREATED)",
                                    event.getFullPath(), propertyName, "NULL", propertyValue);
                        }
                        canAdd = true;
                    }
                }
                if (canAdd) mutablePropertySources.addFirst(propertySource);
            }
            // Trigger dependency updates.
            applicationContext.getBean(DynamicsYamlConfigLoadingBeanPostProcessor.class)
                    .processInjection(updatePropertyNames);
        }
        catch (MalformedURLException ex) {
            LOGGER.error("[ORIGIN CONFIG] URL {} specification is not valid", event.getFullPath(), ex);
            throw new FileWatchException("URL " + event.getFullPath() + " specification is not valid", ex);
        }
        catch (IOException ex) {
            LOGGER.error("[ORIGIN CONFIG] Failed to load {}", event.context(), ex);
            throw new FileWatchException("Failed to load " + event.context(), ex);
        }
        catch (IllegalArgumentException | ConversionException | BeanCreationException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
