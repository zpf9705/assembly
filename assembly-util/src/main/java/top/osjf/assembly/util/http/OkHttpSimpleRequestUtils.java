package top.osjf.assembly.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson2.util.IOUtils;
import okhttp3.*;
import okhttp3.internal.http.HttpMethod;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import top.osjf.assembly.util.UtilException;

import java.util.HashMap;
import java.util.Map;

/**
 * The Square's HTTP client request tool class mainly includes four request methods: post, get, put, and del.
 * <p>And other methods that can be customized with HTTP support, link to method
 * {@link #doRequest(OkHttpClient, Request.Builder, Map)} }</p>
 * <p>
 * This class is a simple request tool for {@link OkHttpClient} and does not provide any other special functions.
 * <p>If necessary, please implement it yourself.</p>
 * <p>Only suitable for use in this project.</p>
 * <p>
 * You need to note that the {@code montage} parameter determines whether parameters that are
 * not {@literal null} need to be concatenated after the URL in the form of key/value,
 * and you need to pay attention to the concatenation rules of the
 * {@link #getRequestBuilder(String, Object, boolean, Map, String)}} method and the
 * format rules of the parameters when the parameter is <pre>{@code montage == true}</pre>.
 *
 * @author zpf
 * @since 2.0.0
 */
public abstract class OkHttpSimpleRequestUtils {

    private OkHttpSimpleRequestUtils() {
        super();
    }

    /**
     * Square's HTTP request for {@code Get}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     * @throws Exception Unknown exception.
     */
    public static String get(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "GET"),
                headers);
    }

    /**
     * Square's HTTP request for {@code Post}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value
     * @throws Exception Unknown exception.
     */
    public static String post(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "POST"), headers);
    }

    /**
     * Square's HTTP request for {@code Put}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value
     * @throws Exception Unknown exception.
     */
    public static String put(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "PUT"),
                headers);
    }

    /**
     * Square's HTTP request for {@code Delete}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     * @throws Exception Unknown exception.
     */
    public static String delete(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "DELETE"),
                headers);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client  Square's HTTP request client,can be {@literal null}.
     * @param builder HTTP Public Request Class {@link Request.Builder}.
     * @param headers Header information map,can be {@literal null}.
     * @return The {@code String} type of the return value
     * @throws Exception Unknown exception.
     */
    public static String doRequest(OkHttpClient client,
                                   Request.Builder builder,
                                   Map<String, String> headers) throws Exception {
        if (client == null) {
            client = new OkHttpClient().newBuilder().build();
        }
        Response response = null;
        String result;
        try {
            addHeaders(headers, builder);
            response = client.newCall(builder.build()).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    result = body.string();
                } else {
                    result = "";
                }
            } else {
                throw new ResponseFailedException(response.message());
            }
        } finally {
            IOUtils.close(response);
        }
        return result;
    }

    /**
     * Add header information for HTTP Request.
     *
     * @param headers Header information map,can be {@literal null}.
     * @param builder HTTP Public Request Class {@link Request.Builder}.
     */
    public static void addHeaders(Map<String, String> headers, Request.Builder builder) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    /**
     * Use relevant parameters to obtain the {@link Request} parameters required for {@link OkHttpClient} execution.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @param headers      Header information map,can be {@literal null}.
     * @param method       Distinguish request types,must not be {@literal null}.
     * @return Obtain the condition builder for {@link Request}.
     */
    @SuppressWarnings("unchecked")
    public static Request.Builder getRequestBuilder(String url, Object requestParam, boolean montage,
                                                    Map<String, String> headers,
                                                    String method) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            throw new IllegalArgumentException("Url is not valid");
        }
        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        boolean montageOn = false;
        if (montage && requestParam != null) {
            montageOn = true;
            if (!(requestParam instanceof Map) && !(requestParam instanceof String))
                throw new UtilException("If you need to concatenate parameters onto the HttpUrl.Builder to addQueryParameter, " +
                        "please provide parameters of map type or JSON type of key/value " +
                        "(which will automatically convert map concatenation). " +
                        "If you provide a simple string type, then the URL parameter will be directly returned.");
            Map<String, Object> params = new HashMap<>();
            if (requestParam instanceof Map) {
                params.putAll((Map<String, Object>) requestParam);
            } else {
                String var = requestParam.toString();
                if (JSONValidator.from(var).validate()) {
                    JSONObject jsonObject = JSON.parseObject(var);
                    params.putAll(jsonObject.getInnerMap());
                }
            }
            if (MapUtils.isNotEmpty(params)) {
                for (String paramKey : params.keySet()) {
                    urlBuilder.addQueryParameter(paramKey, String.valueOf(params.get(paramKey)));
                }
            }
        }
        Request.Builder requestBuild = new Request.Builder().url(urlBuilder.build());
        boolean permits = HttpMethod.requiresRequestBody(method);
        // If the parameters have already been concatenated onto the URL, then this type of requirement does not
        // allow for further setting of the request body.
        // However, according to the okhttp specification,
        // if {@link HttpMethod#requiresRequestBody(String)} is met, the request body must be required,
        // so you need to weigh it at this point.
        if (montageOn) {
            if (permits) throw new IllegalStateException(
                    "According to the specification requirements of OKHTTP, if you concatenate parameter " +
                            "links, you must meet the current request type that does not require a body," +
                            "which can be seen in the class [okhttp3.internal.http.HttpMethod]");
        } else {
            if (permits) {
                if (requestParam == null) throw new IllegalStateException(
                        "According to the specification requirements of okhttp, " +
                                "the current request type must have a request body, which can be seen " +
                                "in the class [okhttp3.internal.http.HttpMethod]");
            }
        }
        String value;
        if (MapUtils.isNotEmpty(headers)) {
            value = headers.get("Content-type");
            if (StringUtils.isBlank(value)) {
                value = ContentType.APPLICATION_JSON.getMimeType();
            }
        } else {
            value = ContentType.APPLICATION_JSON.getMimeType();
        }
        value = value.concat(";charset=utf-8");
        RequestBody body = RequestBody.create(MediaType.parse(value),
                requestParam == null ? "" : requestParam.toString());
        switch (method) {
            case "GET":
                requestBuild = requestBuild.get();
                break;
            case "POST":
                requestBuild = requestBuild.post(body);
                break;
            case "PUT":
                requestBuild = requestBuild.put(body);
                break;
            case "DELETE":
                requestBuild = requestBuild.delete(body);
                break;
        }
        return requestBuild;
    }
}
