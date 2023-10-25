package top.osjf.assembly.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The automatic verification of one's own method is carried out by the colleague
 * wearing this annotation to implement {@link MethodValidate}, rewrite its method,
 * call it automatically at runtime, and throw the specified error.
 * <p>
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MethodValidateConstraintValidator.class)
public @interface SelfMethodValidate {

    /**
     * When the self checking method of an object returns false,
     * it is necessary to implement {@link Error} to obtain
     * the dependency information of the object.
     * Its priority program is lower than {@link #message()}.
     * @return Class Object within {@link Error}.
     */
    Class<? extends Error> errorReply() default Error.class;

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
