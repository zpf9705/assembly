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

package top.osjf.sdk.http.annotation;

import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.HttpProtocol;
import top.osjf.sdk.http.HttpRequestMethod;
import top.osjf.sdk.http.process.HttpSdkEnum;

/**
 * Extends for {@code top.osjf.sdk.http.DefaultCultivateHttpSdkEnum} class implements
 * the {@code HttpSdkEnum} interface and serves as an annotation {@link HttpSdkEnumCultivate}
 * to as a {@code HttpSdkEnum}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class DefaultCultivateHttpSdkEnum extends top.osjf.sdk.http.DefaultCultivateHttpSdkEnum {

    /**
     * Create a {@code DefaultCultivateHttpSdkEnum} using the {@code url}
     * {@code version} {@code httpProtocol} {@code httpRequestMethod} {@code name}
     * construction method.
     *
     * @param url               {@link HttpSdkEnum#getUrl}
     * @param version           Defined SDK version.
     * @param httpProtocol      {@link HttpSdkEnum#getProtocol}
     * @param httpRequestMethod {@link HttpSdkEnum#getRequestMethod}
     * @param name              {@link HttpSdkEnum#name()}
     * @throws NullPointerException If the input url or httpRequestMethod
     *                              or name is {@literal null}.
     */
    public DefaultCultivateHttpSdkEnum(@NotNull String url,
                                       @Nullable String version,
                                       @Nullable String httpProtocol,
                                       @NotNull String httpRequestMethod,
                                       @NotNull String name) {
        super(url,
                version,
                StringUtils.isBlank(httpProtocol) ||
                        HttpProtocol.NULLS.name().equals(httpProtocol) ? null : HttpProtocol.valueOf(httpProtocol),
                HttpRequestMethod.valueOf(httpRequestMethod),
                name);
    }
}
