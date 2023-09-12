package top.osjf.assembly.sdk.process;

import top.osjf.assembly.sdk.SdkEnum;
import top.osjf.assembly.sdk.SdkException;
import top.osjf.assembly.sdk.client.Client;
import top.osjf.assembly.sdk.client.HttpClient;

import java.io.Serializable;
import java.util.Map;

/**
 * Request node information interface defined by SDK.
 * <p>
 * You need to define an implementation {@link #matchSdk()} interface to provide a fixed description of
 * SDK information.
 * <p>It is recommended to define an enumeration class.</p>
 * <p>
 * Generally, configure the host name as a configurable item, and dynamically format the input, which
 * is the {@link #formatUrl(String)} method. The corresponding request header information can be easily
 * added through the {@link #getHeadMap()} method. The body input here is set to {@link Object} to mask
 * all parameter differences and be processed uniformly in the future, Through the {@link #getClientCls()}
 * method, you can customize the request process for {@link Client}.
 * <p>
 * Here is a written HTTP based client solution.{@link top.osjf.assembly.sdk.client.HttpClient}.
 * <p>
 * Of course, the final conversion is still the response implementation class {@link Request} that
 * you defined for {@link #getResponseCls()}.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Request<R extends Response> extends RequestParamCapable, Serializable {

    /**
     * SDK containing {@link SdkEnum} implementation information.
     *
     * @return Impl for {@link SdkEnum}.
     */
    SdkEnum matchSdk();

    /**
     * The manual URL concatenation method allows users to concatenate parameters on URLs
     * other than the {@link SdkEnum#getRequestMethod()} request mode, which needs to be rewritten directly.
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
        return matchSdk().getUlr(host) + urlJoin();
    }

    @Override
    Object getRequestParam();

    /**
     * Method for verifying request parameters, fixed throw {@link SdkException}.
     * <p>Taking {@link top.osjf.assembly.sdk.client.HttpClient} as an example, you can take
     * a look at its {@link HttpClient#request()} method. You need to throw {@link SdkException}
     * for validation parameters in order to perform specialized exception capture.</p>
     */
    void validate() throws SdkException;

    /**
     * Obtain the class object of the response transformation entity, implemented in {@link Response}.
     *
     * @return Return implementation for {@link Response}.
     */
    Class<R> getResponseCls();

    /**
     * Obtain the header information that needs to be set, and return a map.
     *
     * @return To be a {@link Map}.
     */
    Map<String, String> getHeadMap();

    /**
     * Obtain the implementation class object of {@link Client} and you can define it yourself.
     * <p>
     * Currently, there are {@link top.osjf.assembly.sdk.client.HttpClient} in HTTP format and
     * some default methods provided in {@link top.osjf.assembly.sdk.client.AbstractClient}.
     *
     * @return Implementation class of {@link Client}.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Client> getClientCls();
}
