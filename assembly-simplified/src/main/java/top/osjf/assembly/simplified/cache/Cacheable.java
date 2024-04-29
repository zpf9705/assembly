package top.osjf.assembly.simplified.cache;

import top.osjf.assembly.simplified.cache.sql.CacheDruidFilterEvent;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * A annotation that can support caching method data in a specific
 * period of time. Parsing is supported by the aspectj framework
 * integrated by spring. It needs to provide a specific value for
 * subsequent changes in the cache mapping. It needs to take some
 * care to consider the definition reference of this value
 * {@link #value()}.
 *
 * @see CacheContextSupport
 * @see CacheUpdate
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {

    /**
     * The important prefix value of the cache flag key should be
     * used as a query comparison for similar keys during the later
     * cache refresh period.
     * <p>For example, in {@link CacheDruidFilterEvent}, this value
     * should be written as the specific name of the database table as
     * a reference for subsequent interception and recovery.
     *
     * @return Fill in the values according to the scene.
     */
    String value();

    /**
     * Returns the cache gap duration, which is a fixed cache duration.
     * <p>Use together with {@link #cacheTimeUnit()}.
     *
     * @return the cache gap duration，default to 30.
     */
    long cacheDuration() default 10;

    /**
     * Return the unit value of cache duration, refer to {@link TimeUnit}.
     * <p>Use together with {@link #cacheDuration()}.
     *
     * @return the unit value of cache duration.
     */
    TimeUnit cacheTimeUnit() default TimeUnit.SECONDS;
}
