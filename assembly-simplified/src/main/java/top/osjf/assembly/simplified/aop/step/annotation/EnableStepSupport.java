package top.osjf.assembly.simplified.aop.step.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.assembly.simplified.aop.step.Step;

import java.lang.annotation.*;

/**
 * Comment on the switch for the callback in the expense step stage.
 *
 * <p>Using this annotation will be supported by an aspectj proxy,
 * responsible for callback methods at various stages of {@link Step}.
 *
 * @see StepSupportConfiguration
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(StepSupportConfiguration.class)
public @interface EnableStepSupport {
}
