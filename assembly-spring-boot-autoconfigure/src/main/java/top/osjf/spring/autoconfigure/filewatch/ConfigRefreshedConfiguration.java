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


package top.osjf.spring.autoconfigure.filewatch;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import top.osjf.filewatch.FileWatchPath;
import top.osjf.filewatch.spring.config.refresh.ConfigRefreshedFileWatchListener;
import top.osjf.filewatch.spring.config.refresh.ValueAnnotationBeanBeanPostProcessor;

import java.util.List;

/**
 * {@link Configuration Configuration} for dynamically refreshing application configuration
 * through external configuration files.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ConfigRefreshedFileWatchListener.class, ValueAnnotationBeanBeanPostProcessor.class })
@Import(ConfigRefreshedConfiguration.ConfigRefreshedInternalConfiguration.class)
class ConfigRefreshedConfiguration {

    @Bean("dynamicsYamlLoadingFileWatchServiceCustomizer")
    public FileWatchServiceCustomizer fileWatchServiceCustomizer(FileWatchProperties fileWatchProperties,
                                                                 ApplicationContext applicationContext) {
        return fileWatchService -> {
            List<FileWatchPath> fileWatchPaths = fileWatchProperties.getFileWatchPaths();
            if (!fileWatchPaths.isEmpty()) {
                ConfigRefreshedFileWatchListener listener = new ConfigRefreshedFileWatchListener();
                listener.setApplicationContext(applicationContext);
                fileWatchService.registerListener(listener);
            }
        };
    }

    @Configuration(proxyBeanMethods = false)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static class ConfigRefreshedInternalConfiguration {

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public ValueAnnotationBeanBeanPostProcessor valueAnnotationBeanBeanPostProcessor() {
            return new ValueAnnotationBeanBeanPostProcessor();
        }
    }
}
