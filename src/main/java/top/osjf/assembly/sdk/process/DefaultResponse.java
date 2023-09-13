package top.osjf.assembly.sdk.process;


import org.apache.http.HttpStatus;

import java.util.Objects;

/**
 * Default response impl for {@link Response}.
 *
 * @author zpf
 * @since 1.1.0
 */
public class DefaultResponse extends AbstractResponse {

    private static final long serialVersionUID = -6303939513087992265L;

    public static final Integer DATA_ERROR_CODE = 600558;

    public static final Integer UNKNOWN_ERROR_CODE = 500358;

    private Integer code;

    private String message;

    private String msg;

    public DefaultResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.msg = message;
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
        return message != null ? message : msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static DefaultResponse buildSdkExceptionResponse(String message) {
        return new DefaultResponse(HttpStatus.SC_BAD_REQUEST, message);
    }

    public static DefaultResponse buildUnknownResponse(String message) {
        return new DefaultResponse(UNKNOWN_ERROR_CODE, String
                .format("happen unknown exception,message=[%s]", message));
    }

    public static DefaultResponse buildDataErrorResponse(String message) {
        return new DefaultResponse(DATA_ERROR_CODE, String
                .format("happen data_error exception,message=[%s]", message));
    }
}
