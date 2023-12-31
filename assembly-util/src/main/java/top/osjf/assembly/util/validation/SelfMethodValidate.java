package top.osjf.assembly.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The automatic verification of one's own method is carried out by the colleague
 * wearing this annotation to implement {@link MethodValidate}, rewrite its method,
 * call it automatically at runtime, and throw the specified error.
 * <p>The usage notes are as follows:
 * <div><h3>Pure annotation, using reflection.</h3></div>
 * <pre>
 *     &#064;SelfMethodValidate
 *     public class SelfCheck {
 *
 *      private Object id;
 *
 *      public Supplier&lt;Boolean&gt; validate(){
 *          return ()-&gt; id != null;
 *      }
 *
 *      public String message(){
 *          return "id not be null";
 *      }
 *     }
 * </pre>
 * <div><h3>Implement the specified verification interface and the specified error dependency interface.</h3></div>
 * <pre>
 *     &#064;SelfMethodValidate
 *     public class SelfCheck implements MethodValidate, Error {
 *
 *      private Object id;
 *
 *      &#064;Override
 *      public Supplier&lt;Boolean&gt; validate(){
 *          return ()-&gt; id != null;
 *      }
 *
 *      &#064;Override
 *      public String message(){
 *          return "id not be null";
 *      }
 *     }
 * </pre>
 * <div><h3>Implement the specified validation interface and specify the error dependency class.</h3></div>
 * <pre>
 *     &#064;SelfMethodValidate(errorReply = Example.class)
 *     public class SelfCheck implements MethodValidate {
 *
 *      private Object id;
 *
 *      &#064;Override
 *      public Supplier&lt;Boolean&gt; validate(){
 *          return ()-&gt; id != null;
 *      }
 *
 *      public static class Example implements Error {
 *
 *          &#064;Override
 *          public String message(){
 *              return "id not be null";
 *          }
 *      }
 *     }
 * </pre>
 * @see MethodValidateConstraintValidator
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
     * <p>Its priority program is lower than {@link #message()}.
     * <p>1.0.5/update</p>
     * <p>When the default is {@code Error.class}, prioritize whether
     * the checksum implements {@link Error} and rewrite its method.
     * {@code MethodValidateConstraintValidator#ifErrorClassDefault(Object)}
     * <p>If there is no or the provided information is empty, consider
     * using the reflection query method named {@link Error#name}.
     * {@code MethodValidateConstraintValidator#instantiateReplyMessage(Class)}
     * <p>If none of the above are available, use the default information
     * {@link Error#DEFAULT}.
     * <p>Of course, if a dependency class is provided, it will be
     * instantiated, and you need to ensure that the information for
     * {@link Error#message()} can be obtained.
     * @return Class Object within {@link Error}.
     */
    Class<? extends Error> errorReply() default Error.class;

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
