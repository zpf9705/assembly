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


package top.osjf.cron.spring.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.spring.datasource.driven.scheduled.*;

/**
 * {@link Configuration Configuration} for {@link SpringDatasourceDrivenScheduled}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Configuration(proxyBeanMethods = false)
public class DatabaseDrivenScheduledConfiguration implements ImportSelector {

    @Override
    @NotNull
    public String[] selectImports(AnnotationMetadata metadata) {
        AnnotationAttributes attributes
                = AnnotationAttributes
                .fromMap(metadata.getAnnotationAttributes(EnableDataSourceDrivenScheduled.class.getCanonicalName()));
        if (attributes != null){
            DataSource dataSource = attributes.getEnum("value");
            switch (dataSource){
                case MY_BATIS_PLUS_ORM_DATABASE:
                    return new String[]{MybatisPlusDatabaseDrivenScheduledConfiguration.class.getCanonicalName()};
                case YAML_CONFIG:
                    return new String[]{YamDatabaseDrivenScheduledConfiguration.class.getCanonicalName()};
            }
        }

        return new String[0];
    }

    @Bean
    public SpringDatasourceDrivenScheduled springDatasourceDrivenScheduled
            (CronTaskRepository cronTaskRepository, DatasourceTaskElementsOperation datasourceTaskElementsOperation) {
        return new SpringHandlerMappingDatasourceDrivenScheduled(cronTaskRepository, datasourceTaskElementsOperation);
    }
}
