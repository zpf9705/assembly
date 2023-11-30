package top.osjf.assembly.cache.autoconfigure;

import net.jodah.expiringmap.ExpiringMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.osjf.assembly.cache.config.expiringmap.ExpiringMapClients;
import top.osjf.assembly.cache.factory.CacheFactory;
import top.osjf.assembly.cache.factory.ExpiringMapCacheFactory;
import top.osjf.assembly.util.annotation.NotNull;

import java.io.PrintStream;

/**
 * One of the optional caches for this component {@link ExpiringMap}.
 * <p>
 * The following is an explanation of important parameters<br>
 * <h3>{@link CacheProperties#getExpiringMap()}.</h3><br>
 * {@code Max Size } : the maximum length of the map, add the 1001 th
 * entry, can lead to the first expired immediately (even if not to
 * expiration time).<br>
 * {@code Expiration }: expiration time and expired unit, set the expiration
 * time, is permanent.<br>
 * The use of expiration Policy: <h4>expiration policies.</h4><br>
 * {@code CREATED}: when each update element, the countdown reset date.<br>
 * {@code ACCESSED }: in every visit elements, the countdown reset date.<br>
 * {@code Variable Expiration }: allows you to update the Expiration time value,
 * if not set variable Expiration.<br>
 * Are not allowed to change the expiration time, once the executive change the
 * expiration time will throw an {@link UnsupportedOperationException}.<br>
 * <p>
 * Provide a configuration for you to load relevant build configurations related
 * to {@link ExpiringMap}, and select this cache configuration to load and use
 * in the main assembly class by default.
 *
 * @author zpf
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ExpiringMap.class})
@ConditionalOnProperty(
        name = "assembly.cache.client",
        havingValue = "expire_map",
        matchIfMissing = true)
public class ExpiringMapConfiguration extends CacheCommonsConfiguration implements CacheBannerDisplayDevice,
        EnvironmentAware {

    private Environment environment;

    public ExpiringMapConfiguration(CacheProperties properties) {
        super(properties);
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        /*
         * print expire - map version and banner info
         */
        StartUpBannerExecutor.printBanner(environment, getStartUpBanner(), sourceClass, out);
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    @NotNull
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    @NotNull
    public Class<?> getSourceClass() {
        return ExpiringMap.class;
    }

    @Override
    @NotNull
    public StartUpBanner getStartUpBanner() {
        return new ExpiringMapBanner();
    }

    @Bean
    @ConditionalOnMissingBean({CacheFactory.class})
    public CacheFactory expiringMapCacheExecutorFactory() {
        CacheProperties properties = getProperties();
        CacheProperties.ExpiringMap expiringMap = properties.getExpiringMap();
        ExpiringMapClients.ExpiringMapClientsBuilder builder = ExpiringMapClients
                .builder()
                .acquireMaxSize(expiringMap.getMaxSize())
                .acquireDefaultExpireTime(properties.getDefaultExpireTime())
                .acquireDefaultExpireTimeUnit(properties.getDefaultExpireTimeUnit())
                .acquireDefaultExpirationPolicy(expiringMap.getExpirationPolicy());
        return new ExpiringMapCacheFactory(builder.build());
    }
}
