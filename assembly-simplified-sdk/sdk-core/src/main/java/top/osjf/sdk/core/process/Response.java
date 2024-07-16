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

package top.osjf.sdk.core.process;

import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.core.client.PreProcessingResponseHandler;
import top.osjf.sdk.core.client.ResponseConvert;

import java.io.Serializable;

/**
 * Define response nodes for SDK.
 *
 * <p>It is mainly used to receive the return value information of the API,
 * defining the method {@link #isSuccess()} to determine whether the request
 * is successful, including the method of obtaining the response message
 * {@link #getMessage()}, other transformation methods and processing methods,
 * defined in {@link Client}.
 *
 * <p>After the request, the corresponding response body will be transformed
 * according to the rewritten {@link PreProcessingResponseHandler} and
 * {@link ResponseConvert}.</p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface Response extends ErrorResponse, Serializable {

    /**
     * Returns the success identifier of the request, displayed as a Boolean value.
     *
     * @return displayed as a Boolean value，if {@code true} represents
     * success, otherwise it fails.
     */
    boolean isSuccess();

    /**
     * Returns information carried by the end of the return request.
     *
     * @return information carried by the end of the return request.
     */
    String getMessage();
}
