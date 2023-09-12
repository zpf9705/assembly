package top.osjf.assembly.sdk.client;

import com.alibaba.fastjson.JSON;
import copy.cn.hutool.v_5819.core.exceptions.ExceptionUtil;
import copy.cn.hutool.v_5819.core.io.IoUtil;
import org.springframework.util.StopWatch;
import top.osjf.assembly.sdk.SdkException;
import top.osjf.assembly.sdk.SdkUtils;
import top.osjf.assembly.sdk.process.DefaultResponse;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.Response;

import java.util.Map;
import java.util.Objects;

/**
 * Implementation class of http mode for {@link Client}.
 *
 * @author zpf
 * @since 1.1.0
 */
public class HttpClient<R extends Response> extends AbstractClient<R> {

    private static final long serialVersionUID = -7155604086466276914L;

    /* ******* super Constructs ***********/

    public HttpClient(String url) {
        super(url);
    }

    @Override
    public R request() {
        Request<R> request = getCurrentRequest();
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
            SdkUtils.checkContentType(headers);
            responseStr = request.matchSdk()
                    .getRequestMethod()
                    .action(getUrl(), headers, requestParam);
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
                    SdkUtils.toLoggerArray(request.matchSdk().name(), ExceptionUtil.stacktraceToOneLineString(e)));
            throwable = e;
            errorMsg = ExceptionUtil.stacktraceToOneLineString(throwable);
            String jsonData = JSON.toJSONString(DefaultResponse.buildResponse(e.getMessage()));
            response = JSON.parseObject(jsonData, request.getResponseCls());
        } catch (Exception e) {
            otherError().accept("Client request fail, apiName={}, error=[{}]",
                    SdkUtils.toLoggerArray(request.matchSdk().name(), ExceptionUtil.stacktraceToOneLineString(e)));
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
                        SdkUtils.toLoggerArray(request.matchSdk().name(), body, responseStr, totalTimeMillis));
            } else {
                String msgFormat = "Request fail, name={}, request={}, response={}, error={}, time={}ms";
                normal().accept(msgFormat,
                        SdkUtils.toLoggerArray(request.matchSdk().name(), body, responseStr, errorMsg, totalTimeMillis));
            }
        }
        //close and clear thread param info
        IoUtil.close(this);
        return response;
    }
}
