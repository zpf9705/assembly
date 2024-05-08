package top.osjf.assembly.simplified.init;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Objects;

/**
 * Implement support classes for pre -, post -, and method
 * initialization markers using Aspectj.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
@Aspect
public class AspectJInitSupport {


    /*
     * The centralized plan for the target object.
     * */
    static class TargetObjHolder {

        /*
         * Retrieve the original object based on the entry point.
         * */
        @SuppressWarnings("unchecked")
        static <T> T getTargetObj(JoinPoint point) throws ClassCastException {
            return (T) point.getTarget();
        }

        /*
         * Is it allowed to be executed.
         * */
        static boolean allow(JoinPoint point) {
            return TargetObjHolder.<Init>getTargetObj(point).canInit()
                    &&
                    Objects.equals(point.getSignature().getName(), Init.MAIN_METHOD_NAME);
        }

        /*
         * Execute the initialization phase method when allowed to execute.
         * */
        static void execute(JoinPoint point, Runnable r) {
            if (allow(point)) r.run();
        }
    }

    /*
     * The execution method used to match the current AOP proxy object type;
     * Note that the type matching of AOP proxy objects may include type
     * matching when introducing interfaces;
     * */
    @Pointcut("this(top.osjf.assembly.simplified.init.InitAble)")
    public void pointCut() {
    }

    /*
     * Intercept and execute before executing the initialization method.
     * */
    @Before("pointCut()")
    public void before(JoinPoint point) {
        TargetObjHolder.execute(point, () -> TargetObjHolder.<InitBefore>getTargetObj(point).initBefore());
    }

    /*
     * Mark initialization when the execution of the initialization method returns.
     * */
    @AfterReturning("pointCut()")
    public void afterReturn(JoinPoint point) {
        TargetObjHolder.execute(point, () -> TargetObjHolder.<ActionInited>getTargetObj(point).actionInited());
    }

    /*
     * Intercept execution after the execution of the initialization method returns.
     * */
    @After("pointCut()")
    public void after(JoinPoint point) {
        TargetObjHolder.execute(point, () -> TargetObjHolder.<InitAfter>getTargetObj(point).initAfter());
    }
}
