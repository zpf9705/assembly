package top.osjf.assembly.simplified.cache;

import top.osjf.assembly.simplified.cache.sql.CacheDruidFilterEvent;

import java.lang.annotation.*;

/**
 * The flag annotation for cache updates identifies the method used
 * to capture cache changes of specific values, depending on how you
 * develop a cache change strategy.
 *
 * <p>For example, this article provides a notification class
 * {@link CacheDruidFilterEvent} for cache changes caused by SQL updates,
 * deletions, and additions. For this annotation, it is a record of changes
 * to indicate timely cache changes.
 *
 * <p>You need to mark it on the method generated relative to the cache.
 *
 * @see Cacheable
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheUpdate {
}
