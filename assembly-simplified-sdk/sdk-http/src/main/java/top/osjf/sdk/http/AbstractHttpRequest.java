/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.osjf.sdk.http;

import top.osjf.sdk.core.AbstractRequest;
import top.osjf.sdk.core.URL;
import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.client.DefaultHttpClient;
import top.osjf.sdk.http.support.HttpSdkSupport;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@code AbstractHttpRequest} class is an abstract class that extends
 * the {@code AbstractRequest} class and implements the {@code HttpRequest}
 * interface.
 *
 * <p>This class provides a general abstract implementation for HTTP request parameters
 * , allowing subclasses to customize specific HTTP request behavior through inheritance
 * and rewriting methods.
 * <p>
 * This abstract class mainly provides the following default feature rewriting:
 * <ul>
 *     <li>Rewrite method {@link #getUrl}, after obtaining {@link HttpSdkEnum#getUrl},
 *     use method {@link #formatUrl} to format the two key points of
 *     {@link HttpSdkEnum#getRequestMethod()} and {@link #urlJoin()}.</li>
 *     <li>Request header management: By rewriting the {@link #getHeadMap()} method, the
 *     'Content-Type' header is automatically set based on the type of request parameter,
 *     And allow adding additional custom request headers by rewriting the {@link #additionalHeaders()}
 *     method.</li>
 *     <li>Provide the default use of {@link DefaultHttpClient} as the HTTP client.</li>
 *     <li>By rewriting the {@link #isAssignableRequest} method, check if the given
 *     class implements the {@code HttpRequest} interface.</li>
 * </ul>
 *
 * @param <R> Subclass generic type of {@code AbstractHttpResponse}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractHttpRequest<R extends AbstractHttpResponse> extends AbstractRequest<R>
        implements HttpRequest<R> {

    private static final long serialVersionUID = 7487068349280012103L;

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";

    /**
     * {@inheritDoc}
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc}
     * @since 1.0.2
     */
    @Override
    @NotNull
    public URL getUrl(@Nullable String host) {
        return URL.same(formatUrl(matchSdkEnum().getUrl(host)));
    }

    /**
     * {@inheritDoc}
     *
     * @return nothing.
     * @since 1.0.2
     */
    @Override
    @Nullable
    public String urlJoin() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This method can be rewritten to automatically recognize and add the request
     * header attribute using a non-null {@link #getRequestParam()} parameter when the
     * {@link #CONTENT_TYPE_HEADER_NAME} request header is not set. It supports the
     * following types of recognition:
     * <ul>
     * <li>{@code application/json}</li>
     * <li>{@code application/xml}</li>
     * <li>{@code application/x-www-form-urlencoded}</li>
     * <li>The recognition logic of the above types can be viewed as follows
     * {@link HttpSdkSupport#getContentTypeWithBody}</li>
     * </ul>
     *
     * <p>If you do need automatic validation for this method, the provision of other
     * request headers can be found in {@link #additionalHeaders()}, and this method
     * will support merge processing.
     *
     * @return {@inheritDoc}
     */
    @Override
    public Map<String, Object> getHeadMap() {
        Object requestParam = getRequestParam();
        Map<String, Object> headers = null;
        if (requestParam != null) {
            String contentType = HttpSdkSupport.getContentTypeWithBody(requestParam, getCharset());
            if (StringUtils.isNotBlank(contentType)) {
                headers = new LinkedHashMap<>();
                headers.put(CONTENT_TYPE_HEADER_NAME, appendCharsetToContentType(contentType));
            }
        }
        headers = resolveAdditionalHeaders(headers);
        if (MapUtils.isNotEmpty(headers)) return headers;
        return super.getHeadMap();
    }

    @Nullable
    @Override
    protected Type defResponseType() {
        return HttpResultResponse.class;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default to use {@code DefaultHttpClient}.
     *
     * @return {@inheritDoc}.
     */
    @Override
    @NotNull
    public Class<? extends Client> getClientType() {
        return DefaultHttpClient.class;
    }

    /**
     * {@inheritDoc}
     *
     * @param clazz {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isAssignableRequest(Class<?> clazz) {
        return HttpRequest.class.isAssignableFrom(clazz);
    }

    /**
     * Appends the character set to the given content type if a character set is available.
     *
     * @param contentType the original content type header value
     * @return the content type header value with the character set appended, or the original
     * value if no character set is available
     * @since 1.0.3
     */
    protected String appendCharsetToContentType(String contentType) {
        Charset charset = getCharset();
        if (charset != null) {
            return contentType + ";charset=" + charset;
        }
        return contentType;
    }

    /**
     * Format the actual request address of the SDK and concatenate subsequent URLs.
     *
     * <p>Here, the splicing parameters of the {@link #urlJoin()} method will be
     * automatically added for you. If you don't need to rewrite {@link #urlJoin()},
     * you can do so.
     *
     * <p>Since version 1.0.2, if you provide {@link HttpProtocol}, you will no longer
     * need to pay attention to the HTTP protocol type in URL formatting. The following
     * method will be used to properly concatenate when {@code HttpProtocol} is not empty.
     *
     * @param url The URL address obtained after matching {@code HttpSdkEnum}.
     * @return The request address for the SDK.
     */
    protected final String formatUrl(String url) {
        HttpProtocol protocol = matchSdkEnum().getProtocol();
        if (protocol != null) url = protocol.formatUrl(url);
        String uj = urlJoin();
        return url + (StringUtils.isNotBlank(uj) ? uj : "");
    }

    /**
     * Given a request header map, process adding additional user-defined
     * request headers. When the provided request header is empty, provide
     * a new {@code LinkedHashMap} and add the custom request header.
     *
     * @param headers given header map.
     * @return resolve result header map.
     * @since 1.0.2
     */
    protected final Map<String, Object> resolveAdditionalHeaders(Map<String, Object> headers) {
        Map<String, Object> additionalHeads = additionalHeaders();
        if (MapUtils.isNotEmpty(additionalHeads)) {
            if (MapUtils.isEmpty(headers)) headers = new LinkedHashMap<>();
            headers.putAll(additionalHeads);
        }
        return headers;
    }

    /**
     * Return an additional header information in map format.
     *
     * <p>Combine with the default {@link #getHeadMap()} rewrite
     * logic in this class and add relevant user-defined request
     * headers other than {@code Content-Type}.
     *
     * @return additional headers.
     * @since 1.0.2
     */
    protected Map<String, Object> additionalHeaders() {
        return Collections.emptyMap();
    }
}
