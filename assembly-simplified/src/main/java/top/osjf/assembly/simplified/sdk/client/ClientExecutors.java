package top.osjf.assembly.simplified.sdk.client;

import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.simplified.sdk.process.Response;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Regarding the request executor of {@link Client}, instantiation is not
 * allowed and only its static methods can be called.
 *
 * @author zpf
 * @see ClientAccess
 * @see AbstractClient
 * @since 1.1.1
 */
public final class ClientExecutors extends ClientAccess {

    private ClientExecutors() {
    }

    /**
     * The static method executed by SDK is functionally placed in
     * the host name {@link Supplier} and executed through the host
     * name and {@link Request} parameters.
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
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a defined {@link Response} class object.
     */
    public static <R extends Response> R executeRequestClient(String host, Request<R> request) {
        try (Client<R> client = getAndSetClient(request.getUrl(host), request)) {
            return client.request();
        } catch (/*update since 2.1.0*/IOException e) {
            throw new ClientRuntimeCloseException(e);
        }
    }
}
