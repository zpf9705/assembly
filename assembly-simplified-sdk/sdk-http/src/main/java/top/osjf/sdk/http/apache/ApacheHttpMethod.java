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

package top.osjf.sdk.http.apache;

import top.osjf.sdk.http.HttpMethod;

import java.util.Map;

/**
 * One of the implementation classes of {@link HttpMethod}, please
 * refer to{@link ApacheHttpSimpleRequestUtils} for implementation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ApacheHttpMethod implements HttpMethod {

    /*** The only instance.*/
    public static final HttpMethod INSTANCE = new ApacheHttpMethod();

    private ApacheHttpMethod() {
    }

    @Override
    public String get(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpSimpleRequestUtils.get(url, headers, requestParam, montage);
    }

    @Override
    public String post(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpSimpleRequestUtils.post(url, headers, requestParam, montage);
    }

    @Override
    public String put(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpSimpleRequestUtils.put(url, headers, requestParam, montage);
    }

    @Override
    public String delete(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpSimpleRequestUtils.delete(url, headers, requestParam, montage);
    }
}
