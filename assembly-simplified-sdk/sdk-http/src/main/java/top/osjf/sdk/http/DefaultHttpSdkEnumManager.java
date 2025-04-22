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

import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.util.org.hibernate.validator.internal.util.v6_2_0_final.ConcurrentReferenceHashMap;

/**
 * Default implementation of the {@code HttpSdkEnum} manager for managing {@code HttpSdkEnum}
 * objects of a specific name.
 * <p>
 * Uses {@code ConcurrentReferenceHashMap} as a cache to store client objects, supporting
 * thread safety and soft references.
 *
 * @param <R> Subclass generic type of {@code HttpResponse}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class DefaultHttpSdkEnumManager<R extends HttpResponse> implements HttpSdkEnumManager<R> {

    /**
     * Cache for client objects, implemented using {@code ConcurrentReferenceHashMap},
     * supporting thread safety and {@link java.lang.ref.SoftReference} references.
     * <p>
     * The key is {@link Class#getName()} of the {@code HttpRequest}, and the value is the
     * corresponding {@code HttpSdkEnum} object.
     */
    private final ConcurrentReferenceHashMap<String, HttpSdkEnum> SDK_ENUM_CACHE =
            new ConcurrentReferenceHashMap<>(16, ConcurrentReferenceHashMap.ReferenceType.SOFT,
                    ConcurrentReferenceHashMap.ReferenceType.SOFT);

    /**
     * {@inheritDoc}
     * Use {@link ConcurrentReferenceHashMap#computeIfAbsent}
     *
     * @param request {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    @Override
    public HttpSdkEnum getAndSetHttpSdkEnum(@NotNull HttpRequest<R> request) throws IllegalStateException {
        return SDK_ENUM_CACHE.computeIfAbsent(request.getClass().getName(),
                s -> {
                    HttpSdkEnumCultivate cultivate =
                            request.getClass().getAnnotation(HttpSdkEnumCultivate.class);
                    if (cultivate == null)
                        throw new IllegalStateException("No available annotation [top.osjf.sdk.http.process." +
                                "HttpSdkEnumCultivate] was found from the running class " + request.getClass().getName() + ".");
                    return new DefaultCultivateHttpSdkEnum(cultivate.url(), cultivate.version(),
                            cultivate.protocol(),
                            cultivate.method(),
                            cultivate.name());
                });
    }
}
