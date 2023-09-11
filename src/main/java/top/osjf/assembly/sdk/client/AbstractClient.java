package top.osjf.assembly.sdk.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONValidator;
import com.sun.istack.internal.NotNull;
import copy.cn.hutool.v_5819.core.collection.CollectionUtil;
import copy.cn.hutool.v_5819.core.util.StrUtil;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Abstract client auxiliary class, where there are single instance clients cached
 * based on URL addresses to prevent memory consumption caused by duplicate object
 * creation, improve memory utilization, and some common method definitions
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractClient<R extends Response> implements Client<R> {

    private static final long serialVersionUID = 8084820515803632476L;

    private static final Object lock = new Object();

    //Cache request clients for each request object to prevent memory waste caused by multiple new requests
    private static final Map<String, Client> CLIENT_CACHE = new ConcurrentHashMap<>(16);

    //Save each request parameter and use it for subsequent requests
    private static final NamedThreadLocal<Request> PARAM_NAMED_SAVER = new NamedThreadLocal<>("CURRENT API CLIENT SAVER");

    // empty json object
    static final String empty_json = "{}";

    //Requested remote interface address
    private final String url;

    /* ******* Constructs ***********/

    public AbstractClient(String url) {
        Assert.hasText(url, "RequestUrl can not be null !");
        this.url = url;
    }

    /**
     * Get Cache Client
     *
     * @param url Link Address
     * @return {@link AbstractClient}
     */
    static <R extends Response> Client<R> getCacheClient(String url) {
        if (StrUtil.isBlank(url)) {
            return null;
        }
        return CLIENT_CACHE.get(url);
    }

    /**
     * Caching client objects
     *
     * @param url    Link Address
     * @param client {@link AbstractClient}
     */
    static void cache(String url, Client client) {
        if (StrUtil.isBlank(url) || client == null) {
            return;
        }
        CLIENT_CACHE.putIfAbsent(url, client);
    }

    /**
     * Put the current requested parameters into thread private variable storage
     *
     * @param request Current request parameters
     */
    static <R extends Response> void setCurrentParam(Request<R> request) {
        if (request == null) {
            PARAM_NAMED_SAVER.remove();
        } else {
            PARAM_NAMED_SAVER.set(request);
        }
    }

    /**
     * Get the current thread variable
     *
     * @param <R> Data Generics
     * @return response data
     */
    public static <R extends Response> Request<R> getCurrentParam() {
        return PARAM_NAMED_SAVER.get();
    }

    /**
     * Obtain or initialize the client object
     *
     * @param newClientSupplier New client provider
     * @param request           Request Object Parameters
     * @param host              Link host Address
     * @param <R>               Object Generics
     * @return Client singleton client
     */
    public static <R extends Response> Client<R> getClient(Supplier<Client<R>> newClientSupplier,
                                                           Request<R> request,
                                                           String host) {
        Assert.hasText(host,
                "Api request host can not be null !");
        Assert.notNull(request,
                "Api request params can not be null !");
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

    public String getUrl() {
        return url;
    }

    @Override
    public String convert(Request<R> request, String responseStr) {
        //Perform JSON conversion according to the return type, and the final processing item is JSON
        if (!request.isrResponseJsonType()) {
            if (request.isrResponseXmlType()) {
                responseStr = request.xmlConvert().apply(responseStr);
            }
        }
        //Special conversion requirements
        if (request.specialConvert() != null) {
            responseStr = request.specialConvert().apply(responseStr);
        }
        return responseStr;
    }

    @Override
    @NotNull
    public R JsonToConvertResponse(Request<R> request, String responseStr) {
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
