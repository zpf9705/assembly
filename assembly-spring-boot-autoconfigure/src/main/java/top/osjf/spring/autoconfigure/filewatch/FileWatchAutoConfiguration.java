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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.osjf.filewatch.FileWatchPath;
import top.osjf.filewatch.FileWatchService;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@code FileWatchService}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(FileWatchService.class)
@EnableConfigurationProperties(FileWatchProperties.class)
@ConditionalOnProperty(prefix = "file-watch", name = "enable", havingValue = "true")
@Import({ ApplicationStartupFileWatchConfiguration.class })
public class FileWatchAutoConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public FileWatchService fileWatchService(FileWatchProperties fileWatchProperties,
                                             ObjectProvider<FileWatchServiceCustomizer> customizerProvider) {
        FileWatchService fileWatchService = new FileWatchService();

        // Register fileWatchPath .
        for (FileWatchPath fileWatchPath : fileWatchProperties.getFileWatchPaths()) {
            fileWatchService.registerWatch(fileWatchPath);
        }

        // Any customize beans .
        customizerProvider.orderedStream().forEach(c -> c.customize(fileWatchService));

        return fileWatchService;
    }

    @Bean
    public FileWatchServiceLifecycle fileWatchServiceLifecycle(FileWatchService fileWatchService) {
        return new FileWatchServiceLifecycle(fileWatchService);
    }
}
