package top.osjf.assembly.simplified.aop.step;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import top.osjf.assembly.simplified.aop.step.annotation.StepInterceptor;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Aspectj support for method level support step {@link Step}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see Before
 * @see AfterReturning
 * @see AfterThrowing
 * @see After
 * @since 2.2.5
 */
@Aspect
public class AspectJStepSupport {

    @Pointcut("@annotation(top.osjf.assembly.simplified.aop.step.annotation.StepInterceptor)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void beforeExecute(JoinPoint joinPoint) {

        // ———————————————— Execute first ——————————————————————

        execute(joinPoint, Step::before);
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void afterExecuteReturn(JoinPoint joinPoint, Object result) {

        // ———————————————— Execute after returning ——————————————————————

        execute(joinPoint, step -> step.afterReturn(result));
    }

    @AfterThrowing(value = "pointCut()", throwing = "e")
    public void afterExecuteThrow(JoinPoint joinPoint, Throwable e) {

        // ———————————————— Execute after exception ——————————————————————

        execute(joinPoint, step -> step.afterThrow(e));
    }


    @After("pointCut()")
    public void afterExecute(JoinPoint joinPoint) {

        // ———————————————— Final Execution ——————————————————————

        execute(joinPoint, Step::after);
    }

    /**
     * According to the value of {@link StepSignature}, it is a collection
     * of objects that need to be executed about {@link Step}.
     *
     * @param joinPoint Method entry point.
     * @param execute   Stage execution method.
     * @see top.osjf.assembly.simplified.aop.step.annotation.StepInterceptor
     */
    private void execute(JoinPoint joinPoint, Consumer<Step> execute) {
        //Step interception annotations exist in methods.
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //Get the entry point method.
        Method method = signature.getMethod();
        StepInterceptor interceptor = method.getAnnotation(StepInterceptor.class);
        //Divide the execution according to the steps of intercepting annotations to execute the target.
        //The initialization length is the parameter length plus the target object.
        List<Object> bodies = new ArrayList<>(joinPoint.getArgs() != null ? joinPoint.getArgs().length + 1 : 1);
        switch (interceptor.value()) {
            case ARG:
                addStepWithArg(bodies, joinPoint);
                break;
            case TARGET:
                addStepWithTarget(bodies, joinPoint);
                break;
            case TOGETHER:
                addStepTogether(bodies, joinPoint);
                break;
            default:
                break;
        }
        if (CollectionUtils.isNotEmpty(bodies)) {
            bodies.forEach(o -> {
                if (o instanceof Step) execute.accept((Step) o);
            });
        }
    }

    private void addStepWithArg(List<Object> bodies, JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isNotEmpty(args)) bodies.addAll(Arrays.asList(args));
    }

    private void addStepWithTarget(List<Object> bodies, JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        if (target != null) bodies.add(target);
    }

    private void addStepTogether(List<Object> bodies, JoinPoint joinPoint) {
        addStepWithArg(bodies, joinPoint);
        addStepWithTarget(bodies, joinPoint);
    }
}
