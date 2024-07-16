package top.osjf.sdk.spring.proxy;

import java.lang.reflect.Method;

/**
 * Proxy callback processing interface.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface ProxyHandler {

    /**
     * Proxy callback processing method.
     *
     * @param obj    Generally, it is a proxy object.
     * @param method The method object of the proxy.
     * @param args   Method parameters.
     * @return Execute the processing result.
     */
    Object handle(Object obj, Method method, Object[] args);
}
