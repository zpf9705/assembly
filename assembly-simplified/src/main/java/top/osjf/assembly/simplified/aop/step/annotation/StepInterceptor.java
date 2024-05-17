package top.osjf.assembly.simplified.aop.step.annotation;

import top.osjf.assembly.simplified.aop.step.AspectJStepSupport;
import top.osjf.assembly.simplified.aop.step.Step;
import top.osjf.assembly.simplified.aop.step.StepSignature;

import java.lang.annotation.*;

/**
 * The identification annotation of the execution step callback
 * interface {@link Step} is annotated at the method level.
 *
 * <p>This article provides support from spring aspectj to
 * intercept callbacks for methods.
 *
 * @see AspectJStepSupport
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StepInterceptor {

    /**
     * Confirm which type of object the current execution step
     * is running on, with default parameters and the actual
     * target object verified for execution.
     * @return Execution type of body detection.
     */
    StepSignature value() default StepSignature.TOGETHER;
}
