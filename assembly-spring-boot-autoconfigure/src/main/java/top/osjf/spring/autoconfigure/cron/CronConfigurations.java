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

import org.springframework.util.Assert;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Mappings between {@link CronProperties.ClientType} and {@code @Configuration}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
final class CronConfigurations {

    private static final Map<CronProperties.ClientType, String> MAPPINGS;

    static {
        Map<CronProperties.ClientType, String> mappings = new EnumMap<>(CronProperties.ClientType.class);
        mappings.put(CronProperties.ClientType.SPRING_SCHEDULER, SpringSchedulerAutoConfiguration.class.getName());
        mappings.put(CronProperties.ClientType.HUTOOL, HutoolCronTaskAutoConfiguration.class.getName());
        mappings.put(CronProperties.ClientType.QUARTZ, QuartzCronTaskAutoConfiguration.class.getName());
        mappings.put(CronProperties.ClientType.CRON4J, Cron4jCronTaskAutoConfiguration.class.getName());
        MAPPINGS = Collections.unmodifiableMap(mappings);
    }

    private CronConfigurations() {
    }

    static String getConfigurationClass(CronProperties.ClientType cacheType) {
        String configurationClassName = MAPPINGS.get(cacheType);
        Assert.state(configurationClassName != null, () -> "Unknown cache type " + cacheType);
        return configurationClassName;
    }

    static CronProperties.ClientType getType(String configurationClassName) {
        for (Map.Entry<CronProperties.ClientType, String> entry : MAPPINGS.entrySet()) {
            if (entry.getValue().equals(configurationClassName)) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Unknown configuration class " + configurationClassName);
    }
}
