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

import top.osjf.sdk.core.exception.DataConvertException;
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

    public static final Integer DATA_ERROR_CODE = 600558;

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

    public String asJson() {
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
     * Build a default error response {@code DefaultErrorResponse} using
     * {@link #DATA_ERROR_CODE} based on the provided information {@code message}.
     *
     * @param message Provide information accordingly.
     * @return a default error response {@code DefaultErrorResponse}.
     */
    public static DefaultErrorResponse buildDataErrorResponse(String message) {
        return new DefaultErrorResponse(DATA_ERROR_CODE, String
                .format("Happen data_error exception,message=[%s]", message));
    }

    /**
     * Convert the specified response {@link Response} to the specified error
     * type based on {@code error} and {@code type} and {@code request}.
     *
     * @param error   Specific error information.
     * @param type    Wrong enumeration type.
     * @param request Request parameters.
     * @param <R>     The type of response class.
     * @return Response error parameters.
     */
    public static <R extends Response> R parseErrorResponse(String error, ErrorType type, Request<R> request) {
        return parseErrorResponse(new DataConvertException(error), type, request);
    }

    /**
     * Convert the specified response {@link Response} to the specified error
     * type based on {@link Throwable} and {@link ErrorType} and {@link Request}.
     *
     * @param error   Specific error information.
     * @param type    Wrong enumeration type.
     * @param request Request parameters.
     * @param <R>     The type of response class.
     * @return Response error parameters.
     */
    public static <R extends Response> R parseErrorResponse(Throwable error, ErrorType type, Request<R> request) {
        DefaultErrorResponse response = type.convertToDefaultErrorResponse(error);
        R r = JSONUtil.parseObject(response.asJson(), request.getResponseCls());
        r.setErrorCode(response.getCode());
        r.setErrorMessage(response.getMessage());
        return r;
    }

    /*** Build a universal interface for {@link DefaultErrorResponse} based on exceptions.*/
    private interface ErrorResponseConvert {
        /* Get the type and build different default error responses. */
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
        }, DATA {
            @Override
            public DefaultErrorResponse convertToDefaultErrorResponse(Throwable error) {
                return buildDataErrorResponse(getMessage(error));
            }
        };

        public String getMessage(Throwable error) {
            return ExceptionUtils.getMessage(error);
        }
    }
}
