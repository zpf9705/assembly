package top.osjf.assembly.sdk.http;

import org.springframework.lang.NonNull;
import top.osjf.assembly.sdk.ClientAccess;
import top.osjf.assembly.sdk.process.Request;

import java.util.function.Supplier;

/**
 * Request the client tool class to route parameters {@link HttpRequest} for simple and efficient resolution of request.
 * <p>Defined as an abstract class, it can be inherited and used, but cannot be instantiated.</p>
 *
 * @author zpf
 * @since 1.1.1
 */
public class HttpClientExecutor extends ClientAccess {

    /**
     * The static method executed by SDK is executed through the host name and {@link Request} parameters.
     *
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK.
     * @param <R>     Data Generics for {@link HttpResponse}.
     * @return Returns a defined {@link HttpResponse} class object.
     */
    public static <R extends HttpResponse> R execute(String host, HttpRequest<R> request) {
        return getAndSetClient(request.formatUrl(host), request).request();
    }

    /**
     * The static method executed by SDK is functionally placed in the host name {@link Supplier}
     * and executed through the host name and {@link Request} parameters.
     *
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK.
     * @param <R>     Data Generics for {@link HttpResponse}.
     * @return Returns a defined {@link HttpResponse} class object.
     */
    public static <R extends HttpResponse> R execute(@NonNull Supplier<String> host, HttpRequest<R> request) {
        return execute(host.get(), request);
    }
}
