package top.osjf.sdk.core.support;

import java.lang.annotation.*;

/**
 * The sorting annotation of loading order is mainly for the active
 * loading order of classes.
 *
 * <p>Sort from small to large based on the size of the provided
 * {@code value} value, with the smallest order value {@code value}
 * being the highest priority sorting.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadOrder {

    /**
     * The order value.
     * <p>Default is {@link Integer#MAX_VALUE}.
     */
    int value() default Integer.MAX_VALUE;
}
