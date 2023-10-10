package top.osjf.assembly.util.lang;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Reflection tool class function implementation in  {@link ReflectUtil}.
 *
 * @author zpf
 * @since 1.0.3
 */
public final class Reflects extends ReflectUtil {
    private Reflects() {
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
