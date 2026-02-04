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


package top.osjf.sdk.http.spi;

import top.osjf.sdk.core.lang.Nullable;

/**
 * The transmission protocol simply wraps objects and supports custom conversion
 * of {@link top.osjf.sdk.core.Wrapper}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class WrapperedProtocolVersion implements ProtocolVersion {

    @Nullable private final Object protocolVersion;

    public WrapperedProtocolVersion(@Nullable Object protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public boolean isWrapperFor(Class<?> clazz) {
        if (protocolVersion == null) return false;
        return clazz.isInstance(protocolVersion);
    }

    @Nullable
    @Override
    public <T> T unwrap(Class<T> clazz) {
        return protocolVersion != null ? clazz.cast(protocolVersion) : null;
    }
}
