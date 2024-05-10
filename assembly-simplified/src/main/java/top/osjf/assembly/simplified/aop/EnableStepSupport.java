package top.osjf.assembly.simplified.aop;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Comment on the switch for the callback in the expense step stage.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(StepSupportConfiguration.class)
public @interface EnableStepSupport {
}
