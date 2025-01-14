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


package top.osjf.sdk.http.spi;

import com.google.common.collect.Lists;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.http.client.HttpRequestOptions;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default impl for {@link HttpRequest}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class DefaultHttpRequest implements HttpRequest {
    private static final long serialVersionUID = -375300629962316312L;
    private final String url;
    private final top.osjf.sdk.http.HttpRequest<?> rawHttpRequest;
    private final HttpRequestOptions requestOptions;
    /**
     * Creates a new {@code DefaultHttpRequest} by given original http request
     * and access url and a {@code HttpRequestOptions}.
     *
     * @param rawHttpRequest input original http request instance.
     * @param url            the access url address of this {@code HttpRequest}.
     * @param requestOptions the request options for this {@code HttpRequest}.
     * @throws NullPointerException if input {@code HttpRequest} or {@code url} is {@literal null}.
     */
    public DefaultHttpRequest(top.osjf.sdk.http.HttpRequest<?> rawHttpRequest,
                              String url, @Nullable HttpRequestOptions requestOptions) {
        this.rawHttpRequest = rawHttpRequest;
        this.url = url;
        this.requestOptions = requestOptions != null ? requestOptions : HttpRequestOptions.DEFAULT_OPTIONS;
    }
    @Override @NotNull public String getUrl() {
        return url;
    }
    @Override @NotNull public String getMethodName() {
        return rawHttpRequest.matchSdkEnum().getRequestMethod().name();
    }
    @Override @Nullable public Object getBody() {
        return rawHttpRequest.getRequestParam();
    }
    @Override @Nullable public <T> T getBody(Class<T> requiredType) {
        return getBody(requiredType, null);
    }
    @Override @Nullable public <T> T getBody(Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
            throws ClassCastException {
        return convertValueToRequired(getBody(), requiredType, customConversionAfterFailed);
    }
    @Override @Nullable public Charset getCharset() {
        return rawHttpRequest.getCharset();
    }
    @Override public Map<String, Object> getHeaders() {
        return rawHttpRequest.getHeadMap();
    }
    @Override public <T> Map<String, T> getHeaders(Class<T> requiredType) {
        return getHeaders(requiredType, null);
    }
    @Override public <T> Map<String, T> getHeaders(Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
            throws ClassCastException {
        Map<String, Object> headMap = rawHttpRequest.getHeadMap();
        if (headMap == null) {
            return Collections.emptyMap();
        }
        return headMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> convertValueToRequired(headMap.get(entry.getKey()),
                                requiredType, customConversionAfterFailed)));
    }
    @Override public <T> Map<String, Collection<T>> getCollectionValueHeaders(Class<T> requiredType,
                                                                    Function<Object, T> customConversionAfterFailed)
            throws ClassCastException {
        Map<String, Object> headMap = rawHttpRequest.getHeadMap();
        if (headMap == null) {
            return Collections.emptyMap();
        }
        return headMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    Object value = entry.getValue();
                    if (value instanceof Collection) {
                        Collection<T> delegate = new ArrayList<>();
                        ((Collection<?>) value)
                                .forEach((Consumer<Object>) o -> delegate
                                        .add(convertValueToRequired(o, requiredType, customConversionAfterFailed)));
                        return delegate;
                    }
                    return Lists.newArrayList(convertValueToRequired(value, requiredType,
                            customConversionAfterFailed));
                }));
    }
    @Override @Nullable public String getHeader(String key) {
        return getHeader(key, String.class);
    }
    @Override @Nullable public <T> T getHeader(String key, Class<T> requiredType) {
        return getHeader(key, requiredType, null);
    }
    @Override @Nullable public <T> T getHeader(String key, Class<T> requiredType,
                           Function<Object, T> customConversionAfterFailed)
            throws ClassCastException {
        Map<String, Object> headMap = rawHttpRequest.getHeadMap();
        if (headMap == null) {
            return null;
        }
        return convertValueToRequired(headMap.get(key), requiredType, customConversionAfterFailed);
    }
    @Override @NotNull public HttpRequestOptions getOptions() {
        return requestOptions;
    }
    private <T> T convertValueToRequired(Object value,
                                         Class<T> requiredType,
                                         Function<Object, T> customConversionAfterFailed)
            throws ClassCastException {
        if (value == null) return null;
        try {
            return requiredType.cast(value);
        } catch (ClassCastException e) {
            if (customConversionAfterFailed != null) return customConversionAfterFailed.apply(value);
            throw e;
        }
    }
}
