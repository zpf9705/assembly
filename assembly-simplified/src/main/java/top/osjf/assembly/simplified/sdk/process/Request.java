package top.osjf.assembly.simplified.sdk.process;

import com.google.common.reflect.TypeToken;
import top.osjf.assembly.simplified.sdk.SdkException;
import top.osjf.assembly.simplified.sdk.SdkUtils;
import top.osjf.assembly.simplified.sdk.client.AbstractClient;
import top.osjf.assembly.simplified.sdk.client.Client;
import top.osjf.assembly.simplified.sdk.http.AbstractHttpRequestParams;
import top.osjf.assembly.simplified.sdk.http.ApacheHttpClient;
import top.osjf.assembly.simplified.sdk.http.HttpResultResponse;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.io.Serializable;
import java.util.Map;

/**
 * The definition method of the requested public node.
 *
 * <p>It includes parameter acquisition {@link RequestParamCapable} required
 * for the request, parameter verification (intercepted in the form of
 * {@link SdkException}), recording the response body type for subsequent encapsulation
 * conversion, and verifying the request header.</p>
 *
 * <p>The class object that can be rewritten to implement {@link Client} can be
 * customized for logical implementation of methods in {@link Client}.
 *
 * <p>On the basis of this interface, it can be extended, such as utilizing
 * the request pattern {@link top.osjf.assembly.simplified.sdk.http.HttpRequest}
 * of HTTP to define special methods related to HTTP.</p>
 *
 * @param <R> Implement a unified response class data type.
 * @author zpf
 * @since 1.1.0
 */
public interface Request<R extends Response> extends RequestParamCapable<Object>, Serializable {

    /**
     * Obtain the true address value of the request based on
     * the given host name.
     *
     * @param host Host name, cannot be {@literal null}.
     * @return Address at the time of request.
     */
    String getUrl(String host);

    @Override
    @CanNull
    Object getRequestParam();

    /**
     * Method for verifying request parameters, fixed throw {@link SdkException}.
     * <p>Taking {@link ApacheHttpClient} as an example, you can take
     * a look at its {@link ApacheHttpClient#request()} method. You
     * need to throw {@link SdkException} for validation parameters in
     * order to perform specialized exception capture.</p>
     */
    void validate() throws SdkException;

    /**
     * Obtain the class object of the response transformation entity,
     * implemented in {@link Response}.
     * <p>If you need a {@link Response} transformation of composite
     * generics, please refer to {@link #getResponseTypeToken()}.
     *
     * @return Return implementation for {@link Response}.
     */
    @CanNull
    default Class<R> getResponseCls() {
        return null;
    }

    /**
     * Introduced a generic help class from Google to solve the problem
     * of composite generics.
     * <p>Calling {@link TypeToken#getType()} can obtain the {@link java.lang.reflect.Type}
     * of composite generics and perform subsequent conversion operations.
     * <p>He can help you no longer need to define the corresponding class,
     * you just need to define the corresponding data based on {@link HttpResultResponse},
     * but the specific state set must comply with {@link HttpResultResponse}'s
     * regulations.
     * <p>If you need to customize {@link Response}, it will be more convenient
     * for you to use {@link #getResponseCls()}.
     *
     * @return Composite paradigm.
     * @since 2.1.2
     */
    @CanNull
    default TypeToken<R> getResponseTypeToken() {
        return null;
    }

    /**
     * Obtain the header information that needs to be set, and return a map.
     *
     * @return Maps heads.
     */
    Map<String, String> getHeadMap();

    /**
     * Obtain the implementation class object of {@link Client} and you
     * can define it yourself.
     * <p>Currently, there are {@link ApacheHttpClient} in HTTP format and
     * some default methods provided in {@link AbstractClient}.
     *
     * @return Implementation class of {@link Client}.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Client> getClientCls();

    /**
     * Based on {@link #getResponseCls()} and {@link #getResponseTypeToken()},
     * obtain the corresponding type that should be converted.
     * <p>{@link #getResponseCls()} has a higher priority than {@link #getResponseTypeToken()}.
     * <p>If neither is provided, {@link SdkUtils#getResponseRequiredType(Request)}
     * will be used for generic retrieval of the response.
     * <p><strong>The specific supported formats are shown below.</strong></p>
     * <strong>____________________________________________________________________</strong>
     * <p>The abstract {@link AbstractHttpRequestParams} provided directly is followed
     * by a response implementation with a generic type.
     * <pre>
     *     {@code
     *   class RequestParam
     *   extends AbstractHttpRequestParams<HttpResultResponse<Character>> {
     *      private static final long serialVersionUID = 6115216307330001269L;
     *         Override
     *         public HttpSdkEnum matchHttpSdk() {
     *             return null;
     *         }
     *       }
     *     }
     * </pre>
     * <strong>____________________________________________________________________</strong>
     * <p>Implement an interface that carries the main class generic request.
     * <pre>
     *     {@code
     *     interface
     *     InterHttpRequest extends HttpRequest<HttpResultResponse<String>> {
     *     }
     *     class RequestParam implements Inter {
     *         private static final long serialVersionUID = 7371775319382181179L;
     *     }
     *     }
     * </pre>
     * <strong>____________________________________________________________________</strong>
     * <p>Nested inheritance type.
     * <pre>
     *     {@code
     *      class RequestParam extends AbstractHttpRequestParams<HttpResultResponse<String>> {
     *         private static final long serialVersionUID = 6115216307330001269L;
     *         Override
     *         public HttpSdkEnum matchHttpSdk() {
     *             return null;
     *         }
     *     }
     *     class RequestParam0 extends RequestParam {
     *         private static final long serialVersionUID = 2463029032762347802L;
     *     }
     *     }
     * </pre>
     * <strong>____________________________________________________________________</strong>
     * <p>Nested implementation type.
     * <pre>
     *     {@code
     *     class RequestParam implements HttpRequest<HttpResultResponse<String>> {
     *         private static final long serialVersionUID = 6115216307330001269L;
     *         ...
     *     }
     *     class RequestParam1 extends RequestParam {
     *         private static final long serialVersionUID = 2463029032762347802L;
     *     }
     *     }
     * <strong>____________________________________________________________________</strong>
     * </pre>
     *
     * @return type object.
     * @see SdkUtils#getResponseRequiredType(Request)
     * @since 2.1.2
     */
    @NotNull
    @SuppressWarnings({"unchecked"})
    default Object getResponseRequiredType() {
        Object type;
        if ((type = getResponseCls()) != null) return type;
        if ((type = getResponseTypeToken()) != null) return ((TypeToken<R>) type).getType();
        //If you have not provided the corresponding types of
        // acquisition methods for the above two suggestions,
        // then the following will dynamically obtain them for you.
        return SdkUtils.getResponseRequiredType(this);
    }

    /**
     * When {@link #getResponseCls()} and {@link #getResponseTypeToken()}
     * are not provided, attempting to obtain the generic indicators of
     * an inherited class or interface must satisfy the requirement that
     * it is a subclass of the requesting main interface, and the criteria
     * for determining whether it is true need to be determined through this method.
     *
     * @param clazz Determine type.
     * @return If {@code true} is judged successful, {@code false} otherwise .
     * @since 2.1.3
     */
    default boolean isAssignableRequest(Class<?> clazz) {
        return Request.class.isAssignableFrom(clazz);
    }
}
