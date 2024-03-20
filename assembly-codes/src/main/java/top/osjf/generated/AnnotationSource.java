package top.osjf.generated;

import java.lang.annotation.Documented;

/**
 * Generate property settings for class holding annotations, set the
 * fully qualified name of the class holding annotations, and only
 * support inputs with {@link #SUPPORT_KEY} attributes.
 * <p>Only as a dependency attribute for other execution annotations,
 * without special parsing.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
@Documented
public @interface AnnotationSource {

    /** only support set annotation "value" name*/
    String SUPPORT_KEY = "value";

    /**
     * Return the fully qualified name of the generated class that holds the annotation.
     * @see Class#getName()
     * @return the fully qualified name of the generated class that holds the annotation.
     */
    String name();

    /**
     * Returns a set value that only supports holding annotations with {@link #SUPPORT_KEY}
     * attributes, and is of string type.
     * @return a set value that only supports holding annotations with {@link #SUPPORT_KEY}
     *          attributes.
     */
    String valueSet() default "";
}
