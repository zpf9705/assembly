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

/**
 * Defines an enumeration type representing HTTP protocols.
 * <p>
 * This enumeration contains two instances: HTTPS and HTTP, representing
 * the secure Hypertext Transfer Protocol (HTTPS) and the plain Hypertext
 * Transfer Protocol (HTTP) respectively.
 * <p>
 * Each enumeration instance has an associated string that is the prefix
 * of the protocol (e.g., "https:" and "http:").
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public enum HttpProtocol {

    /**
     * The enumeration instance representing the HTTPS protocol.
     * <p>
     * The value of its path field is "https:", representing the prefix of the HTTPS protocol.
     */
    HTTPS("https:"),

    /**
     * The enumeration instance representing the HTTP protocol.
     * <p>
     * The value of its path field is "http:", representing the prefix of the HTTP protocol.
     */
    HTTP("http:");

    /**
     * A string field storing the protocol prefix.
     * <p>
     * This field is private for each enumeration instance and can only be
     * accessed through the {@link #getPath()} method.
     */
    private final String path;

    /**
     * The constructor of the enumeration.
     * <p>
     * This constructor is called when each enumeration instance is created,
     * and the protocol prefix is passed as a parameter to the path field.
     *
     * @param path The prefix string of the protocol.
     */
    HttpProtocol(String path) {
        this.path = path;
    }

    /**
     * A method to get the protocol prefix string.
     * <p>
     * Returns the protocol prefix string associated with this enumeration
     * instance.
     *
     * @return The protocol prefix string.
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the dependent path by appending double slashes {@code //} to
     * the current path.
     *
     * <p>This method retrieves the current path by calling the internal
     * {@link #getPath()} method,then appends double slashes {@code //}
     * to the end of that path, and returns the resulting string.
     *
     * @return A string with double slashes {@code //} appended to the
     * current path.
     */
    public String getDependentPath() {
        return getPath() + "//";
    }

    /**
     * Returns a processed dependent path based on the provided URL string.
     *
     * <p>This method first checks if the provided URL string starts with the {@code HTTP}
     * or {@code HTTPS} protocol.
     * If it does, the URL string is returned directly without any modifications.
     *
     * <p>If the provided URL string does not start with the HTTP or HTTPS protocol,
     * it calls the internal {@link #getDependentPath()} method to obtain a dependent
     * path (i.e., a path with double slashes "//" appended to the current path),
     * then concatenates this dependent path with the provided URL string, and returns
     * the resulting string.
     *
     * @param url The provided URL string, which may be a full URL or a relative path.
     * @return The processed dependent path string.
     */
    public String getDependentPathWithUrl(String url) {
        if (url.startsWith(HTTP.path) || url.startsWith(HTTPS.path)) {
            return url;
        }
        return getDependentPath() + url;
    }
}
