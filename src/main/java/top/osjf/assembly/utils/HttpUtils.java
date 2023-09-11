package top.osjf.assembly.utils;

import copy.cn.hutool.v_5819.core.collection.CollectionUtil;
import copy.cn.hutool.v_5819.core.exceptions.UtilException;
import copy.cn.hutool.v_5819.core.io.IoUtil;
import copy.cn.hutool.v_5819.core.util.StrUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * The Apache HTTP client request tool class mainly includes four request methods: post, get, put, and del.
 *
 * @author zpf
 * @since 1.1.0
 */
public final class HttpUtils {

    private HttpUtils() {
        super();
    }

    /**
     * Apache HTTP request for {@code Get}.
     *
     * @param url     Request String url
     * @param headers Header information
     * @param params  Get Splice Request Parameters
     * @return The {@code String} type of the return value
     */
    public static String get(String url, Map<String, String> headers, Map<String, Object> params) {
        return doRequest(new HttpGet(getURIByUrlAndParams(url, params)), headers, null);
    }

    /**
     * Apache HTTP request for {@code Post}.
     *
     * @param url     Request String url
     * @param json    JSON data input parameter
     * @param headers Header information
     * @return The {@code String} type of the return value
     */
    public static String post(String url, Map<String, String> headers, String json) {
        return doRequest(new HttpPost(url), headers, json);
    }

    /**
     * Apache HTTP request for {@code Put}.
     *
     * @param url     Request String url
     * @param headers Header information
     * @param json    JSON data input parameter
     * @return The {@code String} type of the return value
     */
    public static String put(String url, Map<String, String> headers, String json) {
        return doRequest(new HttpPut(url), headers, json);
    }

    /**
     * Apache HTTP request for {@code Delete}.
     *
     * @param url     Request String url
     * @param headers Header information
     * @return The {@code String} type of the return value
     */
    public static String delete(String url, Map<String, String> headers) {
        return doRequest(new HttpDelete(url), headers, null);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}.
     * @param headers     Header information map.
     * @param json        JSON data input parameter
     * @return The {@code String} type of the return value
     */
    public static String doRequest(@NonNull HttpRequestBase requestBase,
                                   Map<String, String> headers,
                                   String json) {
        CloseableHttpClient client = HttpClients.custom().build();
        CloseableHttpResponse response = null;
        String result;
        try {
            addHeaders(headers, requestBase);
            setEntity(json, requestBase, headers);
            response = client.execute(requestBase);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            throw new UtilException(e.getMessage());
        } finally {
            IoUtil.close(response);
            IoUtil.close(client);
        }
        return result;
    }

    /**
     * Set {@link HttpEntity}.
     *
     * @param json        JSON data input parameter
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}
     * @param header      Header information map.
     */
    private static void setEntity(String json, HttpRequestBase requestBase, Map<String, String> header) {
        if (StrUtil.isNotBlank(json) && requestBase instanceof HttpEntityEnclosingRequestBase) {
            StringEntity stringEntity;
            if (CollectionUtil.isNotEmpty(header)) {
                String value = header.get("Content-type");
                if (StrUtil.isNotBlank(value)) {
                    stringEntity = new StringEntity(json, StandardCharsets.UTF_8);
                } else {
                    stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
                }
            } else {
                stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            }
            HttpEntityEnclosingRequestBase base = (HttpEntityEnclosingRequestBase) requestBase;
            base.setEntity(stringEntity);
        }
    }

    /**
     * Add this request body information body.
     *
     * @param headers     Header information map.
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}.
     */
    private static void addHeaders(Map<String, String> headers, HttpRequestBase requestBase) {
        if (CollectionUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBase.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    /**
     * Splice Get Request Address.
     *
     * @param url    Request URL address
     * @param params ? Rear splicing parameters
     * @return URL {@link URI}
     */
    private static URI getURIByUrlAndParams(String url, Map<String, Object> params) {
        URI uri;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (CollectionUtil.isNotEmpty(params)) {
                for (String paramKey : params.keySet()) {
                    uriBuilder.addParameter(paramKey, String.valueOf(params.get(paramKey)));
                }
            }
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new UtilException(url + " no a valid url");
        }
        return uri;
    }
}
