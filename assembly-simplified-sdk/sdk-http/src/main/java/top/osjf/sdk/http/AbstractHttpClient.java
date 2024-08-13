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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.sdk.core.client.AbstractClient;
import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.core.exception.SdkException;
import top.osjf.sdk.core.process.DefaultErrorResponse;
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.util.CollectionUtils;
import top.osjf.sdk.core.util.JSONUtil;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
 * <p>Of course, there are also handling of the final results, and common
 * methods are placed.
 *
 * <p>If this type is inherited, it can be rewritten.
 *
 * <p>Of course, you can define your request based on the {@link #doHttpRequest} method.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public abstract class AbstractHttpClient<R extends HttpResponse> extends AbstractClient<R> implements HttpClient<R> {

    /*** default slf4j logger with {@link Client} */
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    /*** HTTP requests the real access address.*/
    private final String url;

    /*** Constructing for {@link HttpClient} objects using access URLs.
     * @param url The real URL address of the SDK request.
     * */
    public AbstractHttpClient(String url) {
        super(url);
        this.url = url;
    }

    /**
     * Return the actual URL path at the time of the request.
     *
     * @return the actual URL path at the time of the request
     */
    public String getUrl() {
        return url;
    }

    /**
     * Return the request that was temporarily placed in the
     * thread copy as {@link Request}.
     *
     * @return the request that was temporarily placed in the
     * thread copy as {@link Request}.
     */
    public HttpRequest<R> getCurrentHttpRequest() {
        return (HttpRequest<R>) getCurrentRequest();
    }

    @Override
    public R request() {

        //Get the request parameters for the current thread.
        HttpRequest<R> request = getCurrentHttpRequest();

        //Define the required parameters for this request.
        R response;
        String responseStr = null;
        Throwable throwable = null;

        //Record the initial time.
        long startTimeMillis = System.currentTimeMillis();

        //Key operation steps: try the package.
        try {

            //Custom validation of request parameters.
            request.validate();

            //Obtain the real request parameters.
            Object requestParam = request.getRequestParam();

            //Get the request header parameters.
            Map<String, String> headers = request.getHeadMap();

            //Execute this request, route according to the request type, and handle the parameters.
            responseStr = doHttpRequest(request.matchSdkEnum().getRequestMethod(),
                    headers,
                    requestParam,
                    request.montage());

            //Preprocessing operation for request results.
            responseStr = preResponseStrHandler(request, responseStr);

            //The result conversion operation of the request result.
            response = convertToResponse(request, responseStr);

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

            //Hand over the call information to the final processing project.
            finallyHandler(HttpResultSolver.ExecuteInfoBuild.builder().requestAccess(request)
                    .spend(System.currentTimeMillis() - startTimeMillis)
                    .maybeError(throwable)
                    .response(responseStr)
                    .build());
        }

        return response;
    }

    @Override
    public String doHttpRequest(HttpRequestMethod method,
                               Map<String, String> headers,
                               Object requestParam,
                               Boolean montage) throws Exception {
        HttpSdkSupport.checkContentType(headers);
        return null;
    }

    @Override
    public String preResponseStrHandler(Request<R> request, String responseStr) {
        return responseStr;
    }

    @Override
    public R convertToResponse(Request<R> request, String responseStr) {
        R response;
        Object type = request.getResponseRequiredType();
        if (JSONUtil.isValidObject(responseStr)) {
            response = JSONUtil.parseObject(responseStr, type);
        } else if (JSONUtil.isValidArray(responseStr)) {
            List<R> responses = JSONUtil.parseArray(responseStr, type);
            if (CollectionUtils.isNotEmpty(responses)) {
                response = responses.get(0);
            } else {
                response = JSONUtil.toEmptyObj(type);
            }
        } else {
            response = DefaultErrorResponse
                    .parseErrorResponse(responseStr, DefaultErrorResponse.ErrorType.DATA, request);
        }
        return response;
    }

    @Override
    public BiConsumer<String, Object[]> normal() {
        return log::info;
    }

    @Override
    public BiConsumer<String, Object[]> sdkError() {
        return log::error;
    }

    @Override
    public BiConsumer<String, Object[]> unKnowError() {
        return log::error;
    }


    @Override
    public void handlerSdkError(HttpRequest<?> request, SdkException e) {
        sdkError().accept("Client request fail, apiName={}, error=[{}]",
                HttpSdkSupport.toLoggerArray(request.matchSdkEnum().name(), e.getMessage()));
    }

    @Override
    public void handlerUnKnowError(HttpRequest<?> request, Throwable e) {
        unKnowError().accept("Client request fail, apiName={}, error=[{}]",
                HttpSdkSupport.toLoggerArray(request.matchSdkEnum().name(), e.getMessage()));
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
            normal().accept(msgFormat,
                    HttpSdkSupport.toLoggerArray(name, body, response, spendTotalTimeMillis));
        } else {
            String msgFormat = "Request fail, name={}, request={}, response={}, error={}, time={}ms";
            normal().accept(msgFormat,
                    HttpSdkSupport.toLoggerArray(name, body, response, info.getErrorMessage(), spendTotalTimeMillis));
        }
    }
}
