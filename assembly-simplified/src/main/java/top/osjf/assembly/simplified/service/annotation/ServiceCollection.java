package top.osjf.assembly.simplified.service.annotation;

import java.lang.annotation.*;

/**
 * The annotation that needs to be annotated for service interfaces/abstract classes
 * is mainly used to tell you that once the class is annotated, its implementation
 * class or subclass can be automatically collected, provided that it has been added
 * to the Spring container and can be collected.
 *
 * <p>For specific collection methods, please refer to the implementation class
 * {@link top.osjf.assembly.simplified.service.context.ClassesServiceContext}.</p>
 *
 * @author zpf
 * @since 2.0.4
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceCollection {

    /**
     * @return Service name prefix,the default is {@link Class#getName()}.
     * @since 2.0.6
     */
    String prefix() default "";
}
