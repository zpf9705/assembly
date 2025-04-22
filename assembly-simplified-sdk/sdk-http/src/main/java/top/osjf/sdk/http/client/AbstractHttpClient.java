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

package top.osjf.sdk.http.client;

import com.google.common.base.Stopwatch;
import top.osjf.sdk.core.DefaultErrorResponse;
import top.osjf.sdk.core.ErrorResponse;
import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.URL;
import top.osjf.sdk.core.client.AbstractClient;
import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.core.exception.SdkException;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.lang.Nullable;
import top.osjf.sdk.core.spi.SpiLoader;
import top.osjf.sdk.core.spi.SpiLoaderException;
import top.osjf.sdk.core.util.ArrayUtils;
import top.osjf.sdk.core.util.ExceptionUtils;
import top.osjf.sdk.core.util.ReflectUtil;
import top.osjf.sdk.http.AbstractHttpResponse;
import top.osjf.sdk.http.HttpRequest;
import top.osjf.sdk.http.HttpResponse;
import top.osjf.sdk.http.spi.DefaultHttpRequest;
import top.osjf.sdk.http.spi.HttpRequestExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * SDK's HTTP request mode client abstract class inherits
 * {@link AbstractClient} to implement the client's caching for
 * each request URL, defines the default conversion method for
 * the HTTP request process, as well as default log input, parameter
 * thread acquisition, and so on.
 *
 * <p>Provide two default method rewrites.<br>
 * {@link #preResponseStrHandler(Request, String)} The default response
 * is in JSON form, without conversion, and returns directly.<br>
 * {@link #convertToResponse(Request, String)} Directly converts the
 * JSON form to the desired response type.<br>
 *
 * <p>It is also a step-by-step implementation process that involves
 * verifying parameters, obtaining parameters, HTTP type method requests,
 * response preprocessing, and request transformation processing, including
 * handling {@link SdkException} exceptions and {@link Exception} unknown
 * exceptions.
 *
 * <p>The execution of HTTP requests is based on {@link HttpRequestExecutor}.
 * Before calling, please first call {@link #setRequestExecutor} to set a
 * {@code HttpRequestExecutor} instance that is reasonable for the request.
 * If it is not set, a corresponding {@code HttpRequestExecutor} instance will
 * be imported according to the SPI mechanism. The architecture details can
 * be seen in the introduction of property {@code #requestExecutor}.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractHttpClient<R extends HttpResponse> extends AbstractClient<R> implements HttpClient<R> {

    private static final long serialVersionUID = -7793213059840466979L;

    /**
     * Http request executor.
     * <p>
     * The method {@link #setRequestExecutor} can be used to set the HTTP
     * executor required by the current {@code HttpClient}.
     * <p>
     * If you haven't set {@code HttpRequestExecutor} through a method, you can
     * use a specific loading mechanism to automatically load it. You can customize
     * the {@code HttpRequestExecutor} implementation class and mark the
     * {@link top.osjf.sdk.core.spi.Spi} annotation to specify the higher
     * priority of the custom executor.
     * <p>
     * The extension already provides two implementations of {@code HttpRequestExecutor},
     * {@code Apache hc} and {@code Ok hc}, with the former taking precedence over the
     * latter (when all are used, you can observe the order of {@link top.osjf.sdk.core.spi.Spi}
     * annotations). To customize the extension implementation class, you can follow the example below:
     * <pre>
     *      {@code
     *      top.osjf.sdk.core.support.LoadOrder(Integer.MIN_VALUE) The highest loading level.
     *      public class DefaultHttpRequestExecutor implements HttpRequestExecutor{
     *
     *         Override
     *         public Response execute(Request request, Request.Options options) throws Exception {
     *              // You can define your own processing logic based on the existing open feign request
     *              // parameters.
     *         }
     *         //The remaining old methods will be removed in future versions and do not require compatibility.
     *         //If there are their own tools to handle classes, they can be used as markers.
     *
     *         //These are implementation methods for source request processes.
     *         Override
     *         public String post(String url, Map<String, String> headers, Object param, boolean montage)
     *         throws Exception {
     *             return null;
     *         }
     *         ... omit
     *     }
     *    }
     *  </pre>
     * <p>
     * The default provided {@code HttpRequestExecutor} implementation can be viewed
     * in the following packages:
     * <ul>
     *     <li><h3>customize:</h3></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-apache">sdk-http-apache</a></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-ok">sdk-http-ok</a></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-google">sdk-http-google</a></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-hc5">sdk-http-hc5</a></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-jaxrs2">sdk-http-jaxrs2</a></li>
     *     <li><h3>Integrate OpenFeign:</h3></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-feign-apache">
     *         sdk-http-feign-apache</a></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-feign-ok">
     *         sdk-http-feign-ok</a></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-feign-google">
     *         sdk-http-feign-google</a></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-feign-hc5">
     *         sdk-http-feign-hc5</a></li>
     *     <li><a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http-feign-jaxrs2">
     *         sdk-http-feign-jaxrs2</a></li>
     * </ul>
     */
    private HttpRequestExecutor requestExecutor;

    /**
     * The persistent URL of the current HTTP client.
     *
     * <p>Suitable for scenarios where the URL remains unchanged and
     * there is no need to perform parameter concatenation or other
     * related changes every time.
     */
    @Nullable
    private String persistentUrl;

    /**
     * Constructing for {@code AbstractHttpClient} objects using access URLs.
     *
     * @param url {@code URL} Object of packaging tags and URL addresses
     *            and updated on version 1.0.2.
     * @throws NullPointerException If the input url is {@literal null}.
     */
    public AbstractHttpClient(@NotNull URL url) {
        super(url);
        if (url.isSame()) {
            this.persistentUrl = url.getUrl();
        }
    }

    /**
     * Set a {@code HttpRequestExecutor}.
     *
     * @param requestExecutor a {@code HttpRequestExecutor}.
     * @throws NullPointerException if input requestExecutor is
     *                              {@literal null}.
     */
    public void setRequestExecutor(@NotNull HttpRequestExecutor requestExecutor) {
        Objects.requireNonNull(requestExecutor, "requestExecutor == null");
        this.requestExecutor = requestExecutor;
    }

    /**
     * Return an available {@code HttpRequestExecutor}.
     *
     * @return an available {@code HttpRequestExecutor}.
     * @throws IllegalStateException if no available {@code HttpRequestExecutor}.
     */
    @NotNull
    public HttpRequestExecutor getRequestExecutor() throws IllegalStateException {
        if (requestExecutor == null) {
            //When the HttpRequestExecutor is not directly set,
            // it is obtained through the loading mechanism.
            requestExecutor = SpiLoader.of(HttpRequestExecutor.class).loadHighestPriorityInstance();
            if (requestExecutor == null) {
                throw new SpiLoaderException(HttpRequestExecutor.class.getName() +
                        " Provider class not found, please check if it is in the SPI configuration file?");
            } else {
                getLogger().info("Http Client {} using HttpRequestExecutor {} by spi.",
                        getClass().getName(), requestExecutor.getClass().getName());
            }
        }
        return requestExecutor;
    }

    /**
     * Return the request address of the HTTP client.
     * If the current address {@link #persistentUrl} is empty,
     * obtain the bound URL address.
     *
     * @return The request address of the HTTP client.
     * @throws IllegalStateException If the bound {@code Url} is empty,
     *                               throw the current binding status
     *                               exception.
     */
    @NotNull
    public String getUrl() throws IllegalStateException {
        return persistentUrl == null ? getBindUrl() : persistentUrl;
    }

    /**
     * {@inheritDoc}
     * <p>
     * When the persistent URL of the current HTTP client exists,
     * there is no need to bind the URL because it is fixed and
     * unchanging. If the URL needs to be formatted, it needs
     * to be bound each time.
     *
     * @param url {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public Client<R> bindUrl(@NotNull String url) throws IllegalStateException {
        if (persistentUrl == null) {
            return super.bindUrl(url);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Execute the HTTP request process.
     * <p>
     * All request related exception issues will be parsed class by class
     * and converted to the current response type, requiring the two
     * methods {@link ErrorResponse#setErrorCode}
     * and {@link ErrorResponse#setErrorMessage}
     * of {@link ErrorResponse} to obtain the relevant
     * content defined in this framework.
     *
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public R request() {

        //Get the binding parameters for HTTP requests.
        HttpRequest<R> request = getBindRequest().unwrap(HttpRequest.class);

        R response;
        String responseStr = null;
        Throwable throwable = null;

        //Create a request timer.
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            //Validation of custom parameters.
            request.validate();

            //Execute HTTP components based on encapsulation parameters.
            top.osjf.sdk.http.spi.HttpResponse spiResponse =
                    getRequestExecutor().execute(new DefaultHttpRequest(request, getUrl(), getOptions()));

            //Get request body string parameters
            responseStr = spiResponse.getBody();

            //Preprocessing operation for request results.
            responseStr = preResponseStrHandler(request, responseStr);

            //The result conversion operation of the request result.
            response = convertToResponse(request, responseStr);

            //Set a spi response to sdk response.
            setSpiResponse(response, spiResponse);

        } catch (SdkException e) {

            //Capture anomalies in SDK and provide a conversion reminder.
            handlerSdkError(request, e);
            throwable = e;
            response = DefaultErrorResponse.parseErrorResponse(throwable, DefaultErrorResponse.ErrorType.SDK, request);

        } catch (Throwable e) {

            //Capture unknown exceptions and provide a transition reminder.
            handlerUnKnowError(request, e);
            throwable = e;
            response = DefaultErrorResponse.parseErrorResponse(throwable, DefaultErrorResponse.ErrorType.UN_KNOWN,
                    request);

        } finally {

            //Stop timing.
            stopwatch.stop();

            //Hand over the call information to the final processing project.
            finallyHandler(HttpResultSolver.ExecuteInfoBuild.builder().requestAccess(request)
                    .spend(stopwatch.elapsed(TimeUnit.MILLISECONDS))
                    .maybeError(throwable)
                    .response(responseStr)
                    .build());
        }

        return response;
    }

    /**
     * Set a {@link top.osjf.sdk.http.spi.HttpResponse spiResponse} for sdk response
     * {@link HttpResponse} to support queries important information returned by the
     * requesting server.
     *
     * @param response    the input sdk response.
     * @param spiResponse the input spi response.
     * @since 1.0.2
     */
    protected void setSpiResponse(R response, top.osjf.sdk.http.spi.HttpResponse spiResponse) {
        if (response instanceof AbstractHttpResponse) {
            ((AbstractHttpResponse) response).setHttpResponse(spiResponse);
        } else {
            for (Method method : ReflectUtil.getAllDeclaredMethods(response.getClass())) {
                Parameter[] parameters = method.getParameters();
                if (parameters.length == 1) {
                    Parameter parameter = parameters[0];
                    if (top.osjf.sdk.http.spi.HttpResponse.class.isAssignableFrom(parameter.getType())) {
                        ReflectUtil.invokeMethod(response, method, spiResponse);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void handlerSdkError(HttpRequest<?> request, SdkException e) {

        // -- only info
        getLogger().info("Client request fail case by {} , apiName={}, error=[{}]",
                e.getClass().getName(), request.matchSdkEnum().name(), e.getMessage());
    }

    @Override
    public void handlerUnKnowError(HttpRequest<?> request, Throwable e) {

        //-- info
        getLogger().info("Client request fail case by {}, apiName={}, error=[{}]",
                e.getClass().getName(), request.matchSdkEnum().name(), ExceptionUtils.getMessage(e));

        //-- error
        getLogger().error(e.getMessage(), e);
    }

    @Override
    public void finallyHandler(HttpResultSolver.ExecuteInfo info) {
        HttpRequest<?> httpRequest = info.getHttpRequest();
        String name = httpRequest.matchSdkEnum().name();
        Object requestParam = httpRequest.getRequestParam();
        String body = requestParam != null ? requestParam.toString() : "";
        String response = info.getResponse();
        long spendTotalTimeMillis = info.getSpendTotalTimeMillis();
        if (info.noHappenError().get()) {
            String msgFormat = "Request end, name={}, request={}, response={}, time={}ms";
            getLogger().info(msgFormat,
                    ArrayUtils.toArray(name, body, response, spendTotalTimeMillis));
        } else {
            String msgFormat = "Request fail, name={}, request={}, response={}, error={}, time={}ms";
            getLogger().info(msgFormat,
                    ArrayUtils.toArray(name, body, response, info.getErrorMessage(), spendTotalTimeMillis));
        }
    }
}
