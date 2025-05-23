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

package top.osjf.cron.spring.datasource.driven.scheduled;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.yaml.snakeyaml.Yaml;
import top.osjf.cron.datasource.driven.scheduled.yaml.YamlDatasourceTaskElementsOperation;

/**
 * {@link Configuration Configuration} for {@link YamDatabaseDrivenScheduledConfiguration}.
 *
 * <p>If the entry for {@code spring.schedule.cron.datasource.driven.yml.name} is not configured,
 * {@code task-config.yml} will be used as the default configuration file name.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Configuration(proxyBeanMethods = false)
public class YamDatabaseDrivenScheduledConfiguration {

    @Bean
    public YamlDatasourceTaskElementsOperation yamlDatasourceTaskElementsOperation(ObjectProvider<Yaml> provider,
                                                                                   Environment environment) {
        String configYamlFileName
                = environment.getProperty("spring.schedule.cron.datasource.driven.yml-config-name",
                "task-config.yml");
        YamlDatasourceTaskElementsOperation operation = new YamlDatasourceTaskElementsOperation(configYamlFileName);
        provider.orderedStream().findFirst().ifPresent(operation::setYaml);
        return operation;
    }
}
