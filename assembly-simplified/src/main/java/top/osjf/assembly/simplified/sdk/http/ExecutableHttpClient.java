package top.osjf.assembly.simplified.sdk.http;

import org.springframework.util.StopWatch;
import top.osjf.assembly.simplified.sdk.SdkException;
import top.osjf.assembly.simplified.sdk.SdkUtils;
import top.osjf.assembly.simplified.sdk.process.DefaultErrorResponse;
import top.osjf.assembly.util.exceptions.ExceptionUtils;

import java.util.Map;

/**
 * The default client for the commons HTTP request mode of SDK.
 *
 * <p>It is also a step-by-step implementation process that involves
 * verifying parameters, obtaining parameters, HTTP type method requests,
 * response preprocessing, and request transformation processing, including
 * handling {@link SdkException} exceptions and {@link Exception} unknown
 * exceptions.
 * <p>Of course, there are also handling of the final results, and common
 * methods are placed.
 * <p>If this type is inherited, it can be rewritten.
 *
 * <p>Of course, you can define your request based on the
 * {@link #doRequest(HttpRequestMethod, Map, Object, Boolean)} method.
 *
 * @param <R> Implement a unified response class data type.
 * @author zpf
 * @since 1.1.1
 */
@SuppressWarnings("serial")
public abstract class ExecutableHttpClient<R extends HttpResponse> extends AbstractHttpClient<R>
        implements HttpSdkEnum.Action0, HttpResultSolver {

    public ExecutableHttpClient(String url) {
        super(url);
    }

    @Override
    public R request() {

        //Get the request parameters for the current thread.
        HttpRequest<R> request = getCurrentHttpRequest();

        //Define the required parameters for this request.
        R response;
        String responseStr = null;
        Throwable throwable = null;

        //Enable request timing operation
        StopWatch watch = new StopWatch();
        watch.start();

        //Key operation steps: try the package.
        try {

            //Custom validation of request parameters.
            request.validate();

            //Obtain the real request parameters.
            Object requestParam = request.getRequestParam();

            //Get the request header parameters.
            Map<String, String> headers = request.getHeadMap();

            //Execute this request, route according to the request type, and handle the parameters.
            responseStr = doRequest(request.matchHttpSdk().getRequestMethod(), headers, requestParam, request.montage());

            //Preprocessing operation for request results.
            responseStr = preResponseStrHandler(request, responseStr);

            //The result conversion operation of the request result.
            response = convertToResponse(request, responseStr);

        } catch (SdkException e) {

            //Capture anomalies in SDK and provide a conversion reminder.
            handlerSdkError(request, e);
            throwable = e;
            response = DefaultErrorResponse.parseErrorResponse(throwable, DefaultErrorResponse.ErrorType.SDK, request);

        } catch (Exception e) {

            //Capture unknown exceptions and provide a transition reminder.
            handlerUnKnowError(request, e);
            throwable = e;
            response = DefaultErrorResponse.parseErrorResponse(throwable, DefaultErrorResponse.ErrorType.UN_KNOWN,
                    request);

        } finally {

            //Stop timing.
            watch.stop();

            //Hand over the call information to the final processing project.
            finallyHandler(ExecuteInfoBuild.builder().requestAccess(request)
                    .spend(watch.getTotalTimeMillis())
                    .maybeError(throwable)
                    .response(responseStr)
                    .build());
        }

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
                SdkUtils.toLoggerArray(request.matchHttpSdk().name(), ExceptionUtils.stacktraceToOneLineString(e)));
    }

    @Override
    public void handlerUnKnowError(HttpRequest<?> request, Throwable e) {
        unKnowError().accept("Client request fail, apiName={}, error=[{}]",
                SdkUtils.toLoggerArray(request.matchHttpSdk().name(), ExceptionUtils.stacktraceToOneLineString(e)));
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
