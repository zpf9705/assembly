package top.osjf.assembly.simplified.sdk.process;

import com.google.common.reflect.TypeToken;
import top.osjf.assembly.simplified.sdk.SdkException;
import top.osjf.assembly.simplified.sdk.client.AbstractClient;
import top.osjf.assembly.simplified.sdk.client.Client;
import top.osjf.assembly.simplified.sdk.http.ApacheHttpClient;
import top.osjf.assembly.simplified.sdk.http.HttpResultResponse;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
     * @return Composite paradigm.
     * @since 2.1.2
     */
    @CanNull
    default TypeToken<R> getResponseTypeToken() {
        return null;
    }

    /**
     * Obtain the header information that needs to be set, and return a map.
     * @return Maps heads.
     */
    Map<String, String> getHeadMap();

    /**
     * Obtain the implementation class object of {@link Client} and you
     * can define it yourself.
     * <p>Currently, there are {@link ApacheHttpClient} in HTTP format and
     * some default methods provided in {@link AbstractClient}.
     * @return Implementation class of {@link Client}.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Client> getClientCls();

    /**
     * Based on {@link #getResponseCls()} and {@link #getResponseTypeToken()},
     * obtain the corresponding type that should be converted.
     * <p>{@link #getResponseCls()} has a higher priority than {@link #getResponseTypeToken()}.
     * <p>If neither is provided, firstly, implement the logic of manually querying generics,
     * as referenced in {@code TypeCapture#capture()};finally,the default {@link HttpResultResponse}
     * type is given.
     * @since 2.1.2
     * @return type object.
     */
    @NotNull
    default Object getResponseRequiredType() {
        Object type;
        Class<R> responseCls = getResponseCls();
        if (responseCls == null) {
            TypeToken<R> typeToken = getResponseTypeToken();
            if (typeToken == null) {
                Type genericSuperclass = getClass().getGenericSuperclass();
                if (genericSuperclass instanceof ParameterizedType) {
                    Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass)
                            .getActualTypeArguments();
                    if (ArrayUtils.isNotEmpty(actualTypeArguments)) {
                        type = actualTypeArguments[0];
                    } else {
                        type = HttpResultResponse.class;
                    }
                } else {
                    type = HttpResultResponse.class;
                }
            } else {
                type = typeToken.getType();
            }
        } else {
            type = responseCls;
        }
        return type;
    }
}
