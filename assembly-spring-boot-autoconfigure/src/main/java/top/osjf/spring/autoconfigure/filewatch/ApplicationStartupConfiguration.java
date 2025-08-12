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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.osjf.filewatch.application.startup.ApplicationStartupFileWatchListener;
import top.osjf.filewatch.application.startup.StartupJarElement;

/**
 * {@link Configuration Configuration} for file watch application startup.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ApplicationStartupFileWatchListener.class })
@ConditionalOnProperty(prefix = "file-watch.application-startup", name = "elements")
public class ApplicationStartupConfiguration {

    @Bean("applicationStartupFileWatchListener")
    public ApplicationStartupFileWatchListener watchListener(FileWatchProperties fileWatchProperties) {
        return new ApplicationStartupFileWatchListener(fileWatchProperties.getApplicationStartup().getElements());
    }

    @Bean("registerWaitConfigurationCustomizer")
    public FileWatchServiceCustomizer fileWatchServiceCustomizer(FileWatchProperties fileWatchProperties) {
        return fileWatchService -> {
            for (StartupJarElement element : fileWatchProperties.getApplicationStartup().getElements()) {
                fileWatchService.registerWaitConfiguration(element);
            }
        };
    }
}
