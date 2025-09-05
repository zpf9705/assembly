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

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.osjf.cron.datasource.driven.scheduled.excel.ExcelDatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.external.file.ExternalFileTaskElementLoader;

import java.util.function.Consumer;

/**
 * Commons {@link Configuration Configuration} for {@link ExcelDatasourceTaskElementsOperation}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@Configuration(proxyBeanMethods = false)
public abstract class AbstractExternalFileDatabaseDrivenScheduledConfigure implements EnvironmentAware {

    /**
     * {@link ExternalFileTaskElementLoader#setBaseDir(String)}
     */
    private String baseDir;

    /**
     * {@link ExternalFileTaskElementLoader#setConfigFileName(String)}
     */
    private String configFileName;

    @Override
    public void setEnvironment(Environment environment) {
        baseDir = environment.getProperty("spring.schedule.cron.scheduled-driven.external.base-dir");
        configFileName = environment.getProperty("spring.schedule.cron.scheduled-driven.external.config-file-name");
    }

    /**
     * Configure external configuration data source driven scheduling {@link ExternalFileTaskElementLoader}.
     * @param loader the External configuration data source loader.
     */
    protected void configureExternalFileTaskElementLoader(ExternalFileTaskElementLoader<?> loader) {
        notNullAccept(baseDir, loader::setBaseDir);
        notNullAccept(configFileName, loader::setConfigFileName);
    }

    static <T> void notNullAccept(T property, Consumer<T> consumer){
        if (property == null){
            return;
        }
        consumer.accept(property);
    }
}
