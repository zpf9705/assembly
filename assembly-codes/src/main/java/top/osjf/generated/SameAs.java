package top.osjf.generated;

import java.lang.annotation.*;

/**
 * Equivalent annotation, marked on the annotation method,
 * only indicates that the meaning expressed is consistent
 * with a certain annotation, and does not have a special
 * analytical meaning.
 *
 * <p>If there is no explanation for the annotation method of a
 * staple food, you can go to {@link #value()} to query. If the
 * annotation explanation is consistent, the annotation can be omitted.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Documented
public @interface SameAs {

    /**
     * Returns Annotations and annotation classes with meanings equal to.
     * @return annotations and annotation classes with meanings equal to.
     */
    Class<? extends Annotation> value() default Annotation.class;
}
