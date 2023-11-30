package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.util.annotation.NotNull;

import java.util.Collections;
import java.util.List;
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
     * Create a success {@code HttpResultResponse} with Result data.
     *
     * @param data Result data.
     */
    public HttpResultResponse(T data) {
        this(true, SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    /**
     * Create a success {@code HttpResultResponse} with message.
     *
     * @param message Result information.
     */
    public HttpResultResponse(String message) {
        this(false, FAILED_CODE, message, null);
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

    /**
     * Provide other codes that can confirm successful requests.
     * @return Codes.
     */
    public List<Integer> anySuccessCodes() {
        return Collections.emptyList();
    }

    //Static construction methods, including success and failure.

    public static HttpResultResponse<Boolean> success() {
        return new HttpResultResponse<>(true);
    }

    public static <T> HttpResultResponse<T> success(T data) {
        return new HttpResultResponse<>(data);
    }

    public static <T> HttpResultResponse<T> success(@NotNull Supplier<T> dataSupplier) {
        return success(dataSupplier.get());
    }

    public static HttpResultResponse<String> failed() {
        return new HttpResultResponse<>(FAILED_MESSAGE);
    }

    public static <T> HttpResultResponse<T> failed(String filedMsg) {
        return new HttpResultResponse<>(filedMsg);
    }

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
}
