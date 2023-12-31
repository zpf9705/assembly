package top.osjf.assembly.simplified.sdk.client;

import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.simplified.sdk.process.Response;
import top.osjf.assembly.util.lang.ReflectUtils;

/**
 * Some access operations related to {@link Client}.
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class ClientAccess {

    /**
     * Obtain or cache and obtain a {@link Client} client.
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
            return ReflectUtils.newInstance(request.getClientCls(), key);
        }, request, key);
    }
}
