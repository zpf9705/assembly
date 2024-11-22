package top.osjf.sdk.http.feign.bridging;

import com.google.common.collect.Lists;
import feign.Client;
import feign.Request;
import feign.Response;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.http.HttpRequestExecutor;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Feign client HTTP request executor interface.
 * <p>
 * This interface extends Feigns Client interface and a custom {@code HttpRequestExecutor}
 * interface for executing HTTP requests. It provides a default execute method that takes
 * an {@code ExecutorHttpRequest} object encapsulating HTTP request information, converts
 * it into a Feign request object,executes the request, and returns the response result.
 * <p>
 * In this method, the Feign request body and headers are first created based on the
 * {@code ExecutorHttpRequest} object. Then, a Feign request object is created and
 * execution options are set. Finally,the Feign request is executed, and the response result
 * is read and converted to a string for return.
 * <p>
 * The final call to the encapsulated component of {@code Feign} is to implement HTTP
 * calling,and its calling process completely depends on the encapsulated component of
 * {@code Feign}.Here, only the string response result is obtained in the final parsing
 * result.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface FeignClientHttpRequestExecutor extends Client, HttpRequestExecutor {

    @Override
    default String execute(ExecutorHttpRequest httpRequest) throws Exception {
        //Create a request body for feign.
        feign.Request.Body feignBody;
        Object requestBody = httpRequest.getBody();
        Charset charset = httpRequest.getCharset();

        if (requestBody != null) {
            feignBody = feign.Request.Body.create(requestBody.toString(), charset);
        } else/* Need to provide a default value of null */
            feignBody = feign.Request.Body.create((byte[]) null, charset);

        //The value of the request header is converted into a collection, which meets the requirements of feature.
        Map<String, Collection<String>> feignHeaders = new LinkedHashMap<>();
        Map<String, String> headers = httpRequest.getHeaders();
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach((k, v) -> feignHeaders.put(k, Lists.newArrayList(v)));
        }

        //Create a request object for feign.
        String methodName = httpRequest.getMethodName();
        feign.Request feignRequest = feign.Request
                .create(feign.Request.HttpMethod.valueOf(methodName),
                        //When integrating components, directly formatting the
                        // URL here involves attaching query parameters.
                        httpRequest.getUrl(),
                        feignHeaders,
                        feignBody,
                        null);/* We won't set up using third-party HTTP here.  */

        //Get the required configuration for the current thread.
        RequestOptions options = httpRequest.getOptions().getMethodOptions(methodName);
        //Set the HTTP execution option for feign.
        Request.Options feignOptions =
                new Request.Options(options.connectTimeout(), options.connectTimeoutUnit(),
                        options.readTimeout(), options.readTimeoutUnit(), options.isFollowRedirects());
        //Put it into the thread configuration of feign.
        feignOptions.setMethodOptions(methodName, feignOptions);

        //Read the stream response result of the feature client and return the request result
        // in the form of a string for subsequent conversion operations.

        //Automatically close the resource information that responds.
        try (Response response = execute(feignRequest, feignOptions)) {
            return new String(IOUtils.readAllBytes(response.body().asInputStream()), charset);
        }
    }
}
