package top.osjf.assembly.util.lang;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Reflect utils from {@link ReflectUtil}, and this category points out the expansion function.
 *
 * @author zpf
 * @since 1.0.4
 */
public final class ReflectUtils extends ReflectUtil {

    private ReflectUtils() {
    }

    //**************************************** Extends Proxy ***********************************************

    public static <T> T newProxyInstance(InvocationHandler invocationHandler, Class<?>... interfaces) {
        return newProxyInstance(ClassUtil.getClassLoader(), invocationHandler, interfaces);
    }

    @SuppressWarnings("unchecked")
    public static <T> T newProxyInstance(ClassLoader classloader, InvocationHandler invocationHandler,
                                         Class<?>... interfaces) {
        return (T) Proxy.newProxyInstance(classloader, interfaces, invocationHandler);
    }
}
