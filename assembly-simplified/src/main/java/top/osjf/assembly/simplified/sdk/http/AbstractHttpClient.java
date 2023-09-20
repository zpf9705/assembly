package top.osjf.assembly.simplified.sdk.http;

import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.osjf.assembly.simplified.sdk.client.AbstractClient;
import top.osjf.assembly.simplified.sdk.process.DefaultErrorResponse;
import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;


/**
 * SDK's HTTP request mode client abstract class inherits {@link AbstractClient} to implement the
 * client's caching for each request URL, defines the default conversion method for the HTTP request
 * process, as well as default log input, parameter thread acquisition, and so on.
 * <p>
 * Provide two default method rewrites.<br>
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
        JSONValidator jsonValidator = StringUtils.isBlank(responseStr) ? null : JSONValidator.from(responseStr);
        if (Objects.isNull(jsonValidator) || !jsonValidator.validate()) {
            response = DefaultErrorResponse.parseErrorResponse(responseStr, DefaultErrorResponse.ErrorType.DATA,
                    request.getResponseCls());
        } else if (Objects.equals(JSONValidator.Type.Array, jsonValidator.getType())) {
            List<R> responses = JSONArray.parseArray(responseStr, request.getResponseCls());
            if (CollectionUtils.isEmpty(responses)) {
                response = responses.get(0);
            } else {
                response = JSON.parseObject("{}", request.getResponseCls());
            }
        } else {
            response = JSON.parseObject(responseStr, request.getResponseCls());
        }
        return response;
    }

    @Override
    public BiConsumer<String, Object[]> normal() {
        return StaticLog::info;
    }

    @Override
    public BiConsumer<String, Object[]> sdkError() {
        return StaticLog::error;
    }

    @Override
    public BiConsumer<String, Object[]> unKnowError() {
        return StaticLog::error;
    }
}
