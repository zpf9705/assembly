package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.json.FastJsonUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Provide a corresponding template, define the relevant items,
 * customize them, or use the default rules of HTTP.
 * <p>Customize the corresponding class, just follow this set of
 * specifications to achieve rapid development, and custom inheritance
 * {@link AbstractHttpResponse} is also possible.
 *
 * @param <T> Response data types.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.1.2
 */
public class HttpResultResponse<T> extends AbstractHttpResponse {

    private static final long serialVersionUID = 3517854416942429708L;

    /*** The result indicates true success and false failure.*/
    private Boolean success;

    /*** Result code.*/
    private Integer code;

    /*** Result information.*/
    private String message;

    /*** Result data.*/
    private T data;

    /**
     * No parameter construction, customized based on set and get.
     */
    public HttpResultResponse() {
    }

    /**
     * Create a success {@code HttpResultResponse} with result indicates.
     *
     * @param success Result indicates.
     */
    public HttpResultResponse(Boolean success) {
        this(success, SC_OK, SUCCESS_MESSAGE, null);
    }

    /**
     * Create a success {@code HttpResultResponse} with Result data.
     *
     * @param data Result data.
     */
    public HttpResultResponse(T data) {
        this(true, SC_OK, SUCCESS_MESSAGE, data);
    }

    /**
     * Create a success {@code HttpResultResponse} with message.
     *
     * @param message Result information.
     */
    public HttpResultResponse(String message) {
        this(false, SC_OK, message, null);
    }

    /**
     * Create a failed {@code HttpResultResponse} with code and message.
     *
     * @param code    Result code.
     * @param message Result information.
     */
    public HttpResultResponse(Integer code, String message) {
        this(false, code, message, null);
    }

    //Construct custom plans with full parameters.
    public HttpResultResponse(Boolean success, Integer code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public boolean isSuccess() {
        return (super.isSuccess() || anySuccessCodes().contains(getCode())) && getSuccess();
    }

    //Since 2.1.3
    @Override
    public void setErrorCode(Object code) {
        if (this.code == null) {
            if (code instanceof Integer) {
                this.code = (Integer) code;
            } else {
                try {
                    this.code = Integer.parseInt(code.toString());
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    //Since 2.1.3
    @Override
    public void setErrorMessage(String message) {
        if (StringUtils.isBlank(this.message)) {
            this.message = message;
        }
    }

    /**
     * Provide other codes that can confirm successful requests.
     *
     * @return String codes.
     */
    public List<Integer> anySuccessCodes() {
        return Collections.emptyList();
    }

    //Static construction methods, including success and failure.

    /**
     * Static for create a success {@code HttpResultResponse}.
     * <p>Defaults to {@code true}</p>
     *
     * @param <T> data types.
     * @return template of response.
     */
    public static <T> HttpResultResponse<T> success() {
        return new HttpResultResponse<>(true);
    }

    /**
     * Static for create a success {@code HttpResultResponse}.
     *
     * @param data response data.
     * @param <T>  data types.
     * @return template of response.
     */
    public static <T> HttpResultResponse<T> success(T data) {
        return new HttpResultResponse<>(data);
    }

    /**
     * Static for create a success {@code HttpResultResponse}.
     *
     * @param dataSupplier data supplier.
     * @param <T>          data types.
     * @return template of response.
     */
    public static <T> HttpResultResponse<T> success(@NotNull Supplier<T> dataSupplier) {
        return success(dataSupplier.get());
    }

    /**
     * Static for create a success {@code HttpResultResponse}.
     * <p>Defaults to {@link #FAILED_MESSAGE}</p>
     *
     * @param <T> data types.
     * @return template of response.
     */
    public static <T> HttpResultResponse<T> failed() {
        return new HttpResultResponse<>(FAILED_MESSAGE);
    }

    /**
     * Static for create a failed {@code HttpResultResponse}.
     *
     * @param <T>      data types.
     * @param filedMsg error message.
     * @return template of response.
     */
    public static <T> HttpResultResponse<T> failed(String filedMsg) {
        return new HttpResultResponse<>(filedMsg);
    }

    /**
     * Static for create a failed {@code HttpResultResponse}.
     *
     * @param <T>              data types.
     * @param filedMsgSupplier error message supplier.
     * @return template of response.
     */
    public static <T> HttpResultResponse<T> failed(@NotNull Supplier<String> filedMsgSupplier) {
        return failed(filedMsgSupplier.get());
    }

    //The get and set methods for attributes.

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    //Override Object methods.

    @Override
    public String toString() {
        return "HttpResultResponse{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", json=" + FastJsonUtils.toJSONString(this) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResultResponse<?> that = (HttpResultResponse<?>) o;
        return Objects.equals(success, that.success) &&
                Objects.equals(code, that.code) &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, code, message, data);
    }
}
