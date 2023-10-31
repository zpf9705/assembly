package top.osjf.assembly.simplified.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * A simple implementation class for {@link MethodInterceptor}, mainly used
 * to pass {@link MethodInvocation} parameters before and after method execution
 * into the current thread, and obtain them through {@link InvocationContext}
 * operations, simplifying the operation of AOP.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.0.7
 */
public class MethodInterceptorImpl implements MethodInterceptor {

    @CanNull
    @Override
    public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
        InvocationContext.setCurrentInvocation(invocation);
        Object proceed;
        try {
            proceed = invocation.proceed();
        } finally {
            InvocationContext.setCurrentInvocation(null);
        }
        return proceed;
    }
}
