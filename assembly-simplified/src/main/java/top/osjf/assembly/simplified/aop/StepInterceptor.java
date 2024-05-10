package top.osjf.assembly.simplified.aop;

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
}
