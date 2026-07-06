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


package top.osjf.cron.spring.datasource.driven.scheduled;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.osjf.cron.datasource.driven.scheduled.nacosconfig.NacosConfigDatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigFormat;

/**
 * {@link Configuration Configuration} for {@link NacosConfigDatasourceTaskElementsOperation}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@Configuration(proxyBeanMethods = false)
public class NacosConfigDatabaseDrivenScheduledConfiguration {

    public static final String PREFIX = "spring.schedule.cron.scheduled-driven.nacos-config";
    public static final String KEY_NACOS_SERVER_ADDR = PREFIX + ".server-addr";
    public static final String KEY_NACOS_GROUP_ID = PREFIX + ".group-id";
    public static final String KEY_NACOS_DATA_ID = PREFIX + ".data-id";
    public static final String KEY_NACOS_CONFIG_FORMAT = PREFIX + ".config-format";

    @Bean
    public NacosConfigDatasourceTaskElementsOperation nacosConfigDatasourceTaskElementsOperation
            (Environment environment) {
        String serverAddr = environment.getProperty(KEY_NACOS_SERVER_ADDR, "localhost:8848");
        String groupId = environment.getProperty(KEY_NACOS_GROUP_ID, "DEFAULT_GROUP");
        String dataId = environment.getProperty(KEY_NACOS_DATA_ID);
        SubstituteConfigFormat configFormat = environment
                .getProperty(KEY_NACOS_CONFIG_FORMAT, SubstituteConfigFormat.class, SubstituteConfigFormat.JSON);
        return new NacosConfigDatasourceTaskElementsOperation(serverAddr, groupId,
                dataId, ConfigFormat.valueOf(configFormat.name()));
    }
}
