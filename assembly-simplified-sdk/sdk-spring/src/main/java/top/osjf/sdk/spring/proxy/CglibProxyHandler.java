package top.osjf.sdk.spring.proxy;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * {@link MethodInterceptor} for {@link ProxyHandler} refit.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface CglibProxyHandler extends MethodInterceptor, ProxyHandler {

    @Override
    default Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
        return handle(o, method, objects);
    }
}
