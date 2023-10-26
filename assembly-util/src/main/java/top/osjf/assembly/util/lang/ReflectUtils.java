package top.osjf.assembly.util.lang;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.data.Sixfold;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * Reflect utils from {@link ReflectUtil}, and this category points out the expansion function.
 *
 * @author zpf
 * @since 1.0.4
 */
public final class ReflectUtils extends ReflectUtil {

    private ReflectUtils() {
    }

    //**************************************** Extends ***********************************************

    /**
     * Check and obtain: The return value executed by the target object after searching
     * for the corresponding method based on whether to ignore method name capitalization.
     * @param folds Six parameter bearing object.
     * @param <T>   Expected the type of return value allowed.
     * @return The return value of the execution method can be allowed to be empty.
     */
    @CanNull
    public static <T> T getAndCheckMethodValue(Sixfold<Object, String, Boolean, Class<T>, Class<?>[],
            Object[]> folds) {
        if (folds == null || !folds.getQuadruple().isChinNotNull()) {
            return null;
        }
        //-----not be null------//
        final Object obj = folds.getV1();
        final String methodName = folds.getV2();
        final Boolean ignoredMethodNameCase = folds.getV3();
        final Class<T> requiredType = folds.getV4();
        //-----can be null ----//
        final Class<?>[] methodParamTypes = folds.getV5();
        final Object[] methodInvokeParams = folds.getV6();
        //-----method find----//
        Method method = getMethod(obj.getClass(), ignoredMethodNameCase, methodName, methodParamTypes);
        if (method == null) {
            return null;
        }
        //-----check requiredType----//
        if (!Objects.equals(method.getReturnType(), requiredType)) {
            return null;
        }
        //-----method invoke----//
        Object result = invoke(obj, method, methodInvokeParams);
        return ConvertUtils.convert(requiredType, result);
    }

    public static <T> T newProxyInstance(InvocationHandler invocationHandler, Class<?>... interfaces) {
        return newProxyInstance(ClassUtil.getClassLoader(), invocationHandler, interfaces);
    }

    @SuppressWarnings("unchecked")
    public static <T> T newProxyInstance(ClassLoader classloader, InvocationHandler invocationHandler,
                                         Class<?>... interfaces) {
        return (T) Proxy.newProxyInstance(classloader, interfaces, invocationHandler);
    }
}
