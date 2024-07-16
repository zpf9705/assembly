package top.osjf.sdk.core.client;

import cn.hutool.core.util.ReflectUtil;
import top.osjf.sdk.core.exception.ClientRuntimeCloseException;
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Regarding the request executor of {@link Client}, instantiation is not
 * allowed and only its static methods can be called.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ClientExecutors {

    private ClientExecutors() {
        throw new AssertionError("No instance for you !");
    }

    /**
     * The static method executed by SDK is functionally placed in
     * the host name {@link Supplier} and executed through the host
     * name and {@link Request} parameters.
     *
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK link to {@link Supplier}.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a defined {@link Response} class object.
     */
    public static <R extends Response> R executeRequestClient(Supplier<String> host, Request<R> request) {
        return executeRequestClient(host.get(), request);
    }

    /**
     * The static method executed by SDK is executed through the
     * host name and {@link Request} parameters.
     *
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a defined {@link Response} class object.
     */
    public static <R extends Response> R executeRequestClient(String host, Request<R> request) {
        try (Client<R> client = getAndSetClient(request.getUrl(host), request)) {
            return client.request();
        } catch (IOException e) {
            throw new ClientRuntimeCloseException(e);
        }
    }

    /**
     * Returns a {@link Client} client using a {@link java.net.URL},
     * If it does not exist, it will be added to the cache.
     *
     * @param request {@link Request} class model parameters of API.
     * @param key     Cache a single {@link Client} to the key value of the map static cache.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a single instance {@link Client} distinguished by a key.
     */
    @SuppressWarnings("unchecked")
    public static <R extends Response> Client<R> getAndSetClient(String key, Request<R> request) {
        return AbstractClient.getAndSetClient(() -> {
            //Building client objects through reflection based on client type (provided that they are not cached)
            return ReflectUtil.newInstance(request.getClientCls(), key);
        }, request, key);
    }
}
