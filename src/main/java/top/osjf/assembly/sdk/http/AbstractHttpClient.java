package top.osjf.assembly.sdk.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONValidator;
import copy.cn.hutool.v_5819.core.collection.CollectionUtil;
import copy.cn.hutool.v_5819.core.util.StrUtil;
import copy.cn.hutool.v_5819.logger.StaticLog;
import org.springframework.lang.NonNull;
import top.osjf.assembly.sdk.client.AbstractClient;
import top.osjf.assembly.sdk.process.DefaultResponse;
import top.osjf.assembly.sdk.process.Request;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;


/**
 * SDK's HTTP request mode client abstract class inherits {@link AbstractClient} to implement the
 * client's caching for each request URL, defines the default conversion method for the HTTP request
 * process, as well as default log input, parameter thread acquisition, and so on.
 *
 * @author zpf
 * @since 1.1.1
 */
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
     * @return Return a {@link Request} param within {@link HttpRequest}.
     */
    public HttpRequest<R> getCurrentHttpRequest() {
        return (HttpRequest<R>) getCurrentRequest();
    }

    /* ******* Default provider ***********/

    @Override
    @NonNull
    public String preResponseStrHandler(Request<R> request, String responseStr) {
        return responseStr;
    }

    @Override
    @NonNull
    public R convertToResponse(Request<R> request, String responseStr) {
        R response;
        JSONValidator jsonValidator = StrUtil.isBlank(responseStr) ? null : JSONValidator.from(responseStr);
        if (Objects.isNull(jsonValidator) || !jsonValidator.validate()) {
            String jsonData = JSON.toJSONString(DefaultResponse.buildDataErrorResponse(responseStr));
            response = JSON.parseObject(jsonData, request.getResponseCls());
        } else if (Objects.equals(JSONValidator.Type.Array, jsonValidator.getType())) {
            List<R> responses = JSONArray.parseArray(responseStr, request.getResponseCls());
            if (CollectionUtil.isEmpty(responses)) {
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
