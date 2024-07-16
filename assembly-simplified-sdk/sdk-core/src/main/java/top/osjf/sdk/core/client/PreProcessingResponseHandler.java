package top.osjf.sdk.core.client;

import top.osjf.sdk.commons.annotation.NotNull;
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;

/**
 * The preprocessing of response strings is mainly to lay the groundwork for
 * the unified conversion of {@link ResponseConvert} response entities in the
 * future.
 *
 * <p>If encountering a type different from the general conversion scheme,
 * special conversion can be attempted here to convert to a unified type, and
 * subsequent conversion of response entities can be carried out.</p>
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface PreProcessingResponseHandler<R extends Response> {

    /**
     * Preprocessing method for response strings.
     * <p>There is no preprocessing method by default.</p>
     *
     * @param request     {@link Request} class model parameters of API.
     * @param responseStr String data for API response.
     * @return The formatted string after special processing of the response
     * string cannot return {@literal null}.
     */
    @NotNull
    String preResponseStrHandler(Request<R> request, String responseStr);
}
