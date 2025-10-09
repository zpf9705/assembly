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

package top.osjf.spring.autoconfigure.cron;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.Crones;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CronProperties.class)
@Import(CronTaskAutoConfiguration.CronConfigurationImportSelector.class)
public class CronTaskAutoConfiguration {

    /**
     * If {@link org.springframework.boot.SpringApplication#setLazyInitialization} is set
     * to {@code true}, then this {@link LazyInitializationExcludeFilter} will filter out
     * tasks, annotations {@link Cron} and {@link Crones}, and beans without lazy loading.
     * @return {@code LazyInitializationExcludeFilter} for annotations {@link Cron} and
     * {@link Crones}.
     */
    @Bean
    public static LazyInitializationExcludeFilter cronBeanLazyInitializationExcludeFilter() {
        return new CronBeanLazyInitializationExcludeFilter();
    }

    @Bean
    public CronClientValidator cronTaskAutoConfigurationValidator(CronProperties cronProperties,
                                                                  ObjectProvider<CronTaskRepository> cronTaskRepositories) {
        return new CronClientValidator(cronProperties, cronTaskRepositories);
    }

    @Bean
    public CronTaskRepositorySmartLifecycle cronTaskRepositorySmartLifecycle(CronTaskRepository cronTaskRepository) {
        return new CronTaskRepositorySmartLifecycle(cronTaskRepository);
    }

    /**
     * Bean used to validate that a {@link CronTaskRepository} exists and provide a more meaningful
     * exception.
     * @since 1.0.4
     */
    static class CronClientValidator implements InitializingBean {

        private final CronProperties cronProperties;

        private final ObjectProvider<CronTaskRepository> cronTaskRepositories;

        public CronClientValidator(CronProperties cronProperties,
                                   ObjectProvider<CronTaskRepository> cronTaskRepositories) {
            this.cronProperties = cronProperties;
            this.cronTaskRepositories = cronTaskRepositories;
        }

        @Override
        public void afterPropertiesSet() {
            Assert.notNull(this.cronTaskRepositories.getIfAvailable(),
                    () -> "No cron task repository could be auto-configured, check your configuration (client type is '"
                            + this.cronProperties.getClientType() + "')");
        }

    }

    /**
     * {@link ImportSelector} to add {@link ClientType} configuration classes.
     * @since 1.0.4
     */
    static class CronConfigurationImportSelector implements ImportSelector {

        @Override
        @NotNull
        public String[] selectImports(@NotNull AnnotationMetadata importingClassMetadata) {
            ClientType[] types = ClientType.values();
            String[] imports = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                imports[i] = CronConfigurations.getConfigurationClass(types[i]);
            }
            return imports;
        }

    }

    /**
     * {@link CronTaskRepository}'s intelligent lifecycle manager.
     * @since 3.0.1
     */
    static class CronTaskRepositorySmartLifecycle implements SmartLifecycle {

        private final CronTaskRepository cronTaskRepository;

        public CronTaskRepositorySmartLifecycle(CronTaskRepository cronTaskRepository) {
            this.cronTaskRepository = cronTaskRepository;
        }

        @Override
        public void start() {
            cronTaskRepository.start();
        }

        @Override
        public void stop() {
            cronTaskRepository.stop();
        }

        @Override
        public boolean isRunning() {
            return cronTaskRepository.isStarted();
        }
    }
}
