package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.simplified.sdk.client.AbstractClient;
import top.osjf.assembly.simplified.sdk.process.DefaultErrorResponse;
import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.json.FastJsonUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.logger.Console;

import java.util.List;
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
 * @param <R> Implement a unified response class data type.
 * @author zpf
 * @since 1.1.1
 */
@SuppressWarnings("serial")
public abstract class AbstractHttpClient<R extends HttpResponse> extends AbstractClient<R> {

    //Requested remote interface address
    private final String url;

    public AbstractHttpClient(String url) {
        super(url);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Return the request that was temporarily placed in the thread copy as {@link Request}.
     * @return the request that was temporarily placed in the thread copy as {@link Request}.
     */
    public HttpRequest<R> getCurrentHttpRequest() {
        return (HttpRequest<R>) getCurrentRequest();
    }

    /* ******* Default provider ***********/

    @Override
    @NotNull
    public String preResponseStrHandler(Request<R> request, String responseStr) {
        return responseStr;
    }

    @Override
    @NotNull
    public R convertToResponse(Request<R> request, String responseStr) {
        R response;
        Object type = request.getResponseRequiredType();
        if (FastJsonUtils.isValidObject(responseStr)) {
            response = FastJsonUtils.parseObject(responseStr, type);
        } else if (FastJsonUtils.isValidArray(responseStr)) {
            List<R> responses = FastJsonUtils.parseArray(responseStr,type);
            if (CollectionUtils.isNotEmpty(responses)) {
                response = responses.get(0);
            } else {
                response = FastJsonUtils.toEmptyObj(type);
            }
        } else {
            response = DefaultErrorResponse
                    .parseErrorResponse(responseStr, DefaultErrorResponse.ErrorType.DATA, request);
        }
        return response;
    }

    @Override
    public BiConsumer<String, Object[]> normal() {
        return Console::info;
    }

    @Override
    public BiConsumer<String, Object[]> sdkError() {
        return Console::error;
    }

    @Override
    public BiConsumer<String, Object[]> unKnowError() {
        return Console::error;
    }
}
