package top.osjf.assembly.simplified.aop;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Temporarily store {@link MethodInvocation} during method interceptor
 * execution for elegant retrieval during method execution.
 * <p>Clear the {@link MethodInvocation} of the current thread at
 * the end of method execution or when an exception occurs.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.0.7
 */
public final class InvocationContext {

    private static final ThreadLocal<MethodInvocation> invocation_local = new ThreadLocal<>();

    private InvocationContext() {
    }

    static void setCurrentInvocation(MethodInvocation invocation) {
        if (invocation == null) {
            invocation_local.remove();
        } else {
            invocation_local.set(invocation);
        }
    }

    public static MethodInvocation currentInvocation() {
        return invocation_local.get();
    }
}
