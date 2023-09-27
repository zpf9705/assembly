package top.osjf.assembly.simplified.sdk.client;

import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.simplified.sdk.process.Response;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * A custom processing scheme for response string conversion {@link Response}.
 *
 * @author zpf
 * @since 1.1.0
 */
@FunctionalInterface
public interface ResponseConvert<R extends Response> {

    /**
     * Convert the response type {@link Request} of the request class record to the specified {@link Response}
     * using the following custom method.
     * <p>
     * You can customize the conversion method based on the pre defined return values of the API.
     * <p>{@link top.osjf.assembly.simplified.sdk.http.AbstractHttpClient#convertToResponse(Request, String)}Default to
     * JSON format conversion.</p>
     *
     * @param request     {@link Request} class model parameters of API.
     * @param responseStr String data for API response.
     * @return The converted response model data is implemented in {@link Response} and cannot be {@literal null}.
     */
    @NotNull
    R convertToResponse(Request<R> request, String responseStr);
}
