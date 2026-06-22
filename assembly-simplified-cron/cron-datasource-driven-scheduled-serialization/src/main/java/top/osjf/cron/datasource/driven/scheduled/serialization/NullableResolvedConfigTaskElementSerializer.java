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

import top.osjf.commons.util.CollectionUtils;
import top.osjf.commons.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * A pre serializer abstract class that preprocesses {@literal null} values in advance. It preprocesses
 * {@literal null} values in {@link #serialize(List)} and {@link #deserialize(String)} and returns the
 * default value provided by the {@literal null} value. When the null value detection passes, it is handed
 * over to subclasses to complete the internal processing of serializing {@link #serializeInternal(List)}
 * and deserializing {@link #deserializeInternal(String)}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class NullableResolvedConfigTaskElementSerializer implements ConfigTaskElementSerializer {

    private static final String SERIALIZE_DEF = "";

    @Override
    public final String serialize(List<ConfigurableTaskElement> elements) throws IOException {
        if (CollectionUtils.isEmpty(elements)) {
            return SERIALIZE_DEF;
        }
        return serializeInternal(elements);
    }

    @Override
    public final List<ConfigurableTaskElement> deserialize(String configInfo) throws IOException {
        if (StringUtils.isBlank(configInfo)) {
            return Collections.emptyList();
        }
        return deserializeInternal(configInfo);
    }

    /**
     * Internal method of {@link #serialize(List)} after {@literal Null} check.
     * @param elements {@link #serialize(List)}
     * @return         {@link #serialize(List)}
     * @throws IOException see {@link #serialize(List)}
     */
    protected abstract String serializeInternal(List<ConfigurableTaskElement> elements)
            throws IOException;

    /**
     * Internal method of {@link #deserialize(String)} after {@literal Null} check.
     * @param configInfo {@link #deserialize(String)}
     * @return           {@link #deserialize(String)}
     * @throws IOException see  {@link #deserialize(String)}
     */
    protected abstract List<ConfigurableTaskElement> deserializeInternal(String configInfo)
            throws IOException;
}
