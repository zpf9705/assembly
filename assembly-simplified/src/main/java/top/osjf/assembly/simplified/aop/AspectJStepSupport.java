package top.osjf.assembly.simplified.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * Aspectj support for method level support step {@link Step}.
 *
 * @see Before
 * @see AfterReturning
 * @see AfterThrowing
 * @see After
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Aspect
public class AspectJStepSupport {

    @Pointcut("@annotation(StepInterceptor)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void beforeExecute(JoinPoint joinPoint) {

        // ———————————————— Execute first ——————————————————————

        Object target = joinPoint.getTarget();

        if (target instanceof Step) {
            ((Step) target).before();
        }
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void afterExecuteReturn(JoinPoint joinPoint, Object result) {

        // ———————————————— Execute after returning ——————————————————————

        Object target = joinPoint.getTarget();

        if (target instanceof Step) {
            ((Step) target).afterReturn(result);
        }
    }

    @AfterThrowing(value = "pointCut()", throwing = "e")
    public void afterExecuteThrow(JoinPoint joinPoint, Throwable e) {

        // ———————————————— Execute after exception ——————————————————————

        Object target = joinPoint.getTarget();

        if (target instanceof Step) {
            ((Step) target).afterThrow(e);
        }
    }


    @After("pointCut()")
    public void afterExecute(JoinPoint joinPoint) {

        // ———————————————— Final Execution ——————————————————————

        Object target = joinPoint.getTarget();

        if (target instanceof Step) {
            ((Step) target).after();
        }
    }
}
