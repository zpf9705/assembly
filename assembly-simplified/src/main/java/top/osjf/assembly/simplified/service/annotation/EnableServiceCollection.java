package top.osjf.assembly.simplified.service.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.assembly.simplified.service.ServiceContextConfiguration;

import java.lang.annotation.*;

/**
 * Service collection configuration enables annotations.
 *
 * <p>Developed based on Spring, it is mainly aimed at implementing the injection collection
 * problem of multiple implementation classes for a single interface.
 *
 * <p>This annotation does not have relevant parameter attributes.
 *
 * <p>For the scanning analysis of {@link ServiceCollection} wearable classes, the package
 * path where the startup class is located is used, and path parameters are not provided
 * for manual writing. Before use, your corresponding class needs to be placed under the
 * package where the startup class is located.
 *
 * @author zpf
 * @since 2.0.4
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ServiceContextConfiguration.class)
public @interface EnableServiceCollection {
}
