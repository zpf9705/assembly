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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.util.StringUtils;
import top.osjf.cron.datasource.driven.scheduled.*;
import top.osjf.cron.spring.annotation.DatasourceDrivenScheduledConfiguration;
import top.osjf.cron.spring.datasource.driven.scheduled.DataSource;
import top.osjf.cron.spring.datasource.driven.scheduled.SpringDatasourceDrivenScheduled;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link SpringDatasourceDrivenScheduled}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CronDatasourceDrivenProperties.class)
@ConditionalOnProperty(prefix = "spring.schedule.cron", name = "scheduled-driven.enable", havingValue = "true")
public class DatasourceDrivenScheduledAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Import({DatasourceDrivenAutoConfigurationImportSelector.class, DatasourceDrivenScheduledConfiguration.class})
    @EnableDatasourceScheduledProfiles
    static class DatasourceDrivenScheduledProfilesMatchedImportConfiguration {
    }

    /**
     * @return A default no operation {@link DatasourceTaskElementsOperation}.
     * @since 3.0.1
     */
    @Bean
    @ConditionalOnMissingBean(DatasourceTaskElementsOperation.class)
    public NoOpDatasourceTaskElementsOperation noOpDatasourceTaskElementsOperation() {
        return new NoOpDatasourceTaskElementsOperation();
    }

    @Bean
    @ConditionalOnMissingBean(DataSourceConfigLoader.class)
    @ConditionalOnBean(javax.sql.DataSource.class)
    @ConditionalOnProperty
            (prefix = "spring.schedule.cron.scheduled-driven.config-loader.javax-datasource", name = "query-config-qql")
    public DataSourceConfigLoader dataSourceConfigLoader(javax.sql.DataSource dataSource,
                                                         CronDatasourceDrivenProperties properties) {
        CronDatasourceDrivenProperties.ConfigLoader.JavaxDatasource javaxDatasource
                = properties.getConfigLoader().getJavaxDatasource();
        JdkDataSourceConfigLoader loader =
                new DefaultJdkDataSourceConfigLoader(dataSource, javaxDatasource.getQueryConfigSql());
        String configValueColumnName = javaxDatasource.getConfigValueColumnName();
        if (StringUtils.isNotBlank(configValueColumnName)) {
            loader.setConfigValueColumnName(configValueColumnName);
        }
        return loader;
    }

    /**
     * {@link ImportSelector} to add {@link DataSource} auto configuration classes.
     * @since 3.0.2
     */
    static class DatasourceDrivenAutoConfigurationImportSelector implements ImportSelector {
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
