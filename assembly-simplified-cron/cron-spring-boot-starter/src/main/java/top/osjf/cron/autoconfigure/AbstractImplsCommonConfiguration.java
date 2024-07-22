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

package top.osjf.cron.autoconfigure;

import org.apache.commons.collections4.MapUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import top.osjf.cron.core.annotation.NotNull;

import java.util.Map;

/**
 * The common configuration class for timed task components.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractImplsCommonConfiguration implements EnvironmentAware, EnvironmentCapable {

    private Environment environment;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    @NotNull
    public Environment getEnvironment() {
        return environment;
    }

    @Bean
    public ConfiguredCronTaskRegisterPostProcessor cronTaskRegisterPostProcessor() {
        Map<String, Object> metadata = getMetadata();
        if (MapUtils.isEmpty(metadata)) return new ConfiguredCronTaskRegisterPostProcessor();
        return new ConfiguredCronTaskRegisterPostProcessor(metadata);
    }

    /**
     * Return metadata information of the post processor for scheduled tasks.
     *
     * @return metadata information.
     */
    public Map<String, Object> getMetadata() {
        return null;
    }
}
