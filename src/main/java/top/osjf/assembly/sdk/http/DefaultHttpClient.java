package top.osjf.assembly.sdk.http;

import com.alibaba.fastjson.JSON;
import copy.cn.hutool.v_5819.core.exceptions.ExceptionUtil;
import copy.cn.hutool.v_5819.core.io.IoUtil;
import io.reactivex.rxjava3.functions.Function4;
import org.springframework.util.StopWatch;
import top.osjf.assembly.sdk.SdkException;
import top.osjf.assembly.sdk.SdkUtils;
import top.osjf.assembly.sdk.process.DefaultResponse;

import java.util.Map;
import java.util.Objects;

/**
 * The default client for the HTTP request mode of SDK.
 *
 * @author zpf
 * @see #request()
 * @since 1.1.0
 */
public class DefaultHttpClient<R extends HttpResponse> extends AbstractHttpClient<R> implements
        Function4<HttpRequestMethod, Map<String, String>, Object, Boolean, String> {

    /* ******* super Constructs ***********/

    public DefaultHttpClient(String url) {
        super(url);
    }

    @Override
    public R request() {
        HttpRequest<R> request = getCurrentHttpRequest();
        //Request Timer
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        R response;
        String body = null;
        String responseStr = null;
        String errorMsg = null;
        Throwable throwable = null;
        try {
            //Verification of necessary parameters
            request.validate();
            //Obtain request body map
            Object requestParam = request.getRequestParam();
            //give body
            body = Objects.isNull(requestParam) ? "" : requestParam.toString();
            //Get Request Header
            Map<String, String> headers = request.getHeadMap();
            //requested action
            responseStr = apply(request.matchHttpSdk().getRequestMethod(), headers, requestParam,
                    request.montage());
            /*
             * This step requires special conversion
             * requirements for response data Final shift to JSON data
             */
            responseStr = this.preResponseStrHandler(request, responseStr);
            /*
             * This step is mainly to convert the encapsulated response class,
             * which is uniformly converted by json Type conversion
             * This requires that the interaction in the response must meet the
             * requirements of JSON return, or the innermost layer should be JSON data
             */
            response = this.convertToResponse(request, responseStr);
        } catch (SdkException e) {
            sdkError().accept("Client request fail, apiName={}, error=[{}]",
                    SdkUtils.toLoggerArray(request.matchHttpSdk().name(), ExceptionUtil.stacktraceToOneLineString(e)));
            throwable = e;
            errorMsg = ExceptionUtil.stacktraceToOneLineString(throwable);
            String jsonData = JSON.toJSONString(DefaultResponse.buildSdkExceptionResponse(e.getMessage()));
            response = JSON.parseObject(jsonData, request.getResponseCls());
        } catch (Exception e) {
            otherError().accept("Client request fail, apiName={}, error=[{}]",
                    SdkUtils.toLoggerArray(request.matchHttpSdk().name(), ExceptionUtil.stacktraceToOneLineString(e)));
            throwable = e;
            errorMsg = ExceptionUtil.stacktraceToOneLineString(throwable);
            String jsonData = JSON.toJSONString(DefaultResponse.buildUnknownResponse(e.getMessage()));
            response = JSON.parseObject(jsonData, request.getResponseCls());
        } finally {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();

            //logger console
            if (throwable == null) {
                String msgFormat = "Request end, name={}, request={}, response={}, time={}ms";
                normal().accept(msgFormat,
                        SdkUtils.toLoggerArray(request.matchHttpSdk().name(), body, responseStr, totalTimeMillis));
            } else {
                String msgFormat = "Request fail, name={}, request={}, response={}, error={}, time={}ms";
                normal().accept(msgFormat,
                        SdkUtils.toLoggerArray(request.matchHttpSdk().name(), body, responseStr, errorMsg, totalTimeMillis));
            }
        }
        //close and clear thread param info
        IoUtil.close(this);
        return response;
    }

    @Override
    public String apply(HttpRequestMethod httpRequestMethod, Map<String, String> headers, Object requestParam,
                        Boolean montage) {
        SdkUtils.checkContentType(headers);
        return httpRequestMethod.apply(getUrl(), headers, requestParam, montage);
    }
}
