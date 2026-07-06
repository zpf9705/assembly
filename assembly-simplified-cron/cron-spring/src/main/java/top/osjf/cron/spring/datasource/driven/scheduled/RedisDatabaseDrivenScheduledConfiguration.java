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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.osjf.cron.datasource.driven.scheduled.redis.RedisDatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.redis.config.RedisConnectionConfig;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigFormat;
import top.osjf.cron.spring.Utils;

/**
 * {@link Configuration Configuration} for {@link RedisDatasourceTaskElementsOperation}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@Configuration(proxyBeanMethods = false)
public class RedisDatabaseDrivenScheduledConfiguration {

    public static final String PREFIX = "spring.schedule.cron.scheduled-driven.redis";
    public static final String KEY_RULE_KEY = PREFIX + ".rule-key";
    public static final String KEY_CHANNEL = PREFIX + ".channel";
    public static final String KEY_CONFIG_FORMAT = PREFIX + ".config-format";

    @Bean
    public RedisDatasourceTaskElementsOperation redisDatasourceTaskElementsOperation
            (ObjectProvider<RedisConnectionConfig> builders, Environment environment) {
        RedisConnectionConfig config = Utils.getHighestPriorityMatchingInstance(builders);
        if (config == null) {
            config = RedisConnectionConfig.builder().build();
        }
        String ruleKey = environment.getProperty(KEY_RULE_KEY);
        String channel = environment.getProperty(KEY_CHANNEL);
        SubstituteConfigFormat configFormat = environment
                .getProperty(KEY_CONFIG_FORMAT, SubstituteConfigFormat.class, SubstituteConfigFormat.JSON);
        return new RedisDatasourceTaskElementsOperation(config, ruleKey,
                channel, ConfigFormat.valueOf(configFormat.name()));
    }

}
