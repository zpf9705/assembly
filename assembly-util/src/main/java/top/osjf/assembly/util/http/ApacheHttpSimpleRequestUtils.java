package top.osjf.assembly.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import top.osjf.assembly.util.UtilException;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.io.IoUtils;
import top.osjf.assembly.util.json.FastJsonUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * The Apache HTTP client request tool class mainly includes four request methods: post, get, put, and del.
 *
 * <p>And other methods that can be customized with HTTP support, link to method {@link
 * #doRequest(CloseableHttpClient, HttpRequestBase, Map, Object)}</p>
 *
 * <p>This class is a simple request tool for {@link CloseableHttpClient} and does not provide any other special
 * functions.
 *
 * <p>If necessary, please implement it yourself.</p>
 *
 * <p>Only suitable for use in this project.</p>
 *
 * <p>You need to note that the {@code montage} parameter determines whether parameters that are
 * not {@literal null} need to be concatenated after the URL in the form of key/value,
 * and you need to pay attention to the concatenation rules of the {@link #getUri(String, Object, boolean)}
 * method and the format rules of the parameters when the parameter is <pre>{@code montage == true}</pre>.
 *
 * @author zpf
 * @since 1.0.0
 */
public abstract class ApacheHttpSimpleRequestUtils {

    private ApacheHttpSimpleRequestUtils() {
        super();
    }

    /**
     * Apache HTTP request for {@code Get}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value
     * @throws Exception Unknown exception.
     */
    public static String get(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, new HttpGet(getUri(url, requestParam, montage)), headers, requestParam);
    }

    /**
     * Apache HTTP request for {@code Post}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpPost(getUri(url, requestParam, montage)), headers, requestParam);
    }

    /**
     * Apache HTTP request for {@code Put}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpPut(getUri(url, requestParam, montage)), headers, requestParam);
    }

    /**
     * Apache HTTP request for {@code Delete}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpDelete(getUri(url, requestParam, montage)), headers, requestParam);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client       Apache's HTTP request client.
     * @param requestBase  HTTP Public Request Class {@link HttpRequestBase}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @return The {@code String} type of the return value
     * @throws Exception Unknown exception.
     */
    public static String doRequest(CloseableHttpClient client,
                                   @NotNull HttpRequestBase requestBase,
                                   Map<String, String> headers,
                                   Object requestParam) throws Exception {
        if (client == null) {
            client = HttpClients.custom().build();
        }
        CloseableHttpResponse response = null;
        String result;
        try {
            addHeaders(headers, requestBase);
            setEntity(requestParam, requestBase, headers);
            response = client.execute(requestBase);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } finally {
            IoUtils.close(response);
            IoUtils.close(client);
        }
        return result;
    }

    /**
     * Set {@link HttpEntity} with a {@link StringEntity}.
     *
     * @param requestParam Request parameters,can be {@literal null}.
     * @param requestBase  HTTP Public Request Class {@link HttpRequestBase}
     * @param headers      Header information map,can be {@literal null}.
     */
    public static void setEntity(Object requestParam, HttpRequestBase requestBase, Map<String, String> headers) {
        if (requestParam == null || !(requestBase instanceof HttpEntityEnclosingRequestBase)) {
            return;
        }
        String paramOfString = requestParam.toString();
        StringEntity stringEntity;
        if (CollectionUtils.isNotEmpty(headers)) {
            String value = headers.get("Content-type");
            if (StringUtils.isNotBlank(value)) {
                stringEntity = new StringEntity(requestParam.toString(), StandardCharsets.UTF_8);
            } else {
                stringEntity = new StringEntity(paramOfString, ContentType.APPLICATION_JSON);
            }
        } else {
            stringEntity = new StringEntity(paramOfString, ContentType.APPLICATION_JSON);
        }
        HttpEntityEnclosingRequestBase base = (HttpEntityEnclosingRequestBase) requestBase;
        base.setEntity(stringEntity);
    }

    /**
     * Add header information for HTTP requests.
     *
     * @param headers     Header information map,can be {@literal null}.
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}.
     */
    public static void addHeaders(Map<String, String> headers, HttpRequestBase requestBase) {
        if (CollectionUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBase.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    /**
     * The get request for building HTTP contains the construction object of {@link URI}.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return Uri object,Please pay attention to the format issue of the URL.
     * @throws Exception unknown exception.
     */
    @SuppressWarnings("unchecked")
    public static URI getUri(String url, Object requestParam, boolean montage) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (montage && requestParam != null) {
            if (!(requestParam instanceof Map)
                    && !(requestParam instanceof String))
                throw new UtilException("If you need to concatenate parameters onto the URL, " +
                        "please provide parameters of map type or JSON type of key/value " +
                        "(which will automatically convert map concatenation). " +
                        "If you provide a simple string type, then the URL parameter will be directly returned.");
            //If the type is a map concatenated after the address
            Map<String, Object> params = null;
            if (requestParam instanceof Map) {
                params = (Map<String, Object>) requestParam;
            } else {
                String jsonStr = requestParam.toString();
                if (FastJsonUtils.isValid(jsonStr)) {
                    params = FastJsonUtils.getInnerMapByJsonStr(jsonStr);
                }
            }
            if (CollectionUtils.isNotEmpty(params)) {
                for (String paramKey : params.keySet()) {
                    uriBuilder.addParameter(paramKey, String.valueOf(params.get(paramKey)));
                }
            }
        }
        //If it is null or a string, it can be directly used as a paparazzi
        return uriBuilder.build();
    }
}
