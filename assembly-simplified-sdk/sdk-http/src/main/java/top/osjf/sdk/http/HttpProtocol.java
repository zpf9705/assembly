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

import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.process.HttpSdkEnum;
import top.osjf.sdk.http.util.UrlUtils;

import java.net.URL;
import java.util.Objects;

/**
 * An enumeration type that defines the HTTP and HTTPS network protocols.
 * <p>
 * Each enumeration instance contains the protocol identity (e.g., "http", "https"),
 * the protocol prefix (e.g., "http:", "https:"), and a complete URL prefix
 * (e.g., "http://", "https://").
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public enum HttpProtocol {

    /**
     * The enumeration instance representing the HTTPS protocol.
     */
    HTTPS("https", "https:", "https://"),

    /**
     * The enumeration instance representing the HTTP protocol.
     */
    HTTP("http", "http:", "http://"),

    /**
     * NULL means not provided.
     * <p>If you provide {@code HttpProtocol} in {@link HttpSdkEnum#getProtocol()},
     * please do not select this option.
     *
     * @since 1.0.2
     */
    NULLS(null, null, null) {
        final UnsupportedOperationException ex =  new UnsupportedOperationException();
        @Override
        public String getIdentity() {
            throw ex;
        }

        @Override
        public String getProtocolPrefix() {
            throw ex;
        }

        @Override
        public String getUrlPrefix() {
            throw ex;
        }
    };

    /**
     * @since 1.0.2
     * The identity of the protocol, such as "http" or "https".
     */
    private final String identity;

    /**
     * @since 1.0.2
     * The prefix of the protocol, such as "http:" or "https:".
     * Derived from version 1.0.0 of {@code path}.
     */
    private final String protocolPrefix;

    /**
     * @since 1.0.2
     * The complete URL prefix, such as "http://" or "https://".
     */
    private final String urlPrefix;

    HttpProtocol(String identity, String protocolPrefix, String urlPrefix) {
        this.identity = identity;
        this.protocolPrefix = protocolPrefix;
        this.urlPrefix = urlPrefix;
    }

    /**
     * Gets the identity of the protocol.
     *
     * @return The identity of the protocol
     * @since 1.0.2
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Gets the prefix of the protocol.
     *
     * @return The prefix of the protocol
     * @since 1.0.2
     */
    public String getProtocolPrefix() {
        return protocolPrefix;
    }

    /**
     * Gets the complete URL prefix.
     *
     * @return The complete URL prefix
     * @since 1.0.2
     */
    public String getUrlPrefix() {
        return urlPrefix;
    }

    /**
     * Formats a URL string.
     * <p>
     * This method is primarily used to format the passed-in URL string.
     * <p>
     * If the current three indicators are not included at the beginning of the URL,
     * consider whether the URL starts with 'http' and the following situations occur:
     * <ul>
     *     <li>Current model: {@code HTTP} | input : `https:xxx`</li>
     *     <li>Current model: {@code HTTPS} | input : `http:xxx`</li>
     * </ul>
     * Type error belonging to parsing disorder.
     * <p>
     * If the protocol type of the input URL {@link URL#getProtocol()} matches
     * the current enumeration protocol type {@link #getIdentity()}, the URL will
     * be returned directly.
     * <p>Else sulations:
     * If the URL starts with a colon {@code :}, it will prepend the current identity.
     * <p>
     * If the URL starts with double slashes {@code //}, it will prepend the protocol prefix.
     * Otherwise, it will prepend the full URL prefix.
     * <p>
     * Can handle the following normal situations:
     * <ul>
     *     <li>https://org.example.com/api</li>
     *     <li>://org.example.com/api</li>
     *     <li>//org.example.com/api</li>
     *     <li>org.example.com/api</li>
     * </ul>
     *
     * @param url The URL string to be formatted.
     * @return The formatted URL string.
     * @since 1.0.2
     */
    public String formatUrl(String url) {
        // If url is blank, directly return it.
        if (StringUtils.isBlank(url)) return url;
        //Handle the following situations:
        //Current model: {@code HTTP} | input : `https:xxx`
        //Current model: {@code HTTPS} | input : `http:xxx`
        if (url.startsWith(HTTP.identity)) {
            String urlProtocol = UrlUtils.getJdkUrlProtocol(url);
            if (urlProtocol != null) {
                if (!Objects.equals(urlProtocol, getIdentity())) {
                    throw new IllegalArgumentException
                            ("The protocol identity of the input URL is [" + urlProtocol + "], which is inconsistent" +
                                    " with the current resolved identity [" + getIdentity() + "]. Format failed." +
                                    " Please check the protocol parameters.");
                } else return url;   // already starts with the current identity, directly return it.
            }
        }
        if (url.startsWith(":")) url = getIdentity() + url;
            // If the URL starts with double slashes "//", prepend the protocol prefix.
        else if (url.startsWith("//")) url = getProtocolPrefix() + url;
            // Otherwise, prepend the full URL prefix.
        else url = getUrlPrefix() + url;
        // Return the formatted URL.
        return url;
    }
    }
