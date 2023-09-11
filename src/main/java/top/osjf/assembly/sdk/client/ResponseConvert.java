package top.osjf.assembly.sdk.client;

import org.springframework.lang.NonNull;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.Response;

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
     * <p>{@link HttpClient#convertToResponse(Request, String)}Default to JSON format conversion.</p>
     *
     * @param request     {@link Request} class model parameters of API.
     * @param responseStr String data for API response.
     * @return The converted response model data is implemented in {@link Response} and cannot be {@literal null}.
     */
    @NonNull
    R convertToResponse(Request<R> request, String responseStr);
}
