package top.osjf.assembly.simplified.sdk.proxy;

import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.simplified.sdk.process.ResponseData;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.lang.reflect.Method;

/**
 * When processing SDK proxy requests, at the method level, after creating {@link Request},
 * this interface method {@link #postProcessRequestBeforeHandle(Request, Class, Method,Object[])}
 * can be used to customize the above {@link Request} parameters and obtain the final
 * request parameter result, which cannot be empty by default and returns the original
 * created request parameter.
 *
 * <p>When processing SDK proxy response data, at the method level, after obtaining the
 * response result (specified data type for {@link top.osjf.assembly.simplified.sdk.process.Response}
 * or {@link ResponseData}), this interface method {@link #postProcessResultAfterHandle(Object, Class, Method,Object[])}
 * can be used to customize and modify the above response result parameters, and obtain the
 * final request response result. The default cannot be empty, and return the original
 * processing to obtain the request response output parameter.
 *
 * <p>Special customization can be applied to the result set based on the target type of
 * the proxy in the provided method and the method object of the proxy.
 *
 * <p>When specifying multiple {@link HandlerPostProcessor}, if you want to specify the
 * execution order, you can use the concept of {@link org.springframework.core.annotation.Order}
 * to specify it. Support for it has been added because the implementation class of this
 * interface requires adding a Spring container to take effect.
 *
 * <p>As mentioned above, there are some functions that are specified by the SDK itself,
 * but you can also adapt according to the situation, because this interface occurs during
 * the request runtime. You can use the relevant methods of this interface to add related
 * actions before and after the SDK request to enhance its functionality.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.7
 */
public interface HandlerPostProcessor {

    /**
     * You can return a new {@link Request} in this method, or you can
     * use relevant information to record and customize some of your
     * actions before the request without customizing the parameters.
     *
     * @param request     The original request parameters.
     * @param targetType  The target type being represented.
     * @param proxyMethod The target method of the agent.
     * @param args        The real parameters executed by the
     *                    proxy method.
     * @return Customized request parameters.
     */
    @NotNull
    default Request<?> postProcessRequestBeforeHandle(Request<?> request, Class<?> targetType, Method proxyMethod,
                                                      @CanNull Object[] args) {
        return request;
    }

    /**
     * In this method, a new response result can be returned, and the input
     * type is determined based on your requirements for the corresponding
     * action. Of course, customization is not necessary, and relevant
     * information can be used to record and customize some of your actions
     * after the request.
     *
     * @param result      The original corresponding parameters.
     * @param targetType  The target type being represented.
     * @param proxyMethod The target method of the agent.
     * @param args        The real parameters executed by the
     *                    proxy method.
     * @return Customized response result.
     */
    @CanNull
    default Object postProcessResultAfterHandle(@CanNull Object result, Class<?> targetType, Method proxyMethod,
                                                @CanNull Object[] args) {
        return result;
    }

    /**
     * Returns the proxy type specified by this {@link HandlerPostProcessor}.
     * <p>If you specify this type, then this {@link HandlerPostProcessor} will
     * only serve this target type, and by default {@literal null} will serve
     * all target types.
     *
     * @return the proxy type specified by this {@link HandlerPostProcessor}.
     */
    default Class<?> appointTarget() {
        return null;
    }
}
