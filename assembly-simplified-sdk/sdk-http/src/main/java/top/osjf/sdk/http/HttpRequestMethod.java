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

import top.osjf.sdk.commons.annotation.NotNull;

import java.util.Map;

/**
 * Enumeration of HTTP request method types.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public enum HttpRequestMethod {

    GET {
        @Override
        public String doRequest(@NotNull Instance instance, @NotNull String url, Map<String, String> headers,
                                Object requestParam, Boolean montage) throws Exception {
            return instance.getInstance().get(url, headers, requestParam, montage);
        }
    }, POST {
        @Override
        public String doRequest(@NotNull Instance instance, @NotNull String url, Map<String, String> headers,
                                Object requestParam, Boolean montage) throws Exception {
            return instance.getInstance().post(url, headers, requestParam, montage);
        }
    }, PUT {
        @Override
        public String doRequest(@NotNull Instance instance, @NotNull String url, Map<String, String> headers,
                                Object requestParam, Boolean montage) throws Exception {
            return instance.getInstance().put(url, headers, requestParam, montage);
        }
    }, DELETE {
        @Override
        public String doRequest(@NotNull Instance instance, @NotNull String url, Map<String, String> headers,
                                Object requestParam, Boolean montage) throws Exception {
            return instance.getInstance().delete(url, headers, requestParam, montage);
        }
    };

    /**
     * General methods for HTTP request method types are implemented by subclass enumeration.
     *
     * @param instance     {@link Instance}
     * @param url          {@link HttpRequest#getUrl(String)} ()}
     * @param headers      {@link HttpRequest#getHeadMap()}
     * @param requestParam {@link HttpRequest#getRequestParam()}
     * @param montage      {@link HttpRequest#montage()}
     * @return http request result.
     * @throws Exception maybe exceptions when http request.
     */
    public String doRequest(@NotNull Instance instance,
                            @NotNull String url,
                            Map<String, String> headers,
                            Object requestParam,
                            Boolean montage) throws Exception {
        throw new UnsupportedOperationException();
    }
}
