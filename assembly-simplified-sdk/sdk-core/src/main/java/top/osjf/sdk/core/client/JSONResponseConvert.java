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

package top.osjf.sdk.core.client;

import top.osjf.sdk.core.process.DefaultErrorResponse;
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.util.CollectionUtils;
import top.osjf.sdk.core.util.JSONUtil;

import java.util.List;

/**
 * JSON response converter interface
 * <p>
 * This interface is used to define a universal mechanism that can convert JSON
 * formatted response strings into specific response objects.
 * It extends from a more general response converter interface ({@link ResponseConvert})
 * and is specifically designed to handle JSON formatted responses.
 * Classes that implement this interface will be able to parse JSON responses from
 * network requests or other sources and convert them into the specific response
 * type expected by the caller.
 * <p>
 * This interface provides a default method ({@link #convertToResponse}) that
 * allows subclasses to execute the method without implementing it,
 * You can obtain the basic JSON to response object conversion function. However,
 * subclasses can still choose to override this method to provide custom transformation logic.
 * <p>
 * Overview of Conversion Logic:
 * <ul>
 * <li>Firstly, obtain the expected response type based on the request object ({@code Request<R>})</li>
 * <li>Then, check if the response string ({@code responsiveStr}) is a valid JSON object. If so,
 * attempt to parse it into an object of the expected type</ li>
 * <li>If the response string is a valid JSON array, parse it into a list of objects of the
 * expected type. If the list is not empty, take the first element in the list as the response object;
 * If the list is empty, create an empty object of the corresponding type</li>
 * <li>If the response string is neither a valid JSON object nor a valid JSON array, treat it as
 * an error response and attempt to parse it into a default error response object</li>
 * </ul>
 * <p>
 * This interface is mainly used for network request libraries or any scenario that requires
 * converting JSON format data into Java objects.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface JSONResponseConvert<R extends Response> extends ResponseConvert<R> {

    /**
     * {@inheritDoc}
     * By default, a mechanism is provided to convert response classes in JSON format.
     *
     * @param request     {@inheritDoc}
     * @param responseStr {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    default R convertToResponse(Request<R> request, String responseStr) {
        R response;
        Object type = request.getResponseRequiredType();
        if (JSONUtil.isValidObject(responseStr)) {
            response = JSONUtil.parseObject(responseStr, type);
        } else if (JSONUtil.isValidArray(responseStr)) {
            List<R> responses = JSONUtil.parseArray(responseStr, type);
            if (CollectionUtils.isNotEmpty(responses)) {
                response = responses.get(0);
            } else {
                response = JSONUtil.toEmptyObj(type);
            }
        } else {
            response = DefaultErrorResponse
                    .parseErrorResponse(responseStr, DefaultErrorResponse.ErrorType.DATA, request);
        }
        return response;
    }
}
