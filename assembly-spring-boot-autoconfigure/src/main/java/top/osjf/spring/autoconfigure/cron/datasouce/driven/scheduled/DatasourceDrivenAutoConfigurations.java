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


package top.osjf.spring.autoconfigure.cron.datasouce.driven.scheduled;

import org.springframework.util.Assert;
import top.osjf.cron.spring.datasource.driven.scheduled.DataSource;
import top.osjf.spring.autoconfigure.cron.QuartzCronTaskAutoConfiguration;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
final class DatasourceDrivenAutoConfigurations {

    private static final Map<DataSource, String> MAPPINGS;

    static {
        Map<DataSource, String> mappings = new EnumMap<>(DataSource.class);
        mappings.put(DataSource.YAML_CONFIG,
                YamlConfigDatasourceTaskElementsOperationAutoConfiguration.class.getName());
        mappings.put(DataSource.MY_BATIS_PLUS_ORM_DATABASE,
                MybatisPlusDatasourceTaskElementsOperationAutoConfiguration.class.getName());
        mappings.put(DataSource.SPRING_JPA_ORM_DATABASE, QuartzCronTaskAutoConfiguration.class.getName());
        MAPPINGS = Collections.unmodifiableMap(mappings);
    }

    private DatasourceDrivenAutoConfigurations() {
    }

    static String getConfigurationClass(DataSource dataSource) {
        String configurationClassName = MAPPINGS.get(dataSource);
        Assert.state(configurationClassName != null, () -> "Unknown dataSource type " + dataSource);
        return configurationClassName;
    }

    static DataSource getType(String configurationClassName) {
        for (Map.Entry<DataSource, String> entry : MAPPINGS.entrySet()) {
            if (entry.getValue().equals(configurationClassName)) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Unknown configuration class " + configurationClassName);
    }
}
