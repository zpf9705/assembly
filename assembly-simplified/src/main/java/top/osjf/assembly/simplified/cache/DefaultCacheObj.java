package top.osjf.assembly.simplified.cache;

import org.springframework.context.ApplicationContext;
import top.osjf.assembly.util.lang.ReflectUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Default Impl for {@link CacheObj}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public class DefaultCacheObj implements CacheObj {

    private static final long serialVersionUID = -5802691745617857758L;

    private final String value;

    private final Object[] makeCacheParams;

    private Object cacheContent;

    private long cacheDuration;

    private TimeUnit cacheTimeUnit;

    private String reCacheProxyObjName;

    private String reCacheMethod;

    public DefaultCacheObj(String value, Object[] makeCacheParams) {
        this.value = value;
        this.makeCacheParams = makeCacheParams;
    }

    @Override
    public void setCacheContent(Object cacheContent) {
        this.cacheContent = cacheContent;
    }

    @Override
    public void setCacheDuration(long cacheDuration) {
        this.cacheDuration = cacheDuration;
    }

    @Override
    public void setCacheTimeUnit(TimeUnit cacheTimeUnit) {
        this.cacheTimeUnit = cacheTimeUnit;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Object[] getMakeCacheParams() {
        return makeCacheParams;
    }

    @Override
    public long getCacheDuration() {
        return cacheDuration;
    }

    @Override
    public TimeUnit getCacheTimeUnit() {
        return cacheTimeUnit;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCacheContent() {
        return (T) cacheContent;
    }

    @Override
    public void setReCacheProxyObjName(String reCacheProxyObjName) {
        this.reCacheProxyObjName = reCacheProxyObjName;
    }

    @Override
    public void setReCacheMethod(String reCacheMethod) {
        this.reCacheMethod = reCacheMethod;
    }

    @Override
    public void reCache(ApplicationContext applicationContext) {
        try {
            cacheContent =
                    ReflectUtils.invoke(applicationContext.getBean(reCacheProxyObjName), reCacheMethod, makeCacheParams);
        } catch (Throwable e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultCacheObj that = (DefaultCacheObj) o;
        return cacheDuration == that.cacheDuration
                && Objects.equals(value, that.value)
                && Arrays.equals(makeCacheParams, that.makeCacheParams)
                && Objects.equals(cacheContent, that.cacheContent)
                && cacheTimeUnit == that.cacheTimeUnit
                && Objects.equals(reCacheProxyObjName, that.reCacheProxyObjName)
                && Objects.equals(reCacheMethod, that.reCacheMethod);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(value, cacheContent, cacheDuration, cacheTimeUnit,
                reCacheProxyObjName, reCacheMethod);
        result = 31 * result + Arrays.hashCode(makeCacheParams);
        return result;
    }

    @Override
    public String toString() {
        return getCacheKey();
    }
}
