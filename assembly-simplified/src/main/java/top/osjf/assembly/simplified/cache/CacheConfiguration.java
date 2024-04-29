package top.osjf.assembly.simplified.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import top.osjf.assembly.cache.factory.CacheFactory;
import top.osjf.assembly.cache.operations.CacheTemplate;
import top.osjf.assembly.cache.operations.ValueOperations;
import top.osjf.assembly.cache.serializer.SerializerAdapter;
import top.osjf.assembly.cache.serializer.StringPairSerializer;
import top.osjf.assembly.simplified.cache.aop.CacheAspectJSupport;
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
    public CacheDruidFilterEvent cacheDruidFilterEvent() {
        return new CacheDruidFilterEvent();
    }

    @Bean
    @ConditionalOnBean(CacheFactory.class)
    public CacheTemplate<String, CacheObj> cacheObjCacheTemplate(CacheFactory cacheFactory) {
        return new CacheTemplate<>(cacheFactory, new StringPairSerializer(),
                new SerializerAdapter<>(CacheObj.class));
    }

    @Bean
    public ValueOperations<String, CacheObj> cacheObjValueOperations(CacheTemplate<String, CacheObj>
                                                                             cacheObjCacheTemplate) {
        return cacheObjCacheTemplate.opsForValue();
    }

    @Bean
    public CacheAspectJSupport cacheAspectJSupport(ValueOperations<String, CacheObj> valueOperations) {
        return new CacheAspectJSupport(valueOperations);
    }
}
