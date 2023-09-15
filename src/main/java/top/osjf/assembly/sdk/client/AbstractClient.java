package top.osjf.assembly.sdk.client;

import copy.cn.hutool.v_5819.core.util.StrUtil;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 *
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractClient<R extends Response> implements Client<R> {

    private static final Object lock = new Object();

    //Cache request clients for each request object to prevent memory waste caused by multiple new requests
    private static final Map<String, Client> CLIENT_CACHE = new ConcurrentHashMap<>(16);

    //Save each request parameter and use it for subsequent requests
    private static final NamedThreadLocal<Request> PARAM_NAMED_SAVER = new NamedThreadLocal<>("CURRENT CLIENT SAVER");

    /* ******* Constructs ***********/

    public AbstractClient(String key) {
        Assert.hasText(key, "Key not be null");
        cache(key, this);
    }

    /**
     * Caching {@link Client} with sign url and real {@link Client}.
     *
     * @param url    Cache link url.
     * @param client Real impl in {@link Client}.
     */
    void cache(String url, Client client) {
        if (StrUtil.isBlank(url) || client == null) {
            return;
        }
        CLIENT_CACHE.putIfAbsent(url, client);
    }

    /* ******* Static ***********/

    /**
     * Put the current requested parameters into thread private variable storage.
     *
     * @param <R>     Data Generics for {@link Response}.
     * @param request The parameter model of the current request is an implementation of {@link Request}.
     */
    static <R extends Response> void setCurrentParam(Request<R> request) {
        if (request == null) {
            PARAM_NAMED_SAVER.remove();
        } else {
            PARAM_NAMED_SAVER.set(request);
        }
    }

    /**
     * Retrieve the cache implementation of {@link Client} from the cache {@link #CLIENT_CACHE}.
     *
     * @param newClientSupplier New client provider,if not found, add it directly.
     * @param request           {@link Request} class model parameters of API.
     * @param key               Cache a single {@link Client} to the key value of the map static cache.
     * @param <R>               Data Generics for {@link Response}.
     * @return {@link Client} 's singleton object, persistently requesting.
     */
    public static <R extends Response> Client<R> getAndSetClient(Supplier<Client<R>> newClientSupplier,
                                                                 Request<R> request,
                                                                 String key) {
        Assert.hasText(key, "Key not be null");
        Assert.notNull(request, "Request not be null");
        setCurrentParam(request);
        Client<R> client = CLIENT_CACHE.get(key);
        if (client == null) {
            synchronized (lock) {
                client = CLIENT_CACHE.get(key);
                if (client == null) {
                    client = newClientSupplier.get();
                }
            }
        }
        return client;
    }

    /**
     * Obtain the current request parameters, which is the implementation class of {@link Request}.
     *
     * @return Actual {@link Request} implementation.
     */
    public Request<R> getCurrentRequest() {
        return PARAM_NAMED_SAVER.get();
    }

    @Override
    public void close() {
        setCurrentParam(null);
    }
}
