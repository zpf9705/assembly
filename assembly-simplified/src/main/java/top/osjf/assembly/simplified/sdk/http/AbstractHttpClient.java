package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.simplified.sdk.client.AbstractClient;
import top.osjf.assembly.simplified.sdk.process.DefaultErrorResponse;
import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.FastJsonUtils;
import top.osjf.assembly.util.logger.Console;

import java.util.List;
import java.util.function.BiConsumer;


/**
 * SDK's HTTP request mode client abstract class inherits {@link AbstractClient} to implement the
 * client's caching for each request URL, defines the default conversion method for the HTTP request
 * process, as well as default log input, parameter thread acquisition, and so on.
 *
 * <p>Provide two default method rewrites.<br>
 * {@link #preResponseStrHandler(Request, String)} The default response is in JSON form, without conversion,
 * and returns directly.<br>
 * {@link #convertToResponse(Request, String)} Directly converts the JSON form to the desired response type.<br>
 *
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
     * Obtain the interface parameters for this HTTP request.
     *
     * @return Return a {@link top.osjf.assembly.simplified.sdk.process.Request} param within {@link HttpRequest}.
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
        if (!FastJsonUtils.isValid(responseStr)) {
            response = DefaultErrorResponse.parseErrorResponse(responseStr, DefaultErrorResponse.ErrorType.DATA,
                    request.getResponseCls());
        } else if (FastJsonUtils.isArray(responseStr)) {
            List<R> responses = FastJsonUtils.parseArray(responseStr, request.getResponseCls());
            if (CollectionUtils.isEmpty(responses)) {
                response = responses.get(0);
            } else {
                response = FastJsonUtils.parseEmptyObject(request.getResponseCls());
            }
        } else {
            response = FastJsonUtils.parseObject(responseStr, request.getResponseCls());
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
