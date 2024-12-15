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

package top.osjf.sdk.http.process;

import top.osjf.sdk.core.process.AbstractResponse;
import top.osjf.sdk.core.process.DefaultErrorResponse;

import java.util.Objects;

/**
 * Http response abstract node class, used to define common states,
 * unknown error messages, success plans, etc.
 *
 * <p>You can check the example code:
 * <pre>
 * {@code
 * public class ExampleHttpResponse extends AbstractHttpResponse {
 *
 *     private Boolean success;
 *     private Integer code;
 *     private String message;
 *     private Object errors;
 *     private List<Object> data;
 * }}
 * </pre>
 *
 * <p>Due to differences in encapsulation interfaces, public fields are not provided here.
 * If you need to default, please refer to {@link DefaultErrorResponse}.
 * <dl>
 *     <dt>{@link DefaultErrorResponse#buildSdkExceptionResponse(String)}</dt>
 *     <dt>{@link DefaultErrorResponse#buildUnknownResponse(String)}</dt>
 *     <dt>{@link DefaultErrorResponse#buildDataErrorResponse(String)}</dt>
 * </dl>
 *
 * <p>The prerequisite for use is to check if the field name is consistent
 * with yours, otherwise the default information in {@link AbstractResponse}
 * will be obtained.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractHttpResponse extends AbstractResponse implements HttpResponse {

    private static final long serialVersionUID = 5815281226315499244L;

    public static final String SUCCESS_MESSAGE = "Congratulations";

    public static final String FAILED_MESSAGE = "Internal system error";


    /**
     * {@code isSuccess} and {@code  getMessage} define http success situation.
     */

    @Override
    public boolean isSuccess() {
        return Objects.equals(getCode(), SC_OK) || Objects.equals(getCode(), SC_OK0);
    }

    /**
     * <p>
     * when {@link #isSuccess()} is {@literal true},return {@link #SUCCESS_MESSAGE},
     * otherwise return {@link #FAILED_MESSAGE}.
     */
    @Override
    public String getMessage() {
        return isSuccess() ? SUCCESS_MESSAGE : FAILED_MESSAGE;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default return internal server error code and please rewrite its method
     * accord to method {@link #isSuccess()}.
     */
    @Override
    public Object getCode() {
        return SC_INTERNAL_SERVER_ERROR;
    }
}
