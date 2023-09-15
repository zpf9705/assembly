package top.osjf.assembly.sdk.client;

import top.osjf.assembly.sdk.ClientAccess;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.Response;

/**
 * Regarding the request executor of {@link Client}, instantiation is not allowed and only its
 * static methods can be called.
 *
 * @see ClientAccess
 * @see AbstractClient
 * @author zpf
 * @since 1.1.1
 */
public final class ClientExecutors extends ClientAccess {

    private ClientExecutors() {
    }

    public static Response executeRequestClient(String host, Request<?> request) {
        return cacheClientAndRequest(host, request);
    }

    @SuppressWarnings("rawtypes")
    private static Response cacheClientAndRequest(String key, Request<?> request) {
        Class<? extends Client> clientCls = request.getClientCls();
        if (clientCls == null) throw new IllegalArgumentException(
                "The execution client of the client cannot be empty.");
        Client<?> andSetClient = getAndSetClient(key, request);
        return andSetClient.request();
    }
}
