package top.osjf.sdk.spring.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * {@link InvocationHandler} for {@link ProxyHandler} refit.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface JdkProxyHandler extends InvocationHandler, ProxyHandler {

    @Override
    default Object invoke(Object proxy, Method method, Object[] args) {
        return handle(proxy, method, args);
    }
}
