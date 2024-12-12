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

package top.osjf.sdk.http;

import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.process.HttpSdkEnum;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The default HTTP SDK enumeration class implements the {@code HttpSdkEnum}
 * interface and serves as an annotation {@link HttpSdkEnumCultivate} to as
 * a {@code HttpSdkEnum}
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class DefaultCultivateHttpSdkEnum implements HttpSdkEnum {

    private String url;
    /**
     * The host name formatted for the current URL.
     */
    private String currentHost;
    private final HttpProtocol httpProtocol;
    private final HttpRequestMethod httpRequestMethod;
    private final String name;
    /**
     * Initialize to ensure obtaining a fair lock for the URL.
     */
    private final Lock lock = new ReentrantLock(true);

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
                                       @Nullable HttpProtocol httpProtocol,
                                       @NotNull HttpRequestMethod httpRequestMethod,
                                       @NotNull String name) {
        this.url = formatUrl(url, version);
        this.httpProtocol = httpProtocol == null ||
                HttpProtocol.NULLS.equals(httpProtocol) ? null : httpProtocol;
        this.httpRequestMethod = httpRequestMethod;
        this.name = name;
    }

    /**
     * Format the URL, and when it contains string formatting symbols,
     * perform logical initialization based on the quantity and whether
     * the version number is empty.
     *
     * @param url     input url.
     * @param version input sdk version.
     * @return format url.
     */
    String formatUrl(String url, String version) {
        if (url.contains("%s")) {
            if (url.indexOf("%s") != url.lastIndexOf("%s") && StringUtils.isNotBlank(version)) {
                return String.format(url, "%s", version);
            }
        }
        return url;
    }

    /**
     * {@inheritDoc}.
     * Thread safe URL retrieval involves formatting the host and URL.
     * <p>
     * After initializing the URL for the first time, it is necessary
     * to check if any subsequent hosts have changed.
     * <p>
     * If there are any changes, the URL should be reinitialized, otherwise
     * it will continue to be used.
     *
     * @param host {@inheritDoc}.
     * @return {@inheritDoc}.
     */
    @Override
    @NotNull
    public String getUrl(@Nullable String host) {
        lock.lock();
        try {
            return getUrlInternal(host);
        } finally {
            lock.unlock();
        }
    }

    String getUrlInternal(@Nullable String host) {
        if (StringUtils.isBlank(host)) return url;
        if (StringUtils.isBlank(currentHost) || !Objects.equals(host, currentHost)) {
            currentHost = host;
            url = String.format(url, currentHost);
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
