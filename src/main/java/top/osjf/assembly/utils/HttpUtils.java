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
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map.
     * @param requestParam Request parameters.
     * @return The {@code String} type of the return value
     */
    public static String get(String url, Map<String, String> headers, Object requestParam) {
        return doRequest(new HttpGet(getUri(url, requestParam)), headers, requestParam);
    }

    /**
     * Apache HTTP request for {@code Post}.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map.
     * @param requestParam Request parameters.
     * @return The {@code String} type of the return value
     */
    public static String post(String url, Map<String, String> headers, Object requestParam) {
        return doRequest(new HttpPost(url), headers, requestParam);
    }

    /**
     * Apache HTTP request for {@code Put}.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map.
     * @param requestParam Request parameters.
     * @return The {@code String} type of the return value
     */
    public static String put(String url, Map<String, String> headers, Object requestParam) {
        return doRequest(new HttpPut(url), headers, requestParam);
    }

    /**
     * Apache HTTP request for {@code Delete}.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map.
     * @param requestParam Request parameters.
     * @return The {@code String} type of the return value
     */
    public static String delete(String url, Map<String, String> headers, Object requestParam) {
        return doRequest(new HttpDelete(url), headers, requestParam);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param requestBase  HTTP Public Request Class {@link HttpRequestBase}.
     * @param headers      Header information map.
     * @param requestParam Request parameters.
     * @return The {@code String} type of the return value
     */
    public static String doRequest(@NonNull HttpRequestBase requestBase,
                                   Map<String, String> headers,
                                   Object requestParam) {
        CloseableHttpClient client = HttpClients.custom().build();
        CloseableHttpResponse response = null;
        String result;
        try {
            addHeaders(headers, requestBase);
            setEntity(requestParam, requestBase, headers);
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
     * Set {@link HttpEntity} with a {@link StringEntity}.
     *
     * @param requestParam Request parameters.
     * @param requestBase  HTTP Public Request Class {@link HttpRequestBase}
     * @param headers      Header information map.
     */
    private static void setEntity(Object requestParam, HttpRequestBase requestBase, Map<String, String> headers) {
        if (requestParam == null || !(requestBase instanceof HttpEntityEnclosingRequestBase)) {
            return;
        }
        String paramOfString = requestParam.toString();
        StringEntity stringEntity;
        if (CollectionUtil.isNotEmpty(headers)) {
            String value = headers.get("Content-type");
            if (StrUtil.isNotBlank(value)) {
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
     * The get request for building HTTP contains the construction object of {@link URI}.
     *
     * @param url          The actual request address.
     * @param requestParam Request parameters.
     * @return Uri object,Please pay attention to the format issue of the URL.
     */
    private static URI getUri(String url, Object requestParam) {
        URI uri;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (requestParam != null && !(requestParam instanceof String)) {
                //If the type is a map concatenated after the address
                if (requestParam instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> params = (Map<String, Object>) requestParam;
                    for (String paramKey : params.keySet()) {
                        uriBuilder.addParameter(paramKey, String.valueOf(params.get(paramKey)));
                    }
                }
            }
            //If it is null or a string, it can be directly used as a paparazzi
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new UtilException(url + " not a valid url");
        }
        return uri;
    }
}
