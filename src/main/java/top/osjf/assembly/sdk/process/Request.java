package top.osjf.assembly.sdk.process;

import top.osjf.assembly.sdk.SdkException;
import top.osjf.assembly.sdk.client.Client;

import java.io.Serializable;
import java.util.Map;

/**
 * The definition method of the requested public node.
 * <p>It includes parameter acquisition {@link RequestParamCapable} required for the request,
 * parameter verification (intercepted in the form of {@link SdkException}), recording the
 * response body type for subsequent encapsulation conversion, and verifying the request header.</p>
 * <p>
 * The class object that can be rewritten to implement {@link Client} can be customized for logical
 * implementation of methods in {@link Client}.
 * <p>On the basis of this interface, it can be extended, such as utilizing the request pattern
 * {@link top.osjf.assembly.sdk.http.HttpRequest} of HTTP to define special methods related to HTTP.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Request<R extends Response> extends RequestParamCapable, Serializable {

    @Override
    Object getRequestParam();

    /**
     * Method for verifying request parameters, fixed throw {@link SdkException}.
     * <p>Taking {@link top.osjf.assembly.sdk.http.DefaultHttpClient} as an example, you can take
     * a look at its {@link top.osjf.assembly.sdk.http.DefaultHttpClient#request()} method. You
     * need to throw {@link SdkException} for validation parameters in order to perform specialized
     * exception capture.</p>
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
     * Currently, there are {@link HttpClient} in HTTP format and
     * some default methods provided in {@link top.osjf.assembly.sdk.client.AbstractClient}.
     *
     * @return Implementation class of {@link Client}.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Client> getClientCls();
}
