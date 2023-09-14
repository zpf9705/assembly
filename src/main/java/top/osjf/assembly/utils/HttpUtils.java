package top.osjf.assembly.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
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
     * @param montage      Whether to concatenate urls as maps.
     * @return The {@code String} type of the return value
     */
    public static String get(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(new HttpGet(getUri(url, requestParam, montage)), headers, requestParam);
    }

    /**
     * Apache HTTP request for {@code Post}.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map.
     * @param requestParam Request parameters.
     * @param montage      Whether to concatenate urls as maps.
     * @return The {@code String} type of the return value
     */
    public static String post(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(new HttpPost(getUri(url, requestParam, montage)), headers, requestParam);
    }

    /**
     * Apache HTTP request for {@code Put}.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map.
     * @param requestParam Request parameters.
     * @param montage      Whether to concatenate urls as maps.
     * @return The {@code String} type of the return value
     */
    public static String put(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(new HttpPut(getUri(url, requestParam, montage)), headers, requestParam);
    }

    /**
     * Apache HTTP request for {@code Delete}.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map.
     * @param requestParam Request parameters.
     * @param montage      Whether to concatenate urls as maps.
     * @return The {@code String} type of the return value
     */
    public static String delete(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(new HttpDelete(getUri(url, requestParam, montage)), headers, requestParam);
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
     * @param montage      Whether to concatenate urls as maps.
     * @return Uri object,Please pay attention to the format issue of the URL.
     */
    @SuppressWarnings("unchecked")
    private static URI getUri(String url, Object requestParam, boolean montage) {
        URI uri;
        try {
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
                    if (JSONValidator.from(requestParam.toString()).validate()) {
                        JSONObject jsonObject = JSON.parseObject(requestParam.toString());
                        params = jsonObject.getInnerMap();
                    }
                }
                if (CollectionUtil.isNotEmpty(params)) {
                    for (String paramKey : params.keySet()) {
                        uriBuilder.addParameter(paramKey, String.valueOf(params.get(paramKey)));
                    }
                }
            }
            //If it is null or a string, it can be directly used as a paparazzi
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new UtilException(url + " Invalid URL.");
        }
        return uri;
    }
}
