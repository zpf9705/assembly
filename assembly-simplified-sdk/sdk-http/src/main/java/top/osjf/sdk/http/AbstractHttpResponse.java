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

import top.osjf.sdk.core.AbstractResponse;
import top.osjf.sdk.core.DefaultErrorResponse;
import top.osjf.sdk.core.support.Nullable;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * The abstract node class {@code AbstractHttpResponse} for implementing HTTP requests
 * includes the default HTTP request indication scheme, which defines the {@code code}
 * code, but the specific type is not determined.
 *
 * <p>Due to differences in encapsulation interfaces, public fields are not provided here.
 * If you need to default, please refer to {@link DefaultErrorResponse}.
 * <dl>
 *     <dt>{@link DefaultErrorResponse#buildSdkExceptionResponse(String)}</dt>
 *     <dt>{@link DefaultErrorResponse#buildUnknownResponse(String)}</dt>
 * </dl>
 *
 * <p>The prerequisite for use is to check if the field name is consistent
 * with yours, otherwise the default information in {@link AbstractResponse}
 * will be obtained.
 *
 * <p>The compilation of sub abstract classes greatly simplifies the difficulty
 * of SDK inheritance. By simply inheriting this class, one can freely define the
 * success or failure of the agreed response type and related information. Below
 * is a simple code example:
 * <pre>
 * {@code
 * public class ExampleHttpResponse extends AbstractHttpResponse {
 *
 *     private Boolean success;
 *     private Integer code;
 *     private String message;
 *     private List<Object> data;
 *
 *     public boolean isSuccess() {
 *          return Objects.equals(code,200) && success;
 *     }
 *     public boolean getMessage() {
 *          return message;
 *     }
 * }}
 * }
 * </pre>
 *
 * <p>In order to provide a more intuitive description of the relevant definitions of
 * HTTP, {@link top.osjf.sdk.http.spi.HttpResponse} was introduced in version 1.0.2,
 * which allows developers to obtain header information, encoding character sets, protocol
 * versions, and other information about the original server's response transmission.
 * It is suitable for scenarios that focus on response results and better configure
 * development requirements.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractHttpResponse extends AbstractResponse implements HttpResponse {

    private static final long serialVersionUID = 5815281226315499244L;

    public static final String SUCCESS_MESSAGE = "Congratulations";

    public static final String FAILED_MESSAGE = "Internal system error";

    /**
     * In version 1.0.3, this response may not necessarily be non-null, for example,
     * if the response encounters an error such as a connection timeout before the
     * request is made.
     */
    @Nullable
    private top.osjf.sdk.http.spi.HttpResponse httpResponse;

    /**
     * Set a spi {@code HttpResponse} for support to query important information
     * returned by the requesting server.
     *
     * @param httpResponse a spi {@code HttpResponse}.
     */
    public void setHttpResponse(@Nullable top.osjf.sdk.http.spi.HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

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

    /**
     * {@inheritDoc}
     * <p>
     * Provided by {@link top.osjf.sdk.http.spi.HttpResponse}.
     */
    @Override
    public int getStatusCode() {
        return ifSpiResponseNotNullApply(top.osjf.sdk.http.spi.HttpResponse::getStatusCode, SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Provided by {@link top.osjf.sdk.http.spi.HttpResponse}.
     */
    @Override
    public String getStatusMessage() {
        return ifSpiResponseNotNullApply(top.osjf.sdk.http.spi.HttpResponse::getStatusMessage, super.getMessage());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Provided by {@link top.osjf.sdk.http.spi.HttpResponse}.
     */
    @Override
    public Map<String, Object> getHeadMap() {
        return ifSpiResponseNotNullApply(top.osjf.sdk.http.spi.HttpResponse::getHeadMap, Collections.emptyMap());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Provided by {@link top.osjf.sdk.http.spi.HttpResponse}.
     */
    @Override
    @Nullable
    public Charset getCharset() {
        return ifSpiResponseNotNullApply(top.osjf.sdk.http.spi.HttpResponse::getCharset, null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Provided by {@link top.osjf.sdk.http.spi.HttpResponse}.
     */
    @Override
    @Nullable
    public String getBody() {
        return ifSpiResponseNotNullApply(top.osjf.sdk.http.spi.HttpResponse::getBody, null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Provided by {@link top.osjf.sdk.http.spi.HttpResponse}.
     */
    @Nullable
    @Override
    public Object getProtocolVersion() {
        return ifSpiResponseNotNullApply(top.osjf.sdk.http.spi.HttpResponse::getProtocolVersion, null);
    }

    private <T> T ifSpiResponseNotNullApply(Function<top.osjf.sdk.http.spi.HttpResponse, T> func, T def) {
        if (httpResponse != null) {
            return func.apply(httpResponse);
        }
        return def;
    }
}
