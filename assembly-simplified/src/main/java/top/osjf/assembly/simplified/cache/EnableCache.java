package top.osjf.assembly.simplified.cache;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Open the development annotation for cache configuration,
 * use this annotation to load {@link CacheConfiguration},
 * and provide automatic assembly for cache setting and refreshing.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Import(CacheConfiguration.class)
@Documented
public @interface EnableCache {
}
