package top.osjf.assembly.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Another verification method different from {@link SelfMethodValidate} annotation.
 * <p>This annotation needs to directly provide the name of the validation method,
 * specify the parameter types that must be returned, and perform validation through
 * reflection, which requires you to fill in the correct method name.
 * <p>Provided method level information acquisition that takes precedence over
 * {@link #message()} throwing, you can provide a correct method name and return
 * value method, which can be obtained through reflection calls.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MethodValidateConstraintValidator2.class)
@Repeatable(value = SelfMethodValidate2.List.class)
public @interface SelfMethodValidate2 {

    /**
     * To support multiple validations, it is necessary to specify the
     * name of the validation method and the format of the return value as
     * <pre>{@code Supplier<Boolean>}.</pre>
     * @return Cannot be empty, item must be provided.
     */
    String method();

    /**
     * Provide the method {@link #method()} above, and when
     * there is no result returned from {@literal true}, the method that
     * obtains the thrown information has a high priority than {@link #message()}.
     * @return The name of the method for obtaining failure information.
     */
    String errorMethod() default "";

    /**
     * If not provided {@link #errorMethod()}, default information
     * will be used.
     * <p>Of course, if you edit it well, your failed statements will
     * still be prioritized.
     * @return failed msg.
     */
    String message() default "An unknown error occurred during model validation.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @SelfMethodValidate2} constraints on the same element.
     * @see SelfMethodValidate2
     */
    @Target(ElementType.TYPE)
    @Retention(RUNTIME)
    @Documented
    @interface List {
        SelfMethodValidate2[] value();
    }
}
