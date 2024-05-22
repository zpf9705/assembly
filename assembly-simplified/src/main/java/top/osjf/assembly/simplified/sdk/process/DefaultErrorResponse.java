package top.osjf.assembly.simplified.sdk.process;

import org.apache.http.HttpStatus;
import top.osjf.assembly.simplified.sdk.DataConvertException;
import top.osjf.assembly.util.exceptions.ExceptionUtils;
import top.osjf.assembly.util.json.FastJsonUtils;

import java.util.Objects;

/**
 * Default response impl for {@link ErrorResponse}.
 * <p>Not directly used for response purposes, mainly used
 * for handling exception conversions in error situations.
 *
 * @see #parseErrorResponse(String, ErrorType, Request)
 * @see #parseErrorResponse(Throwable, ErrorType, Request)
 * @author zpf
 * @since 1.1.0
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
        return code != null && Objects.equals(HttpStatus.SC_OK, code);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        DefaultErrorResponse response = type.getMessageWithType(error);
        R r = FastJsonUtils.parseObject(FastJsonUtils.toJSONString(response), request.getResponseRequiredType());
        r.setErrorCode(response.getCode());
        r.setErrorMessage(response.getMessage());
        return r;
    }

    /*** Build a universal interface for {@link DefaultErrorResponse} based on exceptions.*/
    @FunctionalInterface
    interface MessageGetter {
        DefaultErrorResponse getMessageWithType(Throwable error);
    }

    /*** Error allocating different {@link DefaultErrorResponse} creation method enumeration
     * classes for different types.*/
    public enum ErrorType implements MessageGetter {
        SDK {
            @Override
            public DefaultErrorResponse getMessageWithType(Throwable error) {
                return buildSdkExceptionResponse(ExceptionUtils.stacktraceToOneLineString(error, 1500));
            }
        }, UN_KNOWN {
            @Override
            public DefaultErrorResponse getMessageWithType(Throwable error) {
                return buildUnknownResponse(ExceptionUtils.stacktraceToOneLineString(error, 4500));
            }
        }, DATA {
            @Override
            public DefaultErrorResponse getMessageWithType(Throwable error) {
                return buildDataErrorResponse(ExceptionUtils.stacktraceToOneLineString(error, 3000));
            }
        }
    }
}
