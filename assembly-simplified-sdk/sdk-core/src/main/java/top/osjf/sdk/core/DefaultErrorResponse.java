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

import top.osjf.sdk.core.util.ExceptionUtils;
import top.osjf.sdk.core.util.JSONUtil;

import java.util.Objects;

/**
 * Default response impl for {@link ErrorResponse}.
 * <p>Not directly used for response purposes, mainly used
 * for handling exception conversions in error situations.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public final class DefaultErrorResponse extends AbstractResponse {

    private static final long serialVersionUID = -6303939513087992265L;

    public static final Integer SDK_ERROR_CODE = 871287;

    public static final Integer UNKNOWN_ERROR_CODE = 500358;

    private Object code;

    private String message;

    public DefaultErrorResponse(Object code, String message) {
        this.code = code;
        this.message = message;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    @Override
    public boolean isSuccess() {
        return code != null && Objects.equals(200, code);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String asJson() {
        return JSONUtil.toJSONString(this);
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

    /** Build a universal interface for {@link DefaultErrorResponse} based on exceptions.*/
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
