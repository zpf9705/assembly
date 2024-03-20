package top.osjf.generated;

import java.lang.annotation.Documented;

/**
 * Set the interface of the inherited class or implementation corresponding
 * to the generated class, as well as the generic class carried.
 * <p>Only as a dependency attribute for other execution annotations,
 * without special parsing.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
@Documented
public @interface ClassSource {

    /**
     * Return the fully qualified name of the generated class that holds the class.
     * @see Class#getName()
     * @return the fully qualified name of the generated class that holds the class.
     */
    String name();

    /**
     * Return the fully qualified name of the generated class that holds the
     * generics classes.
     * @see Class#getName()
     * @return the fully qualified name of the generated class that holds the
     *         generics classes.
     */
    String[] genericsNames() default {};
}
