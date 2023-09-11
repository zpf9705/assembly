package top.osjf.assembly.sdk.process;

import top.osjf.assembly.sdk.SdkEnum;
import top.osjf.assembly.sdk.SdkException;
import top.osjf.assembly.sdk.client.ClientType;

import java.io.Serializable;
import java.util.Map;

/**
 * The request node defined for SDK, which records URLs, response transformation types, header information, request methods, etc.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Request<R extends Response> extends RequestParamCapable, Validated, Serializable {

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
     *
     * @param host The host name of the SDK.
     * @return The request address for the SDK.
     */
    default String formatUrl(String host) {
        return matchSdk().getUlr(host) + urlJoin();
    }

    @Override
    void validate() throws SdkException;

    @Override
    Object getRequestParam();

    /**
     * Obtain the class object of the response transformation entity, implemented in {@link Response}.
     *
     * @return API - Response Object
     */
    Class<R> getResponseCls();

    /**
     * Obtain the header information that needs to be set, and return a map.
     *
     * @return To be a {@link Map}.
     */
    Map<String, String> getHeadMap();

    /**
     * Choose his request mode for {@link ClientType}.
     *
     * @return See {@link ClientType}.
     */
    ClientType getClientType();
}
