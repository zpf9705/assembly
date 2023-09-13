package top.osjf.assembly.sdk;

import copy.cn.hutool.v_5819.core.util.ArrayUtil;
import copy.cn.hutool.v_5819.core.util.StrUtil;
import org.springframework.lang.NonNull;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.RequestInvoker;
import top.osjf.assembly.sdk.process.Response;
import top.osjf.assembly.support.AbstractJdkProxySupport;

import java.lang.reflect.Method;

/**
 * Inheriting {@link AbstractJdkProxySupport} implements handing over the object of the jdk dynamic proxy
 * to the spring container for management.
 * <p>When this object is called, the {@link #invoke(Object, Method, Object[])} method will be executed
 * and passed to this abstract class.</p>
 * <p>
 * This class takes on the common parameter processing and converts it into the parameters required for SDK execution.</p>
 * <p>
 * It can use the method case of reference {@link ClientUtils#execute(String, Request)} to inherit the
 * real processing object, such as {@link SdkProxyBeanDefinition}. All real processing objects need to
 * rewrite the method {@link #apply(Request)}.
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractSdkProxyInvoker<T> extends AbstractJdkProxySupport<T> implements RequestInvoker {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return invoke0(proxy, method, args);
    }

    public Object invoke0(Object proxy, Method method, Object[] args) {
        //Request parameters must be passed.
        if (ArrayUtil.isEmpty(args)) throw new IllegalArgumentException(
                StrUtil.format("The method {} of proxy class {} must pass the request parameter " +
                        "when executing the sdk option.", proxy.getClass().getName(), method.getName()));
        //Specify that only one request parameter of the encapsulation type is passed.
        Object arg = args[0];
        //Determine if it is of type AbstractRequestParams
        if (!(arg instanceof Request)) throw new IllegalArgumentException(
                StrUtil.format("You must encapsulate your SDK request class to inherit from top.osjf.assembly" +
                        ".sdk.process.Request"));
        return apply((Request<?>) arg);
    }

    @Override
    public abstract Response apply(@NonNull Request<?> request);
}
