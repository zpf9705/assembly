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

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.util.ArrayUtils;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.mp.MybatisPlusDatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.yaml.YamlDatasourceTaskElementsOperation;
import top.osjf.cron.spring.annotation.DatabaseDrivenScheduledConfiguration;
import top.osjf.cron.spring.datasource.driven.scheduled.MybatisPlusDatabaseDrivenScheduledConfiguration;
import top.osjf.cron.spring.datasource.driven.scheduled.SpringDatasourceDrivenScheduled;
import top.osjf.cron.spring.datasource.driven.scheduled.YamDatabaseDrivenScheduledConfiguration;

import java.lang.annotation.*;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link SpringDatasourceDrivenScheduled}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Configuration(proxyBeanMethods = false)
@Import(DatabaseDrivenScheduledConfiguration.class)
@DatasourceDrivenScheduledAutoConfiguration.ConditionalOnDrivenScheduledConfigurablePropertyProfiles
public class DatasourceDrivenScheduledAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Import(MybatisPlusDatabaseDrivenScheduledConfiguration.class)
    @ConditionalOnClass(MybatisPlusDatasourceTaskElementsOperation.class)
    @ConditionalOnProperty(prefix = "spring.schedule.cron", name = "scheduledDrivenDataSource",
            havingValue = "my_batis_plus_orm_database")
    @ConditionalOnMissingBean(DatasourceTaskElementsOperation.class)
    public static class MybatisPlusDatabaseDrivenScheduledAutoConfiguration {
    }

    @Configuration(proxyBeanMethods = false)
    @Import(YamDatabaseDrivenScheduledConfiguration.class)
    @ConditionalOnClass(YamlDatasourceTaskElementsOperation.class)
    @ConditionalOnProperty(prefix = "spring.schedule.cron", name = "scheduledDrivenDataSource",
            havingValue = "yaml_config")
    public static class YamDatabaseDrivenScheduledAutoConfiguration {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Documented
    @Conditional(OnPropertyProfilesCondition.class)
    public @interface ConditionalOnDrivenScheduledConfigurablePropertyProfiles {

        String PROPERTY_NAME_OF_MATCHED_PROFILES = "spring.schedule.cron.datasource.driven.active-profiles.matched";
    }

    public static class OnPropertyProfilesCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, @NotNull AnnotatedTypeMetadata metadata) {
            Environment environment = context.getEnvironment();
            String[] matchedProfiles = environment
                    .getProperty(ConditionalOnDrivenScheduledConfigurablePropertyProfiles
                            .PROPERTY_NAME_OF_MATCHED_PROFILES, String[].class);
            if (ArrayUtils.isEmpty(matchedProfiles)) {
                return true;
            }
            return environment.acceptsProfiles(Profiles.of(matchedProfiles));
        }
    }
}
