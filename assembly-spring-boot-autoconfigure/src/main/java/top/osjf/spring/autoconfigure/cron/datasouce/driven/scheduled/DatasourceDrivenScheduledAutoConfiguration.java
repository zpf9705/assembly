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


package top.osjf.spring.autoconfigure.cron.datasouce.driven.scheduled;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.NoOpDatasourceTaskElementsOperation;
import top.osjf.cron.spring.annotation.DatabaseDrivenScheduledConfiguration;
import top.osjf.cron.spring.datasource.driven.scheduled.DataSource;
import top.osjf.cron.spring.datasource.driven.scheduled.SpringDatasourceDrivenScheduled;
import top.osjf.spring.autoconfigure.ConditionalOnPropertyProfiles;
import top.osjf.spring.autoconfigure.cron.ClientType;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link SpringDatasourceDrivenScheduled}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.schedule.cron", name = "scheduled-driven.enable", havingValue = "true")
public class DatasourceDrivenScheduledAutoConfiguration {

    public static final String PROPERTY_NAME_OF_MATCHED_PROFILES
            = "spring.schedule.cron.datasource.driven.active-profiles.matched";

    @Configuration(proxyBeanMethods = false)
    @Import({DatasourceDrivenConfigurationImportSelector.class, DatabaseDrivenScheduledConfiguration.class})
    @ConditionalOnPropertyProfiles(propertyName = PROPERTY_NAME_OF_MATCHED_PROFILES)
    public static class DatasourceDrivenScheduledSelectiveAutoConfiguration {
    }

    @Bean
    @ConditionalOnMissingBean(DatasourceTaskElementsOperation.class)
    public NoOpDatasourceTaskElementsOperation noOpDatasourceTaskElementsOperation() {
        return new NoOpDatasourceTaskElementsOperation();
    }

    /**
     * {@link ImportSelector} to add {@link ClientType} configuration classes.
     * @since 1.0.4
     */
    public static class DatasourceDrivenConfigurationImportSelector implements ImportSelector {
        @Override
        @NotNull
        public String[] selectImports(@NotNull AnnotationMetadata importingClassMetadata) {
            DataSource[] types = DataSource.values();
            String[] imports = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                imports[i] = DatasourceDrivenAutoConfigurations.getConfigurationClass(types[i]);
            }
            return imports;
        }
    }

}
