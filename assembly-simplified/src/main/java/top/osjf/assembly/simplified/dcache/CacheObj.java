package top.osjf.assembly.simplified.dcache;

import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public interface CacheObj extends Serializable {

    String getValue();

    Object[] getMakeCacheParams();

    default String getCacheKey() {
        return getValue() + "@" + Arrays.hashCode(getMakeCacheParams());
    }

    void setCacheDuration(long cacheDuration);

    long getCacheDuration();

    void setCacheTimeUnit(TimeUnit cacheTimeUnit);

    TimeUnit getCacheTimeUnit();

    void setCacheContent(Object cacheContent);

    <T> T getCacheContent();

    void setReCacheProxyObjName(String reCacheProxyObjName);

    void setReCacheMethod(String reCacheMethod);

    void reCache(ApplicationContext context);
}
