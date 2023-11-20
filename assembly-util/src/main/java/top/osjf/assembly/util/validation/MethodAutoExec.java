package top.osjf.assembly.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The behavior annotation of method automatic execution provides the execution
 * and error verification of model's own behavior method, without verifying the
 * specific return value type.
 * <p>It is limited to intercepting error information of method statements or manually
 * throwing information within the method, and can be used for parameter verification
 * or direct operation of the method.
 * Compared to {@link SelfMethodValidate} and {@link SelfMethodValidateAny}, it does
 * not require a return value and is more free, but has slightly different purposes.
 * @see SelfMethodValidate
 * @see SelfMethodValidateAny
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MethodAutomaticActuator.class)
@Documented
public @interface MethodAutoExec {

    /**
     * The array set of method names that need to be automatically
     * executed, which is equivalent to the {@link Runnable} annotated
     * method.
     * <p>The executor will filter the methods under both conditions
     * to automatically execute.
     * @see Runnable
     * @return Array of method names.
     */
    String[] value() default {};

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
