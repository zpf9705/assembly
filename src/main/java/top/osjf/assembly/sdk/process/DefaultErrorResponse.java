package top.osjf.assembly.sdk.process;


import com.alibaba.fastjson.JSON;
import copy.cn.hutool.v_5819.core.exceptions.ExceptionUtil;
import org.apache.http.HttpStatus;
import top.osjf.assembly.sdk.DataConvertException;

import java.util.Objects;

/**
 * Default response impl for {@link Response}.
 * <p>Used to handle exception requests.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public class DefaultErrorResponse extends AbstractResponse {

    private static final long serialVersionUID = -6303939513087992265L;

    public static final Integer SDK_ERROR_CODE = 871287;

    public static final Integer DATA_ERROR_CODE = 600558;

    public static final Integer UNKNOWN_ERROR_CODE = 500358;

    private Integer code;

    private String message;

    public DefaultErrorResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    public static DefaultErrorResponse buildSdkExceptionResponse(String message) {
        return new DefaultErrorResponse(SDK_ERROR_CODE, message);
    }

    public static DefaultErrorResponse buildUnknownResponse(String message) {
        return new DefaultErrorResponse(UNKNOWN_ERROR_CODE, String
                .format("Happen unknown exception,message=[%s]", message));
    }

    public static DefaultErrorResponse buildDataErrorResponse(String message) {
        return new DefaultErrorResponse(DATA_ERROR_CODE, String
                .format("Happen data_error exception,message=[%s]", message));
    }

    public static <R extends Response> R parseErrorResponse(String error, ErrorType type, Class<R> clazz) {
        return parseErrorResponse(new DataConvertException(error), type, clazz);
    }

    public static <R extends Response> R parseErrorResponse(Throwable error, ErrorType type, Class<R> clazz) {
        DefaultErrorResponse defaultErrorResponse = type.getMessageWithType(error);
        String responseErrorStr = JSON.toJSONString(defaultErrorResponse);
        R r = JSON.parseObject(responseErrorStr, clazz);
        r.setErrorCode(defaultErrorResponse.getCode());
        r.setErrorMessage(defaultErrorResponse.getMessage());
        return r;
    }


    @FunctionalInterface
    interface MessageGetter {

        DefaultErrorResponse getMessageWithType(Throwable error);
    }

    public enum ErrorType implements MessageGetter {
        SDK {
            @Override
            public DefaultErrorResponse getMessageWithType(Throwable error) {
                return buildSdkExceptionResponse(
                        ExceptionUtil.stacktraceToOneLineString(error, 1500)
                );
            }
        }, UN_KNOWN {
            @Override
            public DefaultErrorResponse getMessageWithType(Throwable error) {
                return buildUnknownResponse(
                        ExceptionUtil.stacktraceToOneLineString(error, 4500)
                );
            }
        }, DATA {
            @Override
            public DefaultErrorResponse getMessageWithType(Throwable error) {
                return buildDataErrorResponse(
                        ExceptionUtil.stacktraceToOneLineString(error, 3000)
                );
            }
        }
    }
}
