package top.osjf.assembly.sdk.http;

import copy.cn.hutool.v_5819.core.exceptions.ExceptionUtil;
import copy.cn.hutool.v_5819.core.io.IoUtil;
import org.springframework.util.StopWatch;
import top.osjf.assembly.sdk.SdkException;
import top.osjf.assembly.sdk.SdkUtils;
import top.osjf.assembly.sdk.process.DefaultErrorResponse;

import java.util.Map;

/**
 * The default client for the HTTP request mode of SDK.
 * <p>
 * It is also a step-by-step implementation process that involves verifying parameters, obtaining parameters,
 * HTTP type method requests, response preprocessing, and request transformation processing, including handling
 * {@link SdkException} exceptions and {@link Exception} unknown exceptions. Of course, there are also handling
 * of the final results, and common methods are placed. If this type is inherited, it can be rewritten.
 * <p>
 * Of course, you can define your request based on the {@link #apply(HttpRequestMethod, Map, Object, Boolean)} method.
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings("serial")
public class DefaultHttpClient<R extends HttpResponse> extends AbstractHttpClient<R> implements HttpSdkEnum.Action0 {

    /* ******* super Constructs ***********/

    public DefaultHttpClient(String url) {
        super(url);
    }

    @Override
    public R request() {
        return request0();
    }

    public R request0() {
        HttpRequest<R> request = getCurrentHttpRequest();
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
            body = requestParam == null ? "" : requestParam.toString();
            //Get Request Header
            Map<String, String> headers = request.getHeadMap();
            //requested action
            responseStr = apply(request.matchHttpSdk().getRequestMethod(), headers, requestParam,
                    request.montage());
            //pretreatment
            responseStr = preResponseStrHandler(request, responseStr);
            //convert
            response = convertToResponse(request, responseStr);
        } catch (SdkException e) {
            //sdk Exception catch
            sdkErrorLog(request, e);
            throwable = e;
            errorMsg = ExceptionUtil.stacktraceToOneLineString(throwable);
            response = DefaultErrorResponse.parseErrorResponse(errorMsg, DefaultErrorResponse.ErrorType.SDK,
                    request.getResponseCls());
        } catch (Exception e) {
            //unKnow Exception catch
            unKnowErrorLog(request, e);
            throwable = e;
            errorMsg = ExceptionUtil.stacktraceToOneLineString(throwable);
            response = DefaultErrorResponse.parseErrorResponse(errorMsg, DefaultErrorResponse.ErrorType.UN_KNOWN,
                    request.getResponseCls());
        } finally {
            //finally result input
            stopWatch.stop();
            finallyPrintLog(stopWatch.getTotalTimeMillis(), throwable, request, body, responseStr, errorMsg);
        }
        //close and clear thread param info
        IoUtil.close(this);
        return response;
    }

    @Override
    public String apply(HttpRequestMethod httpRequestMethod, Map<String, String> headers, Object requestParam,
                        Boolean montage) throws Exception {
        SdkUtils.checkContentType(headers);
        return httpRequestMethod.apply(getUrl(), headers, requestParam, montage);
    }

    public void sdkErrorLog(HttpRequest<R> request, Throwable e) {
        sdkError().accept("Client request fail, apiName={}, error=[{}]",
                SdkUtils.toLoggerArray(request.matchHttpSdk().name(), ExceptionUtil.stacktraceToOneLineString(e)));
    }

    public void unKnowErrorLog(HttpRequest<R> request, Throwable e) {
        unKnowError().accept("Client request fail, apiName={}, error=[{}]",
                SdkUtils.toLoggerArray(request.matchHttpSdk().name(), ExceptionUtil.stacktraceToOneLineString(e)));
    }

    public void finallyPrintLog(long totalTimeMillis, Throwable throwable, HttpRequest<R> request, String body,
                                String responseStr, String errorMsg) {
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
}
