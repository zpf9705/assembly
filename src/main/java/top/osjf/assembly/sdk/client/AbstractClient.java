package top.osjf.assembly.sdk.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONValidator;
import copy.cn.hutool.v_5819.core.collection.CollectionUtil;
import copy.cn.hutool.v_5819.core.util.StrUtil;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import top.osjf.assembly.sdk.process.DefaultResponse;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.Response;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * The abstract class implementation of {@link Client} records some common processing and parameter
 * operations, as well as default operations, including {} caching based on the request identifier
 * (HTTP request form is the request URL, and the rest can be customized with a unique request identifier)
 * (every time the request parameter is changed, it is cleared after the request, and {@link Client} can
 * easily obtain the request data during the request process).
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
    private static final NamedThreadLocal<Request> PARAM_NAMED_SAVER = new NamedThreadLocal<>("CURRENT API CLIENT SAVER");

    // empty json object
    static final String empty_json = "{}";

    //Requested remote interface address
    private final String url;

    public String getUrl() {
        return url;
    }

    /* ******* Constructs ***********/

    public AbstractClient(String url) {
        Assert.hasText(url, "RequestUrl can not be null !");
        this.url = url;
    }

    /* ******* Static ***********/

    /**
     * Get Cache for {@link Client}.
     *
     * @param url Cache link url.
     * @param <R> Data Generics for {@link Response}.
     * @return {@link Client} and maybe be {@link null}.
     */
    static <R extends Response> Client<R> getCacheClient(String url) {
        if (StrUtil.isBlank(url)) {
            return null;
        }
        return CLIENT_CACHE.get(url);
    }

    /**
     * Caching {@link Client} with sign url and real {@link Client}.
     *
     * @param url    Cache link url.
     * @param client Real impl in {@link Client}.
     */
    static void cache(String url, Client client) {
        if (StrUtil.isBlank(url) || client == null) {
            return;
        }
        CLIENT_CACHE.putIfAbsent(url, client);
    }

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
     * Obtain the current request parameters, which is the implementation class of {@link Request}.
     *
     * @param <R> Data Generics for {@link Response}.
     * @return Actual {@link Request} implementation.
     */
    public static <R extends Response> Request<R> getCurrentRequest() {
        return PARAM_NAMED_SAVER.get();
    }

    /**
     * Retrieve the cache implementation of {@link Client} from the cache {@link #CLIENT_CACHE}.
     *
     * @param newClientSupplier New client provider,if not found, add it directly.
     * @param request           {@link Request} class model parameters of API.
     * @param host              Request the host name to obtain the actual URL, please refer to
     *                          {@link Request#formatUrl(String)} for details.
     * @param <R>               Data Generics for {@link Response}.
     * @return {@link Client} 's singleton object, persistently requesting.
     */
    public static <R extends Response> Client<R> getClient(Supplier<Client<R>> newClientSupplier,
                                                           Request<R> request,
                                                           String host) {
        Assert.hasText(host, "Api request host can not be null !");
        Assert.notNull(request, "Api request params can not be null !");
        setCurrentParam(request);
        String url = request.formatUrl(host);
        Client<R> client = getCacheClient(url);
        if (client == null) {
            synchronized (lock) {
                client = getCacheClient(url);
                if (client == null) {
                    cache(url, newClientSupplier.get());
                    client = getCacheClient(url);
                }
            }
        }
        return client;
    }

    /* ******* Default provider ***********/

    @Override
    @NonNull
    public String preResponseStrHandler(Request<R> request, String responseStr) {
        return responseStr;
    }

    @Override
    @NonNull
    public R convertToResponse(Request<R> request, String responseStr) {
        R response;
        JSONValidator jsonValidator = StrUtil.isBlank(responseStr) ? null : JSONValidator.from(responseStr);
        if (Objects.isNull(jsonValidator) || !jsonValidator.validate()) {
            String jsonData = JSON.toJSONString(DefaultResponse.buildDataErrorResponse(responseStr));
            response = JSON.parseObject(jsonData, request.getResponseCls());
        } else if (Objects.equals(JSONValidator.Type.Array, jsonValidator.getType())) {
            List<R> responses = JSONArray.parseArray(responseStr, request.getResponseCls());
            if (CollectionUtil.isEmpty(responses)) {
                response = responses.get(0);
            } else {
                response = JSON.parseObject(empty_json, request.getResponseCls());
            }
        } else {
            response = JSON.parseObject(responseStr, request.getResponseCls());
        }
        return response;
    }

    @Override
    public void close() {
        setCurrentParam(null);
    }
}
