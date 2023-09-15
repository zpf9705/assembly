package top.osjf.assembly.sdk.client;

import top.osjf.assembly.sdk.http.DefaultHttpClient;
import top.osjf.assembly.sdk.http.HttpClientExecutor;
import top.osjf.assembly.sdk.http.HttpRequest;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.Response;
import top.osjf.assembly.sdk.rpc.DefaultRpcClient;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * {@link Client} Related selection help.
 *
 * @author zpf
 * @since 1.1.1
 */
public class ClientShoulder {

    static final String http = DefaultHttpClient.class.getName();

    static final String rpc = DefaultRpcClient.class.getName();

    /**
     * Select the corresponding execution tool by providing the full name of the class object for {@link Client},
     * along with the initialization name {@link #http}, such as {@code top.osjf.assembly.sdk.http.HttpClient}
     * applicable to {@link HttpClientExecutor}.
     *
     * @param clientCls Link extends for {@link Client} clazz.
     * @return Return a {@link BiFunction} to finish executor.
     */
    @SuppressWarnings("rawtypes")
    public static BiFunction<String, Request<?>, Response> findClientConsumer(Class<? extends Client> clientCls) {
        Objects.requireNonNull(clientCls, "Client clazz not be null");
        BiFunction<String, Request<?>, Response> function = null;
        if (Objects.equals(clientCls.getName(), http)) {
            function = (s, r) -> HttpClientExecutor.execute(s, (HttpRequest<?>) r);
        }
        return function;
    }
}
