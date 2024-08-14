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

package top.osjf.sdk.http.ok;

import top.osjf.sdk.http.AbstractHttpClient;
import top.osjf.sdk.http.HttpResponse;


/**
 * HTTP tool request client class based on Square's open-source products.
 *
 * <p>Please refer to {@link AbstractHttpClient} for the specific request process.</p>
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class OkHttpClient<R extends HttpResponse> extends AbstractHttpClient<R> {

    private static final long serialVersionUID = 9030529850666460725L;

    /* ******* Construct ***********/

    public OkHttpClient(String url) {
        super(url);
        setRequestExecutor(OkHttpRequestExecutor.INSTANCE);
    }
}
