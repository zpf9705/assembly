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

import top.osjf.sdk.http.process.HttpRequest;
import top.osjf.sdk.http.process.HttpResponse;
import top.osjf.sdk.http.process.HttpSdkEnum;

import javax.annotation.concurrent.ThreadSafe;

/**
 * {@code HttpSdkEnumManager} is used to retrieve and cache a dedicated
 * immutable {@code HttpSdkEnum} object based on the current request object
 * {@code Request}.
 *
 * <p>This interface can be extended according to Java's SPI mechanism
 * {@link java.util.ServiceLoader}, with annotations {@link top.osjf.sdk.core.support.LoadOrder},
 * to achieve self defined extensions.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@ThreadSafe
public interface HttpSdkEnumManager<R extends HttpResponse> {

    /**
     * Based on a current request object {@code Request}, retrieve and cache a
     * dedicated immutable {@code HttpSdkEnum} object, hoping that the implementation
     * of this method is thread safe.
     *
     * @param currentRequest The current executing request
     *                       {@code Request} object.
     * @return Request relevant metadata information interface {@code HttpSdkEnum}.
     * @throws IllegalStateException If there are no available annotations for the
     *                               currently executed {@code HttpSdkEnumCultivate},
     *                               this exception object will be thrown.
     */
    HttpSdkEnum getAndSetHttpSdkEnum(HttpRequest<R> currentRequest) throws IllegalStateException;
}
