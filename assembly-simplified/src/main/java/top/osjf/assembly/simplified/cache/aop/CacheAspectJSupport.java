package top.osjf.assembly.simplified.cache.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import top.osjf.assembly.cache.operations.ValueOperations;
import top.osjf.assembly.simplified.cache.*;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.ConvertUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A unified processing class based on the spring aop aspectj
 * framework for cache settings and timely updates, specifically
 * handling the cache settings of {@link Cacheable} and its parsing
 * {@link CacheUpdate}based on key value cache changes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
@Aspect
public class CacheAspectJSupport implements ApplicationContextAware {

    private final ValueOperations<String, Object> valueOperations;

    private ApplicationContext applicationContext;

    public CacheAspectJSupport(ValueOperations<String, Object> valueOperations) {
        this.valueOperations = valueOperations;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Around("@annotation(top.osjf.assembly.simplified.cache.Cacheable)")
    public Object aroundForExecuteCache(ProceedingJoinPoint pjp) throws Throwable {

        //————————————————————— Retrieve cache annotation information from the method first.

        Method method = getMethodByJoinPoint(pjp);

        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        //Build key information based on important values and parameter hash values.
        CacheObj probingCacheObj
                = new DefaultCacheObj(getProxyKey(cacheable.value()), pjp.getArgs());

        String key = probingCacheObj.getCacheKey();

        //————————————————————— If cache is found, return directly.

        CacheObj cacheObj = getCacheObj(key);

        if (cacheObj != null) return cacheObj.getCacheContent();

        //————————————————————— If there is no cache, execute the ontology operation.

        Object result = pjp.proceed();

        probingCacheObj.setCacheContent(result);

        probingCacheObj.setReCacheMethod(method.getName());

        String currentProxyBeanName = getCurrentProxyBeanName();

        probingCacheObj.setReCacheProxyObjName(currentProxyBeanName);

        probingCacheObj.setCacheDuration(cacheable.cacheDuration());

        probingCacheObj.setCacheTimeUnit(cacheable.cacheTimeUnit());

        //————————————————————— Put in cache before returning.

        valueOperations.set(key, probingCacheObj, cacheable.cacheDuration(), cacheable.cacheTimeUnit());

        return result;
    }


    /**
     * Relying on enabling {@link EnableAspectJAutoProxy#exposeProxy()} to obtain
     * the name of its bean based on the proxy object.
     *
     * @return The bean name of the proxy object.
     */
    private String getCurrentProxyBeanName() {
        Object o = AopContext.currentProxy();
        for (String name : applicationContext.getBeanDefinitionNames()) {
            if (applicationContext.getBean(name) == o) {
                return name;
            }
        }
        //If no bean name is found, retrieve its true class object and provide a prompt.
        throw new NoSuchBeanDefinitionException(AopProxyUtils.ultimateTargetClass(o));
    }

    /**
     * Return the cache object {@link CacheObj} corresponding to the provided key value.
     *
     * @param key Cached flags.
     * @return the cache object {@link CacheObj} corresponding to the provided key value.
     */
    @CanNull
    private CacheObj getCacheObj(String key) {
        return ConvertUtils.convert(CacheObj.class, valueOperations.get(key));
    }

    /**
     * Returns the method parameters corresponding to the entry point.
     *
     * @param jp Entry point parameters.
     * @return the method parameters corresponding to the entry point.
     */
    private Method getMethodByJoinPoint(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        return signature.getMethod();
    }

    /**
     * Returns the key value processed by the agent.
     *
     * @param key {@link Cacheable}'s value.
     * @return the key value processed by the agent.
     */
    public String getProxyKey(String key) {
        Object obj = AopContext.currentProxy();
        Class<?> targetType = AopProxyUtils.ultimateTargetClass(obj);
        return key + "@" + targetType.getName();
    }
    //———————————————————————————————————————— Cache refresh after execution

    @AfterReturning(value = "@annotation(top.osjf.assembly.simplified.cache.CacheUpdate)", returning = "result")
    public void afterExecutedToCacheUpdate(@SuppressWarnings("unused") JoinPoint joinPoint,
                                           @SuppressWarnings("unused") Object result) {

        //Monitor and clear cache information in a timely manner.
        List<Exchange> exchanges = CacheContextSupport.currentPurgeExchanges();
        if (CollectionUtils.isEmpty(exchanges)) {
            return;
        }

        //Enable cache thread processing.
        new Thread(() -> {
            for (Exchange exchange : exchanges) {
                if (!exchange.result()) {
                    continue;
                }
                String proxyKey = getProxyKey(exchange.getValue());
                //Find similar key cache clearing based on important value values.
                List<String> similarKeys = valueOperations.getSimilarKeys(proxyKey);
                if (CollectionUtils.isNotEmpty(similarKeys)) {
                    for (String key : similarKeys) {
                        CacheObj cacheObj = getCacheObj(key);
                        if (cacheObj == null) continue;
                        valueOperations.getCommonsOperations().delete(key);
                        cacheObj.reCache(applicationContext);

                    }
                }
            }
        }).start();

        //Clear cached information in this thread.
        CacheContextSupport.removeCurrentExchanges();

    }
}
