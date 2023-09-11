package top.osjf.assembly.sdk.client;

import com.alibaba.fastjson.JSON;
import copy.cn.hutool.v_5819.core.exceptions.ExceptionUtil;
import copy.cn.hutool.v_5819.core.io.IoUtil;
import copy.cn.hutool.v_5819.http.ContentType;
import copy.cn.hutool.v_5819.logger.StaticLog;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import top.osjf.assembly.sdk.SdkException;
import top.osjf.assembly.utils.HttpUtils;

import java.util.Map;
import java.util.Objects;

/**
 * The HTTP based request client supports four types of request methods: get, post, put, and del.
 * <p>
 * The parameter selection is a common and standardized form, and the default request body type is JSON.
 * <p>
 * Currently, it also supports {@code application/x-www-form-urlencoded} type request methods.
 * <p>
 * More specific support for request body types will be added when encountering them
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
        Request<R> request = getCurrentParam();
        StopWatch stopWatch = new StopWatch();
        //Request Timer
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
            Map<String, Object> paramsMap = request.toParamsMap();
            //Get Request Header
            Map<String, String> headers = request.getHeadMap();
            /*
             * Here, different plans will be made for the request
             * body string based on the type of response context set
             */
            body = getBodyStringWithContentType(request, headers);
            //requested action
            responseStr = caseRequestMethodDoThat(request, headers, paramsMap, body);
            /*
             * This step requires special conversion
             * requirements for response data Final shift to JSON data
             */
            responseStr = this.convert(request, responseStr);
            /*
             * This step is mainly to convert the encapsulated response class,
             * which is uniformly converted by json Type conversion
             * This requires that the interaction in the response must meet the
             * requirements of JSON return, or the innermost layer should be JSON data
             */
            response = this.JsonToConvertResponse(request, responseStr);

        } catch (SdkException e) {
            StaticLog.error("Client request fail, apiName={}, error=[{}]",
                    request.matchApi().name(), ExceptionUtil.stacktraceToOneLineString(e));
            throwable = e;
            errorMsg = ExceptionUtil.stacktraceToOneLineString(throwable);
            String jsonData = JSON.toJSONString(DefaultResponse.buildResponse(e.getCode(), e.getMsg()));
            response = JSON.parseObject(jsonData, request.getResponseCls());
        } catch (Exception e) {
            StaticLog.error("Client request fail, apiName={}, error=[{}]",
                    request.matchApi().name(), ExceptionUtil.stacktraceToOneLineString(e));
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
                StaticLog.info(msgFormat, request.matchApi().name(), body, responseStr,
                        totalTimeMillis);
            } else {
                String msgFormat = "Request fail, name={}, request={}, response={}, error={}, time={}ms";
                StaticLog.info(msgFormat, request.matchApi().name(), body, responseStr,
                        errorMsg, totalTimeMillis);
            }
        }

        //close and clear thread param info
        IoUtil.close(this);
        return response;
    }

    /**
     * Make different plans for the request body based on different context types
     *
     * @param request Request parameter encapsulation
     * @param headers Header information
     * @return body value
     */
    private String getBodyStringWithContentType(Request<R> request, Map<String, String> headers) {
        String body;
        String contentType = headers.get("Content-Type");
        //application/json
        if (CollectionUtils.isEmpty(headers) || Objects.equals(contentType, ContentType.JSON.getValue())) {
            body = JSON.toJSONString(request.toParamsMap());
            //application/x-www-form-urlencoded
        } else if (Objects.equals(contentType, ContentType.FORM_URLENCODED.getValue())) {
            body = (String) request.getBody();
        } else {
            //default to json
            body = JSON.toJSONString(request.toParamsMap());

            //other any content-type if meet need to add
        }
        return body;
    }

    /**
     * HTTP requests based on different methods
     *
     * @param request   Request parameter encapsulation
     * @param headers   Header information
     * @param paramsMap Parameter map
     * @param body      Certified body
     * @return return value
     */
    public String caseRequestMethodDoThat(Request<R> request,
                                          Map<String, String> headers,
                                          Map<String, Object> paramsMap,
                                          String body) {
        String responseStr;
        //get request url
        String url = getUrl();
        //with request method switch to request any
        switch (request.matchApi().getRequestMethod()) {
            case GET:
                responseStr = HttpUtils.get(url, headers, paramsMap);
                break;
            case POST:
                responseStr = HttpUtils.post(url, headers, body);
                break;
            case PUT:
                responseStr = HttpUtils.put(url, headers, body);
                break;
            case DELETE:
                responseStr = HttpUtils.delete(url, headers);
                break;
            default:
                //default to empty map string
                responseStr = "{}";
                break;
        }
        return responseStr;
    }
}
