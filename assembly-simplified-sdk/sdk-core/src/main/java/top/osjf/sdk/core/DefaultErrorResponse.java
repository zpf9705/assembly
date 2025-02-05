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

package top.osjf.sdk.core;

import top.osjf.sdk.core.exception.SdkException;
import top.osjf.sdk.core.util.ExceptionUtils;
import top.osjf.sdk.core.util.JSONUtil;

import java.util.Objects;

/**
 * {@code DefaultErrorResponse} is a default {@link Response Response} implementation
 * class that has type conversion handling in case of success or error.
 *
 * <p>The partial introduction is as follows:
 * <ul>
 * <li>Conventional default handling:
 * Provides a combination of status code {@link #setCode(Object)} and status information
 * {@link #setMessage(String)} to support successful judgment of server response. If it
 * meets your response requirements, it can be used by default.
 * </li>
 * <li>Indicate the handling method for errors:
 *  Custom status codes {@link #SDK_ERROR_CODE} and {@link #UNKNOWN_ERROR_CODE} are used
 *  to define the boundaries of exceptions during requests. For {@link SdkException} or
 *  other exceptions, default status codes are provided when there is no response class
 *  accepted by default. The response information is the specific reason for the exception
 *  object {@link Throwable#getMessage()}.
 * </li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public final class DefaultErrorResponse extends AbstractResponse {
    private static final long serialVersionUID = -6303939513087992265L;

    //////////////////////////////////// Conventional default handling. ////////////////////////

    /**
     * The response status code can be defined for any type.
     */
    private Object code;

    /**
     * Response status information and the reason for the request server's return.
     */
    private String message;

    /**
     * Creates a {@code DefaultErrorResponse} by given case {@code code} and {@code message}.
     *
     * @param code    #this.code.
     * @param message #this.message.
     */
    public DefaultErrorResponse(Object code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The agreement for successful communication is to use a 200 status code.
     */
    @Override
    public boolean isSuccess() {
        if (code != null) {
            return Objects.equals(code.toString(), "200");
        }
        return super.isSuccess();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Object getCode() {
        return code;
    }

    //////////////////////////////////// Indicate the handling method for errors. ////////////////////////

    /**
     * This code is used to describe the default state when {@link SdkException SdkException} occurs.
     */
    public static final Integer SDK_ERROR_CODE = 871287;

    /**
     * This code is used to describe the default state outside of {@link SdkException SdkException}.
     */
    public static final Integer UNKNOWN_ERROR_CODE = 500358;

    /**
     * {@inheritDoc}
     *
     * @since 1.0.3
     */
    @Override
    public void setErrorCode(Object code) {
        this.setCode(code);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.3
     */
    @Override
    public void setErrorMessage(String message) {
        this.setMessage(message);
    }

    /**
     * Build a default error response {@code DefaultErrorResponse} using
     * {@link #SDK_ERROR_CODE} based on the provided information {@code message}.
     *
     * @param message Provide information accordingly.
     * @return a default error response {@code DefaultErrorResponse}.
     */
    public static DefaultErrorResponse buildSdkExceptionResponse(String message) {
        return new DefaultErrorResponse(SDK_ERROR_CODE, message);
    }

    /**
     * Build a default error response {@code DefaultErrorResponse} using
     * {@link #UNKNOWN_ERROR_CODE} based on the provided information {@code message}.
     *
     * @param message Provide information accordingly.
     * @return a default error response {@code DefaultErrorResponse}.
     */
    public static DefaultErrorResponse buildUnknownResponse(String message) {
        return new DefaultErrorResponse(UNKNOWN_ERROR_CODE, String
                .format("Happen unknown exception,message=[%s]", message));
    }

    /**
     * Convert the specified response {@link Response} to the specified error
     * type based on {@link Throwable} and {@link ErrorType} and {@link Request}.
     *
     * @param error   the exception instance.
     * @param type    the wrong enumeration type.
     * @param request the request instance.
     * @param <R>     the type of response.
     * @return The building response {@code R} error instance.
     * @throws NullPointerException if input {@code Throwable} or {@code ErrorType}
     *                              or {@code Request} is null.
     */
    public static <R extends Response> R parseErrorResponse(Throwable error, ErrorType type, Request<R> request) {
        DefaultErrorResponse response = type.convertToDefaultErrorResponse(error);
        R r = JSONUtil.parseObject(response.asJson(), request.getResponseType());
        r.setErrorCode(response.getCode());
        r.setErrorMessage(response.getMessage());
        return r;
    }

    /**
     * Convert the specified response {@link Response} to the specified error
     * type based on {@code DefaultErrorResponse} and {@code request}.
     *
     * @param errorResponse the typed {@code DefaultErrorResponse} instance.
     * @param request       the request instance.
     * @param <R>           the type of response.
     * @return The building response {@code R} error instance.
     * @throws NullPointerException if input {@code DefaultErrorResponse} or
     *                              {@code Request} is null.
     */
    public static <R extends Response> R parseErrorResponse(DefaultErrorResponse errorResponse, Request<R> request) {
        R response = JSONUtil.parseObject(errorResponse.asJson(), request.getResponseType());
        response.setErrorCode(errorResponse.getCode());
        response.setErrorMessage(errorResponse.getMessage());
        return response;
    }

    private String asJson() {
        return JSONUtil.toJSONString(this);
    }

    /**
     * Build a universal interface for {@link DefaultErrorResponse} based on exceptions.
     */
    @FunctionalInterface
    private interface ErrorResponseConvert {
        DefaultErrorResponse convertToDefaultErrorResponse(Throwable error);
    }

    /*** Error allocating different {@link DefaultErrorResponse} creation method enumeration
     * classes for different types.*/
    public enum ErrorType implements ErrorResponseConvert {

        SDK {
            @Override
            public DefaultErrorResponse convertToDefaultErrorResponse(Throwable error) {
                return buildSdkExceptionResponse(getMessage(error));
            }
        }, UN_KNOWN {
            @Override
            public DefaultErrorResponse convertToDefaultErrorResponse(Throwable error) {
                return buildUnknownResponse(getMessage(error));
            }
        };

        protected String getMessage(Throwable error) {
            return ExceptionUtils.getMessage(error);
        }
    }
}
