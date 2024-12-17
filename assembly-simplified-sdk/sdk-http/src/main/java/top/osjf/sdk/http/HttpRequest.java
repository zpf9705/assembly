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

import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.URL;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;

import java.util.Map;

/**
 * The {@code HttpRequest} interface extends from the {@code Request} interface and
 * is specifically designed to define the methods and behaviors related to HTTP requests.
 *
 * <p>This interface restricts the response type to instances of {@code HttpResponse} or
 * its subclasses through the generic parameter {@code R}.
 *
 * <p>In addition, the interface also provides detailed guidance on how to rewrite the
 * {@link #matchSdkEnum()} method, including using the {@link HttpSdkEnumCultivate}
 * annotation to simplify the implementation process.
 *
 * <p>Extension method {@link #urlJoin()} allows users to manually concatenate URL
 * parameters. Parameters can be query parameters for URLs or supplementary links to URLs,
 * depending on the specific scenario.
 *
 * <p>This interface is designed to be implemented by specific HTTP request classes and
 * provides specific implementation logic.
 *
 * @param <R> Subclass generic type of {@code HttpResponse}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public interface HttpRequest<R extends HttpResponse> extends Request<R> {

    /**
     * {@inheritDoc}
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    URL getUrl(@Nullable String host);

    /**
     * The manual URL concatenation method allows users to concatenate
     * parameters on URLs other than the {@link HttpSdkEnum#getRequestMethod()}
     * request mode, which needs to be rewritten directly.
     * <p>For example, there is now a URL that is <strong>https://example.com</strong>,
     * <p>This method is provided:
     * <pre>
     *     {@code
     *      String urlJoin(){
     *          return "?accessToken='123456'"
     *      }
     *     }
     * </pre>
     * So the final result is: <strong>https://example.com?accessToken=123456</strong>
     *
     * @return post concatenation of URL in string format.
     */
    @Nullable
    String urlJoin();

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    Map<String, Object> getHeadMap();

    /**
     * {@inheritDoc}
     * <p>
     * Overrides the {@code matchSdkEnum} method from the parent class or interface to match
     * and return an {@code matchSdkEnum} instance that fits the current context.
     *
     * <p>This method is specific to the {@code matchSdkEnum} type, meaning that the returned
     * enumeration instance is dedicated to SDK enumerations for HTTP requests.
     *
     * <p>Compared to the {@code matchSdkEnum} method in the parent class or interface, this
     * method provides a more specific return type, {@code matchSdkEnum}, allowing the caller
     * to directly obtain HTTP-related SDK configuration information.
     *
     * <p>In version 1.0.2, an easier rewrite solution was provided for the implementation
     * of this method:
     * <ul>
     *     <li>Implement class label annotation {@link HttpSdkEnumCultivate} in {@code HttpRequest}
     *     to inform the framework of relevant information about interface {@code HttpSdkEnum}. The
     *     framework will dynamically obtain annotation {@code HttpSdkEnumCultivate} at runtime to
     *     obtain the relevant metadata of this SDK, and make HTTP related requests accordingly
     *     and just inherit {@link CultivateSupportHttpRequest} and it will automatically
     *     help you manage {@code HttpSdkEnum}.</li>
     *     <li>Annotate {@code top.osjf.sdk.http.annotation.HttpSdkEnumCultivate} onto the implementation
     *     class of {@code HttpRequest}, and then override this method to return {@literal null}. This
     *     annotation will be added during compilation to the default implementation of {@code HttpSdkEnum}
     *     on this method. Please refer to the package
     *      <hr><blockquote><pre>
     *      {@code
     *        <dependency>
     *           <groupId>top.osjf.sdk</groupId>
     *          <artifactId>sdk-http-annotation-processor</artifactId>
     *           <version>Latest version</version>
     *        </dependency>
     *      }
     *      </pre></blockquote><hr>
     *     <p>for details and view the annotation {@code top.osjf.sdk.http.annotation.HttpSdkEnumCultivate}.</li>
     * </ul>
     * <p>Since the ultimate agreement of this method is that it must not be null {@code HttpSdkEnum}, a
     * {@link NotNull} tag is given, and when using a solution other than directly rewriting to obtain the
     * custom {@code HttpSdkEnum} to inform this value, the method can return null.
     *
     * @return An {@code HttpSdkEnum} instance that fits the current context.
     */
    @Override
    @NotNull
    HttpSdkEnum matchSdkEnum();

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @NotNull
    @Override
    Class<? extends Client> getClientCls();

    /**
     * {@inheritDoc}
     *
     * @param clazz {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    boolean isAssignableRequest(Class<?> clazz);
}
