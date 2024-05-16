package top.osjf.assembly.simplified.aop.step;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;

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

    @NotNull
    private final StepSignature signature;

    public AspectJStepSupport(@NotNull StepSignature signature) {
        this.signature = signature;
    }

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
     * According to the value of {@link #signature}, it is a collection
     * of objects that need to be executed about {@link Step}.
     *
     * @param joinPoint Method entry point.
     * @param execute   Stage execution method.
     */
    private void execute(JoinPoint joinPoint, Consumer<Step> execute) {
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        List<Object> bodies = new ArrayList<>(args.length + 1);
        switch (signature) {
            case ARG:
                if (ArrayUtils.isNotEmpty(args)) bodies.addAll(Arrays.asList(args));
                break;
            case TARGET:
                if (target != null) bodies.add(target);
                break;
            case TOGETHER:
                if (ArrayUtils.isNotEmpty(args)) bodies.addAll(Arrays.asList(args));
                if (target != null) bodies.add(target);
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
}
