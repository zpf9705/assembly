/*
 * Copyright 2023-2024 the original author or authors.
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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class DefaultCultivateHttpSdkEnum implements HttpSdkEnum {

    private String url;
    private final String version;
    private final HttpProtocol httpProtocol;
    private final HttpRequestMethod httpRequestMethod;
    private final String name;
    private final AtomicBoolean urlInit = new AtomicBoolean(false);

    public DefaultCultivateHttpSdkEnum(@NotNull String url,
                                       @Nullable String version,
                                       @Nullable String httpProtocol,
                                       @NotNull String httpRequestMethod,
                                       @NotNull String name) {
        this.url = url;
        this.version = version;
        this.httpProtocol = StringUtils.isBlank(httpProtocol) ||
                HttpProtocol.NULLS.name().equals(httpProtocol) ? null : HttpProtocol.valueOf(httpProtocol);
        this.httpRequestMethod = HttpRequestMethod.valueOf(httpRequestMethod);
        this.name = name;
    }

    @Override
    @NotNull
    public String getUrl(@Nullable String host) {
        if (!urlInit.get()) {
            if (url.contains("%s")) {
                if (StringUtils.isNotBlank(version)) {
                    url = String.format(url, host, version);
                } else {
                    url = String.format(url, host);
                }
            }
            urlInit.set(true);
        }
        return url;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    @NotNull
    public HttpRequestMethod getRequestMethod() {
        return httpRequestMethod;
    }

    @Nullable
    @Override
    public HttpProtocol getProtocol() {
        return httpProtocol;
    }
}
