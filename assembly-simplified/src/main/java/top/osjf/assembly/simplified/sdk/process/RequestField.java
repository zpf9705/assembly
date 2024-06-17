package top.osjf.assembly.simplified.sdk.process;

import java.lang.annotation.*;

/**
 * When assigning values to request class fields, if the compiled
 * parameters no longer have the original parameter names, use this
 * annotation to mark the true information of the current request
 * class field.
 *
 * <p>For specific information on related properties, you can query
 * the relevant properties of this annotation.
 *
 * <p>For specific usage, please refer to {@link RequestParam}.
 *
 * <p>Regarding the proxy analysis of SDK to implement the parsing of
 * this annotation, please refer to {@code top.osjf.assembly.simplified.sdk.
 * SdkUtils#invokeCreateRequestUseSet(Class, Object...)}
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.6
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestField {

    /**
     * The real name of the marked field.
     *
     * <p>When using reflection assignment, this
     * property is not mandatory.
     *
     * @return real name of the marked field.
     */
    String value() default "";

    /**
     * The true ranking of this parameter in the proxy method.
     *
     * <p>If not specified, the selection will be made in the
     * default order.
     *
     * @return ranking of this parameter in the proxy method.
     */
    int order() default -1;

    /**
     * Whether to directly use reflection to assign values to fields.
     * <p>If {@code true}, then the real name of the field {@link #value()}
     * is no longer required.
     *
     * @return result of use reflection to assign values to fields.
     */
    boolean useReflect() default false;
}
