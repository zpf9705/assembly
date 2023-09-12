package top.osjf.assembly.sdk;

import copy.cn.hutool.v_5819.core.util.ReflectUtil;
import org.springframework.lang.NonNull;
import top.osjf.assembly.sdk.client.AbstractClient;
import top.osjf.assembly.sdk.client.Client;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.Response;

import java.util.function.Supplier;

/**
 * Request the client tool class to route parameters {@link Request} for simple and efficient resolution of request.
 * <p>Defined as an abstract class, it can be inherited and used, but cannot be instantiated.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class ClientUtils {

    /**
     * The static method executed by SDK is executed through the host name and {@link Request} parameters.
     *
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a defined {@link Response} class object.
     */
    public static <R extends Response> R execute(String host, Request<R> request) {
        return getClient(host, request).request();
    }

    /**
     * The static method executed by SDK is functionally placed in the host name {@link Supplier}
     * and executed through the host name and {@link Request} parameters.
     *
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a defined {@link Response} class object.
     */
    public static <R extends Response> R execute(@NonNull Supplier<String> host, Request<R> request) {
        return execute(host.get(), request);
    }

    /**
     * Obtain or cache and obtain a {@link Client} client.
     *
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a single instance {@link Client} distinguished by a URL.
     */
    @SuppressWarnings("unchecked")
    private static <R extends Response> Client<R> getClient(String host, Request<R> request) {
        return AbstractClient.getClient(() -> {
            //Building client objects through reflection based on client type (provided that they are not cached)
            return ReflectUtil.newInstance(request.getClientCls(),
                    //Format Request Address
                    request.formatUrl(host));
        }, request, host);
    }
}
