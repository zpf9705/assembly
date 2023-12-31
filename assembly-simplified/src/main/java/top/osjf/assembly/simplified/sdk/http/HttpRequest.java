package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.simplified.sdk.process.Request;

/**
 * Request node information interface defined by SDK of http type.
 *
 * <p>You need to define an implementation {@link #matchHttpSdk()} interface
 * to provide a fixed description of SDK information.
 *
 * <p>It is recommended to define an enumeration class.</p>
 *
 * <p>Generally, configure the host name as a configurable item, and
 * dynamically format the input, which is the {@link #formatUrl(String)} method.
 * <p>The corresponding request header information can be easily added through
 * the {@link #getHeadMap()} method.
 * <p>The body input here is set to {@link Object} to mask all parameter differences
 * and be processed uniformly in the future, Through the {@link #getClientCls()}
 * method, you can customize the request process for
 * {@link top.osjf.assembly.simplified.sdk.client.Client}.
 *
 * <p>Here is a written HTTP based client solution.{@link ApacheHttpClient}
 * and {@link OkHttpClient}.
 *
 * <p>Of course, the final conversion is still the response implementation
 * class {@link HttpRequest} that you defined for {@link #getResponseCls()}.
 *
 * @param <R> Implement a unified response class data type.
 * @author zpf
 * @since 1.1.1
 */
public interface HttpRequest<R extends HttpResponse> extends Request<R> {

    @Override
    default String getUrl(String host) {
        return formatUrl(host);
    }

    @Override
    default boolean isAssignableRequest(Class<?> clazz) {
        return HttpRequest.class.isAssignableFrom(clazz);
    }

    /**
     * Http sdk containing {@link HttpSdkEnum} implementation information.
     *
     * @return Impl for {@link HttpSdkEnum}.
     */
    HttpSdkEnum matchHttpSdk();

    /**
     * Do you want to concatenate the given {@link #getRequestParam()} parameters with rules after the URL.
     * <p>The prerequisite is to provide parameters in the form of a map or JSON strings for key/value.</p>
     *
     * @return If true, it will concatenate the provided parameters for you, otherwise it will be determined
     * based on the request header.
     */
    boolean montage();

    /**
     * The manual URL concatenation method allows users to concatenate parameters on URLs
     * other than the {@link HttpSdkEnum#getRequestMethod()} request mode, which needs to be rewritten directly.
     * <p>The format should refer to the get request.</p>
     *
     * @return Splicing item
     */
    default String urlJoin() {
        return "";
    }

    /**
     * Format the actual request address of the SDK and concatenate subsequent URLs.
     * <p>Here, the splicing parameters of the {@link #urlJoin()} method will be
     * automatically added for you. If you don't need to rewrite {@link #urlJoin()}, you can do so.</p>
     *
     * @param host The host name of the SDK.
     * @return The request address for the SDK.
     */
    default String formatUrl(String host) {
        return matchHttpSdk().getUlr(host) + urlJoin();
    }
}
