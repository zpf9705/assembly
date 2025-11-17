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


package top.osjf.cron.datasource.driven.scheduled.serialization;

import top.osjf.cron.core.lang.NotNull;

import java.util.List;

/**
 * Default HTML-formatted scheduled task element serializer.
 *
 * <p>In generic scheduling systems, task configuration data may support multiple formats for storage
 * and display.The HTML format is primarily intended for front-end rendering and visualization, such
 * as embedding task information directly into management console pages. However, due to its verbose
 * tags, risk of script injection, complex structure, and poor permeability, it is not suitable for
 * backend service communication or persistent metadata storage.
 *
 * <p>Therefore, this implementation does not provide actual serialization or deserialization logic.
 * It only serves to identify the configuration format as HTML via {@link #getConfigFormat}. Systems
 * should reject or warn against using HTML for task configuration persistence. Structured formats
 * like JSON or YAML are recommended instead.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultHtmlConfigTaskElementSerializer implements ConfigTaskElementSerializer {

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException Operation not supported.
     */
    @Override
    public String serialize(@NotNull List<ConfigurableTaskElement> elements) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException Operation not supported.
     */
    @Override
    public List<ConfigurableTaskElement> deserialize(@NotNull String configInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.HTML;
    }
}
