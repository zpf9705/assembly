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


package top.osjf.cron.datasource.driven.scheduled.nacosconfig;

import top.osjf.cron.core.lang.NotNull;

import java.util.List;

/**
 * Default HTML-formatted Nacos configuration task element serializer.
 *
 * <p>In the current business scenario, the data source information of scheduled tasks is stored in Nacos Config
 * and needs to be fetched remotely and deserialized for use. However, HTML format is primarily designed for
 * front-end {@code JavaScript} rendering and display purposes. It is not suitable for back-end service
 * configuration due to issues such as poor readability, risk of code injection, and difficulty in maintenance.
 *
 * <p>Therefore, this implementation does not support serialization and deserialization operations.
 * It only serves to identify the configuration format as HTML, returning the corresponding enum via
 * {@link #getConfigFormat}.Actual serialization/deserialization should be handled by other formats (e.g.,
 * JSON, YAML).
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultHtmlNacosConfigTaskElementSerializer implements NacosConfigTaskElementSerializer {

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException Operation not supported.
     */
    @Override
    public String serialize(@NotNull List<NacosConfigTaskElement> elements) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException Operation not supported.
     */
    @Override
    public List<NacosConfigTaskElement> deserialize(@NotNull String configInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.HTML;
    }
}
