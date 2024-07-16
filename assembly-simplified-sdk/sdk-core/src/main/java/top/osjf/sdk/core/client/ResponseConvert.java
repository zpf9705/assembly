package top.osjf.sdk.core.client;

import top.osjf.sdk.commons.annotation.NotNull;
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;

/**
 * A custom processing scheme for response string conversion {@link Response}.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface ResponseConvert<R extends Response> {

    /**
     * Convert the response type {@link Request} of the request
     * class record to the specified {@link Response} using the
     * following custom method.
     *
     * <p>You can customize the conversion method based on the
     * predefined return values of the API.
     *
     * @param request     {@link Request} class model parameters of API.
     * @param responseStr String data for API response.
     * @return The converted response model data is implemented in
     * {@link Response} and cannot be {@literal null}.
     */
    @NotNull
    R convertToResponse(Request<R> request, String responseStr);
}
