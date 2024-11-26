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

import com.palominolabs.http.url.UrlBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

/**
 * URL utility class, providing help support for related URLs
 * and auxiliary framework support.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class UrlUtils {

    /**
     * Converts the given URL string and charset into a {@code Palominolabs UrlBuilder} object.
     * <p>
     * This method first attempts to parse the input string into a URL object,
     * then creates a new character decoder using the provided charset,
     * and finally uses this decoder and the URL object to create a UrlBuilder instance.
     * <p>
     * If the input URL string is malformed or the charset cannot be used to decode the URL,
     * this method will throw the corresponding exceptions.
     *
     * @param url     url to initialize builder with
     * @param charset encoding character set.
     * @return the palominolabs {@code UrlBuilder}.
     * @throws CharacterCodingException if decoding percent-encoded bytes fails and charsetDecoder is configured to
     *                                  report errors.
     * @throws MalformedURLException    if no protocol is specified, or an
     *                                  unknown protocol is found, or {@code spec} is {@code null}.
     */
    public static UrlBuilder toPalominolabsBuilder(String url, Charset charset)
            throws CharacterCodingException, MalformedURLException {
        return UrlBuilder.fromUrl(new URL(url), charset.newDecoder());
    }

    /**
     * Retrieves the protocol part of a given URL.
     * <p>
     * This method takes a string parameter {@code url}, which should represent a valid URL.
     * It first attempts to parse this string using the constructor of the {@code URL} class.
     * If parsing succeeds, it retrieves the protocol part of the URL by calling the
     * {@code getProtocol()} method and returns this value.
     * <p>
     * If a {@code MalformedURLException} is encountered during URL parsing (indicating that
     * the URL is not well-formed),the exception is caught and null is returned.
     *
     * @param url The URL string to be parsed.
     * @return The protocol part of the URL (e.g., "http", "https", "ftp") if the URL is well-formed;
     * otherwise, returns null if the URL is malformed.
     */
    public static String getJdkUrlProtocol(String url) {
        try {
            return new URL(url).getProtocol();
        } catch (MalformedURLException e) {
            // If the URL is malformed, catch the exception and return null
            return null;
        }
    }
}
