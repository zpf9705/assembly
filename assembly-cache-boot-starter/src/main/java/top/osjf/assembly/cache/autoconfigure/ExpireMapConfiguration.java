package top.osjf.assembly.cache.autoconfigure;

import cn.hutool.core.util.ReflectUtil;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.osjf.assembly.cache.help.ExpireHelperFactory;
import top.osjf.assembly.cache.help.expiremap.ExpireMapClientConfiguration;
import top.osjf.assembly.cache.help.expiremap.ExpireMapClientConfigurationCustomizer;
import top.osjf.assembly.cache.help.expiremap.ExpireMapHelperFactory;
import top.osjf.assembly.cache.core.Console;
import top.osjf.assembly.cache.listener.AsyncListener;
import top.osjf.assembly.cache.listener.SyncListener;
import top.osjf.assembly.util.DefaultConsole;
import top.osjf.assembly.util.ScanUtils;
import top.osjf.assembly.util.annotation.NotNull;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

/**
 * One of the optional caches for this component {@link net.jodah.expiringmap.ExpiringMap}
 * <p>
 * The following is an explanation of important parameters<br>
 * <h3>{@link AssemblyCacheProperties#getExpiringMap()}.</h3><br>
 * {@code Max Size } : the maximum length of the map, add the 1001 th entry, can lead to the first expired
 * immediately (even if not to expiration time).<br>
 * {@code Expiration }: expiration time and expired unit, set the expiration time, is permanent.<br>
 * The use of expiration Policy: <h4>expiration policies.</h4><br>
 * {@code CREATED}: when each update element, the countdown reset date.<br>
 * {@code ACCESSED }: in every visit elements, the countdown reset date.<br>
 * {@code Variable Expiration }: allows you to update the Expiration time value, if not set variable Expiration.<br>
 * Are not allowed to change the expiration time, once the executive change the expiration time
 * will throw an {@link UnsupportedOperationException}.<br>
 * {@code Expiration Listener }: Synchronous listener , Need to implement {@link ExpirationListener}
 * and annotate {@link SyncListener} to achieve synchronous expiration notification.<br>
 * {@code Async Expiration Listener } : Asynchronous listener , Need to implement {@link ExpirationListener}
 * and annotate {@link AsyncListener} to achieve synchronous expiration notification.<br>
 * {@code Entry Loader } : lazy loading, if the key does not exist when calling the get method to create
 * the default value.<br>
 * <p>
 * Provide a configuration for you to load relevant build configurations related to {@link ExpiringMap},
 * and select this cache configuration to load and use in the main assembly class by default.
 *
 * @author zpf
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ExpiringMap.class})
@ConditionalOnProperty(
        name = "assembly.cache.client",
        havingValue = "expire_map",
        matchIfMissing = true
)
public class ExpireMapConfiguration extends CacheCommonsConfiguration implements CacheBannerDisplayDevice,
        EnvironmentAware {

    private Environment environment;

    static final String SYNC_SIGN = "SYNC";

    static final String ASYNC_SIGN = "ASYNC";

    static String EXPIRED_METHOD_NAME;

    static Predicate<Method> METHOD_PREDICATE;

    static {
        /*
         * Take the default Expiration Listener the class name of the first method
         */
        Method[] methods = ExpirationListener.class.getMethods();
        if (ArrayUtils.isNotEmpty(methods)) {
            EXPIRED_METHOD_NAME = methods[0].getName();
            //Matching assertion method static load
            METHOD_PREDICATE = (s) -> EXPIRED_METHOD_NAME.equals(s.getName());
        }
    }

    public ExpireMapConfiguration(AssemblyCacheProperties properties) {
        super(properties);
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        /*
         * print expire - map version and banner info
         */
        ExpireStartUpBannerExecutor.printBanner(environment, getStartUpBanner(), sourceClass, out);
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
        return new ExpireMapBanner();
    }

    @Bean
    @ConditionalOnMissingBean({ExpireHelperFactory.class})
    public ExpireHelperFactory expireMapConnectionFactory(
            ObjectProvider<ExpireMapClientConfigurationCustomizer> buildCustomizer) {
        ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder builder =
                ExpireMapClientConfiguration.builder();
        buildCustomizer.orderedStream()
                .forEach((customizer) -> customizer.customize(builder));
        return new ExpireMapHelperFactory(builder.build());
    }

    @Bean("expireMap::expireMapClientCustomizer")
    @SuppressWarnings("rawtypes")
    public ExpireMapClientConfigurationCustomizer expireMapClientCustomizer() {
        AssemblyCacheProperties expireProperties = getProperties();
        return c -> {
            ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder builder =
                    c.acquireMaxSize(expireProperties.getExpiringMap().getMaxSize())
                            .acquireDefaultExpireTime(expireProperties.getDefaultExpireTime())
                            .acquireDefaultExpireTimeUnit(expireProperties.getDefaultExpireTimeUnit())
                            .acquireDefaultExpirationPolicy(expireProperties.getExpiringMap().getExpirationPolicy());
            Map<String, List<ExpirationListener>> listenerMap = findExpirationListener();
            if (MapUtils.isNotEmpty(listenerMap)) {
                List<ExpirationListener> sync = listenerMap.get(SYNC_SIGN);
                if (CollectionUtils.isNotEmpty(sync)) {
                    sync.forEach(builder::addSyncExpiredListener);
                }
                List<ExpirationListener> async = listenerMap.get(ASYNC_SIGN);
                if (CollectionUtils.isNotEmpty(async)) {
                    async.forEach(builder::addASyncExpiredListener);
                }
            }
        };
    }

    /**
     * Find a listening class that carries {@link SyncListener} or {@link AsyncListener} in the specified path,
     * as it needs to be instantiated.
     * <p>
     * If an interface or abstract class is scanned, it will be scanned for its implementation self class.
     * <p>
     * If it is not, it will not be possible to add listening.
     *
     * @return Sync listeners and async listeners.
     */
    @SuppressWarnings({"rawtypes"})
    public Map<String, List<ExpirationListener>> findExpirationListener() {
        //obtain listing packages path
        String[] listeningPackages = getProperties().getExpiringMap().getListeningPackages();
        if (ArrayUtils.isEmpty(listeningPackages)) {
            DefaultConsole.info(
                    "no provider listening scan path ," +
                            "so ec no can provider binding Expiration Listener !"
            );
            return Collections.emptyMap();
        }
        //reflection find packages
        Set<Class<ExpirationListener>> subTypesOf =
                ScanUtils.getSubTypesOf(ExpirationListener.class, listeningPackages);
        if (CollectionUtils.isEmpty(subTypesOf)) {
            DefaultConsole.info(
                    "No provider implementation ExpiringLoadListener class ," +
                            "so ec no can provider binding Expiration Listener"
            );
            return Collections.emptyMap();
        }
        List<ExpirationListener> sync = new ArrayList<>();
        List<ExpirationListener> async = new ArrayList<>();
        Map<String, List<ExpirationListener>> listenerMap = new HashMap<>();
        for (Class<? extends ExpirationListener> listenerClass : subTypesOf) {
            if (Modifier.isAbstract(listenerClass.getModifiers())) {
                continue;
            }
            Method target;
            if (Arrays.stream(listenerClass.getMethods()).noneMatch(METHOD_PREDICATE)) {
                continue;
            } else {
                target = Arrays.stream(listenerClass.getMethods()).filter(METHOD_PREDICATE)
                        .findFirst()
                        .orElse(null);
            }
            if (target == null) {
                continue;
            }
            ExpirationListener listener;
            try {
                listener = ReflectUtil.newInstance(listenerClass);
            } catch (Throwable e) {
                Console.warn("[" + listenerClass.getName() + "] newInstanceForNoArgs failed : [" + e.getMessage() + "]");
                continue;
            }
            //Synchronous monitoring is preferred
            SyncListener syncListener = listenerClass.getAnnotation(SyncListener.class);
            if (syncListener == null) {
                AsyncListener asyncListener = listenerClass.getAnnotation(AsyncListener.class);
                if (asyncListener != null) {
                    async.add(listener);
                }
            } else {
                sync.add(listener);
            }
        }
        listenerMap.put(SYNC_SIGN, sync);
        listenerMap.put(ASYNC_SIGN, async);
        return listenerMap;
    }
}
