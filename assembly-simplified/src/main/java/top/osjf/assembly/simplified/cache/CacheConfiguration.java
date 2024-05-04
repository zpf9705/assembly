package top.osjf.assembly.simplified.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import top.osjf.assembly.cache.operations.ValueOperations;
import top.osjf.assembly.simplified.cache.aop.CacheAspectJSupport;
import top.osjf.assembly.simplified.cache.sql.AroundSQLExecuteInterceptor;
import top.osjf.assembly.simplified.cache.sql.CacheDruidFilterEvent;

/**
 * Based on the cache configuration item enabled by {@link EnableCache}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy(exposeProxy = true)
public class CacheConfiguration {

    @Bean
    public CacheAspectJSupport cacheAspectJSupport(ValueOperations<String, Object> valueOperations) {
        return new CacheAspectJSupport(valueOperations);
    }


    //—————————————————————————————————— For example, using SQL for cache change configuration.

    @Bean
    public CacheDruidFilterEvent cacheDruidFilterEvent() {
        return new CacheDruidFilterEvent();
    }

    @Bean
    public AroundSQLExecuteInterceptor aroundSQLExecuteInterceptor(){
        return new AroundSQLExecuteInterceptor();
    }
}
