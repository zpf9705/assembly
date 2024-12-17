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

package top.osjf.sdk.core;

import top.osjf.sdk.core.support.SdkArgs;

import java.util.Objects;

/**
 * Defines an interface representing a URL, including methods to get a unique
 * identifier and the URL address.
 * <p>
 * It also provides static factory methods to create URL instances.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface URL {

    /**
     * <p>
     * Gets the unique identifier for this URL.
     * </p>
     *
     * @return The unique identifier for the URL.
     */
    String getUnique();

    /**
     * <p>
     * Gets the address of this URL.
     * </p>
     *
     * @return The address of the URL.
     */
    String getUrl();

    /**
     * <p>
     * Return a Boolean tag indicating whether the
     * {@code unique identifier} is the same as the real URL.
     * </p>
     *
     * @return if {@code true} two are equals,otherwise {@code false}.
     */
    boolean isSame();

    /**
     * <p>
     * A static factory method to create a URL instance where the unique
     * identifier and URL address are the same.
     * </p>
     *
     * @param url The address of the URL.
     * @return URL instance.
     */
    static URL same(String url) {
        return new URLImpl(url, url);
    }

    /**
     * <p>
     * A static factory method to create a URL instance with a specified
     * unique identifier and URL address.
     * </p>
     *
     * @param unique The unique identifier for the URL.
     * @param url    The address of the URL.
     * @return URL instance.
     */
    static URL of(String unique, String url) {
        return new URLImpl(unique, url);
    }

    /**
     * The implementation class of the URL interface, containing properties
     * for the unique identifier and URL address,and providing corresponding
     * constructor methods and interface method implementations.
     */
    class URLImpl implements URL {

        /**
         * <p>
         * The unique identifier for the URL.
         * </p>
         */
        String unique;
        /**
         * <p>
         * The address of the URL.
         * </p>
         */
        String url;

        URLImpl(String unique, String url) {
            SdkArgs.hasText(unique, "unique");
            SdkArgs.hasText(url, "url");
            this.unique = unique;
            this.url = url;
        }

        @Override
        public String getUnique() {
            return unique;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public boolean isSame() {
            return Objects.equals(unique, url);
        }
    }
}
