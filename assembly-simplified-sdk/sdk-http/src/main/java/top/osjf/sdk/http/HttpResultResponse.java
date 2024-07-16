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

import org.apache.commons.lang3.StringUtils;
import top.osjf.sdk.commons.annotation.NotNull;
import top.osjf.sdk.core.process.InspectionResponseData;
import top.osjf.sdk.core.util.JSONUtil;

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
 * @since 1.0.0
 */
public class HttpResultResponse<T> extends AbstractHttpResponse implements InspectionResponseData {

    private static final long serialVersionUID = 3517854416942429708L;

    /*** The result indicates true success and false failure.*/
    private Boolean success;

    /*** Result code.*/
    private Integer code;

    /*** Result information.*/
    private String message;

    /*** Result data.*/
    private T data;

    /*** The default value returned when the request fails.*/
    private T failedSeatData;

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

    @Override
    public T failedSeatData() {
        return failedSeatData;
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
        return failed(SC_INTERNAL_SERVER_ERROR, FAILED_MESSAGE);
    }

    /**
     * Static for create a failed {@code HttpResultResponse}.
     *
     * @param <T>      data types.
     * @param filedMsg error message.
     * @return template of response.
     */
    public static <T> HttpResultResponse<T> failed(String filedMsg) {
        return failed(SC_INTERNAL_SERVER_ERROR, filedMsg);
    }

    /**
     * Static for create a failed {@code HttpResultResponse}.
     *
     * @param <T>      data types.
     * @param code     error result code.
     * @param filedMsg error message supplier.
     * @return template of response.
     */
    public static <T> HttpResultResponse<T> failed(Integer code, String filedMsg) {
        return new HttpResultResponse<>(code, filedMsg);
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

    public void setFailedSeatData(T failedSeatData) {
        this.failedSeatData = failedSeatData;
    }

    //Override Object methods.

    @Override
    public String toString() {
        return "HttpResultResponse{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", json=" + JSONUtil.toJSONString(this) +
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
