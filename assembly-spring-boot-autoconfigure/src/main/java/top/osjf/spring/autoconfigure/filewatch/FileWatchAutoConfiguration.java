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


package top.osjf.spring.autoconfigure.filewatch;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.osjf.filewatch.FileWatchListener;
import top.osjf.filewatch.FileWatchService;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@code FileWatchService}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@ConditionalOnClass(FileWatchService.class)
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FileWatchProperties.class)
public class FileWatchAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FileWatchService fileWatchService(ObjectProvider<FileWatchListener> provider,
                                             FileWatchProperties fileWatchProperties) {
        FileWatchService fileWatchService = new FileWatchService();
        provider.orderedStream().forEach(fileWatchService::registerListener);
        fileWatchService.registerWatches(fileWatchProperties.getPaths());
        return fileWatchService;
    }

    @Bean
    public FileWatchServiceLifecycle fileWatchServiceLifecycle(FileWatchService fileWatchService) {
        return new FileWatchServiceLifecycle(fileWatchService);
    }
}
