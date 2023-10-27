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
 * <p>The usage notes are as follows:
 * <div><h3>Use the method in the calibration body.</h3></div>
 * <pre>
 *     &#064;SelfMethodValidate(selfCheckMethod = "validate", selfCheckFailedReply = "message")
 *     &#064;SelfMethodValidate(selfCheckMethod = "validate0", selfCheckFailedReply = "message0")
 *     public class SelfCheck {
 *
 *      private Object id;
 *
 *      private Object num;
 *
 *      public Supplier&lt;Boolean&gt; validate(){
 *          return ()-&gt; id != null;
 *      }
 *
 *      public String message(){
 *          return "id not be null";
 *      }
 *
 *      public Supplier&lt;Boolean&gt; validate0(){
 *          return ()-&gt; num != null;
 *      }
 *
 *      public String message0(){
 *          return "num not be null";
 *      }
 *     }
 * </pre>
 * <div><h3>The failure information is obtained using the attachment class outside the verification body.</h3></div>
 * <pre>
 *     &#064;SelfMethodValidate(selfCheckMethod = "validate", selfCheckFailedReply = "top.osjf.example.Example")
 *     &#064;SelfMethodValidate(selfCheckMethod = "validate0", selfCheckFailedReply = "top.osjf.example.Example0")
 *     public class SelfCheck {
 *
 *      private Object id;
 *
 *      private Object num;
 *
 *      public Supplier&lt;Boolean&gt; validate(){
 *          return ()-&gt; id != null;
 *      }
 *
 *      public Supplier&lt;Boolean&gt; validate0(){
 *          return ()-&gt; num != null;
 *      }
 *     }
 *
 *      public static class Example implements Error {
 *
 *          &#064;Override
 *          public String message(){
 *              return "id not be null";
 *          }
 *      }
 *
 *      public static class Example0 implements Error {
 *
 *          &#064;Override
 *          public String message(){
 *              return "num not be null";
 *          }
 *      }
 * </pre>
 * @see MethodValidateAnyConstraintValidator
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MethodValidateAnyConstraintValidator.class)
@Repeatable(value = SelfMethodValidateAny.List.class)
public @interface SelfMethodValidateAny {

    /**
     * To support multiple validations, it is necessary to specify the
     * name of the validation method and the format of the return value as
     * <pre>{@code Supplier<Boolean>}.</pre>
     * @return Cannot be empty, item must be provided.
     */
    String selfCheckMethod();

    /**
     * Provide error information acquisition after verification failure
     * for the above method {@link #selfCheckMethod()}.
     * <p>You can provide the following items:
     * <ul>
     *     <li>The method name in this class.
     *     <p>Invoke through reflection if necessary.
     *     <p>Prioritize first.</li>
     *     <li>The full path of the class.Implement {@link Error}.
     *     <p>when needed and can be instantiated.
     *     <p>Priority second consideration.</li>
     * </ul>
     * <p>Only when this is not provided will {@link #message()} be used.
     * @return As mentioned above, if the parameter is empty,
     * use {@link #message()}.
     */
    String selfCheckFailedReply() default "";

    String message() default "An unknown error occurred during model validation.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @SelfMethodValidateAny} constraints on the same element.
     * @see SelfMethodValidateAny
     */
    @Target(ElementType.TYPE)
    @Retention(RUNTIME)
    @Documented
    @interface List {
        SelfMethodValidateAny[] value();
    }
}
