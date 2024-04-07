package top.osjf.assembly.simplified.scope;

import org.aspectj.lang.JoinPoint;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.ConvertUtils;

import java.util.Arrays;

/**
 * Abstract scope definition classes, rewriting empty loading methods,
 * and dynamic entry requirements that may be required after loading special beans.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.1.6
 */
public abstract class AbstractScopeLoader implements ScopeLoader {

    @Override
    public void load() {
    }

    //============================= Proxy interception assistance ============================= //

    /**
     * The {@link JoinPoint} that returns the running state comes from aspectj<br>
     * If obtained using this method in a non aspectj environment, the returned
     * value is {@link org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint}<br>
     * Using aspectj interception, the return is {@code org.aspectj.runtime.reflect.JoinPointImpl}
     *
     * <p>The acquisition of the above information needs to be carried out in a proxy
     * environment, and the current proxy method needs to be ProxyMethodInvocation,
     * which is a Spring method proxy. Please understand the principle of Spring aop
     * for more details
     *
     * <p>Please note the above two points. The above logic comes from
     * {@link AspectJAroundAdvice#currentJoinPoint()}
     */
    public JoinPoint currentJoinPoint() {
        return AbstractAspectJAdvice.currentJoinPoint();
    }

    /*** Empty array reference object.*/
    private static final Object[] empty_array = {};

    /**
     * Retrieve all parameters of the current pointcut method based
     * on method {@link #currentJoinPoint()} and return them in array form.
     *
     * @return All input parameters of the entry point method.
     */
    public Object[] getJoinPointAllArgs() {
        JoinPoint joinPoint = currentJoinPoint();
        if (joinPoint == null) {
            return empty_array;
        }
        return joinPoint.getArgs();
    }

    /**
     * Returns the parameter information of the specified index position of
     * the pointcut method, and converts it to the desired type.
     *
     * <p>The position of the parameter index needs to be estimated within the
     * length range of the specified method parameter list, otherwise an error
     * {@link IndexOutOfBoundsException} will be thrown, and the conversion type
     * parameter must also be specified.
     *
     * @param index        Specify the parameters for the location.
     * @param requiredType The class object of the type that needs to be converted.
     * @param <T>          The type that needs to be converted.
     * @return Specify the index position parameter and convert it to the specified type.
     */
    public <T> T getJoinPointAppointTypeArg(int index, Class<T> requiredType) {
        Object[] args = getJoinPointAllArgs();
        if (Arrays.equals(empty_array, args) || ArrayUtils.isEmpty(args)) {
            return null;
        }
        if (index >= args.length) {
            throw new IndexOutOfBoundsException(
                    "JoinPoint args length " + args.length + ", but need index " + index);
        }
        return ConvertUtils.convert(requiredType, args[index]);
    }
}
