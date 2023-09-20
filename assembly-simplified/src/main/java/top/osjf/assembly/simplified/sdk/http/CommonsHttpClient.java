package top.osjf.assembly.simplified.sdk.http;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import org.springframework.util.StopWatch;
import top.osjf.assembly.simplified.sdk.SdkException;
import top.osjf.assembly.simplified.sdk.SdkUtils;
import top.osjf.assembly.simplified.sdk.process.DefaultErrorResponse;

import java.util.Map;

/**
 * The default client for the commons HTTP request mode of SDK.
 * <p>
 * It is also a step-by-step implementation process that involves verifying parameters, obtaining parameters,
 * HTTP type method requests, response preprocessing, and request transformation processing, including handling
 * {@link SdkException} exceptions and {@link Exception} unknown exceptions. Of course, there are also handling
 * of the final results, and common methods are placed. If this type is inherited, it can be rewritten.
 * <p>
 * Of course, you can define your request based on the {@link #doRequest(HttpRequestMethod, Map, Object, Boolean)}
 * method.
 *
 * @author zpf
 * @since 1.1.1
 */
@SuppressWarnings("serial")
public class CommonsHttpClient<R extends HttpResponse> extends AbstractHttpClient<R> implements HttpSdkEnum.Action0,
        HttpResultSolver {

    public CommonsHttpClient(String url) {
        super(url);
    }

    @Override
    public R request() {
        HttpRequest<R> request = getCurrentHttpRequest();
        R response;
        String responseStr = null;
        Throwable throwable = null;
        StopWatch watch = new StopWatch();
        try {
            //Verification of necessary parameters
            request.validate();
            watch.start();
            //Obtain request body map
            Object requestParam = request.getRequestParam();
            //Get Request Header
            Map<String, String> headers = request.getHeadMap();
            //requested action
            responseStr = doRequest(request.matchHttpSdk().getRequestMethod(), headers, requestParam,
                    request.montage());
            //pretreatment
            responseStr = preResponseStrHandler(request, responseStr);
            //convert
            response = convertToResponse(request, responseStr);
        } catch (SdkException e) {
            //sdk Exception catch
            handlerSdkError(request, e);
            throwable = e;
            response = DefaultErrorResponse.parseErrorResponse(throwable, DefaultErrorResponse.ErrorType.SDK,
                    request.getResponseCls());
        } catch (Exception e) {
            //unKnow Exception catch
            handlerUnKnowError(request, e);
            throwable = e;
            response = DefaultErrorResponse.parseErrorResponse(throwable, DefaultErrorResponse.ErrorType.UN_KNOWN,
                    request.getResponseCls());
        } finally {
            watch.stop();
            //finally result input
            finallyHandler(ExecuteInfoBuild.builder().requestAccess(request)
                    .spend(watch.getTotalTimeMillis())
                    .maybeError(throwable)
                    .response(responseStr)
                    .build());
        }
        //close and clear thread param info
        IoUtil.close(this);
        return response;
    }

    @Override
    public String doRequest(HttpRequestMethod method, Map<String, String> headers, Object requestParam,
                            Boolean montage) throws Exception {
        SdkUtils.checkContentType(headers);
        return null;
    }

    @Override
    public void handlerSdkError(HttpRequest<?> request, SdkException e) {
        sdkError().accept("Client request fail, apiName={}, error=[{}]",
                SdkUtils.toLoggerArray(request.matchHttpSdk().name(), ExceptionUtil.stacktraceToOneLineString(e)));
    }

    @Override
    public void handlerUnKnowError(HttpRequest<?> request, Throwable e) {
        unKnowError().accept("Client request fail, apiName={}, error=[{}]",
                SdkUtils.toLoggerArray(request.matchHttpSdk().name(), ExceptionUtil.stacktraceToOneLineString(e)));
    }

    @Override
    public void finallyHandler(ExecuteInfo info) {
        HttpRequest<?> httpRequest = info.getHttpRequest();
        String name = httpRequest.matchHttpSdk().name();
        Object requestParam = httpRequest.getRequestParam();
        String body = requestParam != null ? requestParam.toString() : "";
        String response = info.getResponse();
        long spendTotalTimeMillis = info.getSpendTotalTimeMillis();
        if (info.noHappenError().get()) {
            String msgFormat = "Request end, name={}, request={}, response={}, time={}ms";
            normal().accept(msgFormat,
                    SdkUtils.toLoggerArray(name, body, response, spendTotalTimeMillis));
        } else {
            String msgFormat = "Request fail, name={}, request={}, response={}, error={}, time={}ms";
            normal().accept(msgFormat,
                    SdkUtils.toLoggerArray(name, body, response, info.getErrorMessage(), spendTotalTimeMillis));
        }
    }
}
