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

import feign.Request;

/**
 * The OpenFeign Client Options interface defines configuration options
 * related to the OpenFeign client.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface OpenFeignClientOptions {

    /*** A default global {@code Request.Options}*/
    Request.Options DEFAULT_FEIGN_OPTIONS = new Request.Options();

    /**
     * Return a Controls the per-request settings currently required to be
     * implemented by all {@link feign.Client clients}
     * <p>Currently only takes effect when <pre>{@code HttpRequestExecutor#useCustomize == false}</pre>.
     * <p>By default, this method returns {@link #DEFAULT_FEIGN_OPTIONS}</ p>
     *
     * @return Controls the per-request settings currently required to be
     * implemented by all {@link feign.Client clients}
     * @since 1.0.2
     */
    default Request.Options getOptions() {
        return DEFAULT_FEIGN_OPTIONS;
    }
}
