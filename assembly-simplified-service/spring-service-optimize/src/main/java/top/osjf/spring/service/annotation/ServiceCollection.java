package top.osjf.spring.service.annotation;

import java.lang.annotation.*;

/**
 * The annotation that needs to be annotated for service interfaces/abstract classes
 * is mainly used to tell you that once the class is annotated, its implementation
 * class or subclass can be automatically collected, provided that it has been added
 * to the Spring container and can be collected.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceCollection {

    /**
     * @return Service name prefix,the default is {@link Class#getName()}.
     */
    String prefix() default "";
}
