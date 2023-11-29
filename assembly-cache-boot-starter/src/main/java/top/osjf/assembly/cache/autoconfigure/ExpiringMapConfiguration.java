package top.osjf.assembly.cache.autoconfigure;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import top.osjf.assembly.cache.config.expiringmap.ExpiringMapClients;
import top.osjf.assembly.cache.config.expiringmap.ExpiringMapClientsCustomizer;
import top.osjf.assembly.cache.factory.CacheFactory;
import top.osjf.assembly.cache.factory.ExpiringMapCacheFactory;
import top.osjf.assembly.cache.listener.expiringmap.AsyncListener;
import top.osjf.assembly.cache.listener.expiringmap.SyncListener;
import top.osjf.assembly.cache.net.jodah.expiringmap.ExpirationListener;
import top.osjf.assembly.cache.net.jodah.expiringmap.ExpiringMap;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.io.ScanUtils;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.MapUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.logger.Console;
import top.osjf.assembly.util.system.DefaultConsole;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * One of the optional caches for this component {@link ExpiringMap}.
 * <p>
 * The following is an explanation of important parameters<br>
 * <h3>{@link CacheProperties#getExpiringMap()}.</h3><br>
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
@SuppressWarnings({"rawtypes"})
public class ExpiringMapConfiguration extends CacheCommonsConfiguration implements CacheBannerDisplayDevice,
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
    public CacheFactory expiringMapCacheExecutorFactory(
            ObjectProvider<List<ExpiringMapClientsCustomizer>> listObjectProvider) {
        ExpiringMapClients.ExpiringMapClientsBuilder builder = ExpiringMapClients.builder();
        listObjectProvider.orderedStream().forEach(expiringMapClientsCustomizers -> {
            if (CollectionUtils.isNotEmpty(expiringMapClientsCustomizers)) {
                expiringMapClientsCustomizers.forEach(customizer -> customizer.customize(builder));
            }
        });
        return new ExpiringMapCacheFactory(builder.build());
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ExpiringMapClientsCustomizer expiringMapClientsCustomizer() {
        CacheProperties properties = getProperties();
        return customizer -> {
            ExpiringMapClients.ExpiringMapClientsBuilder builder =
                    customizer.acquireMaxSize(properties.getExpiringMap().getMaxSize())
                            .acquireDefaultExpireTime(properties.getDefaultExpireTime())
                            .acquireDefaultExpireTimeUnit(properties.getDefaultExpireTimeUnit())
                            .acquireDefaultExpirationPolicy(properties.getExpiringMap().getExpirationPolicy());
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
     * Find a listening class that carries {@link SyncListener} or
     * {@link AsyncListener} in the specified path, as it needs to
     * be instantiated.
     * <p>If an interface or abstract class is scanned, it will be
     * scanned for its implementation self class.
     * <p>If it is not, it will not be possible to add listening.
     *
     * @return Sync listeners and async listeners.
     */
    private Map<String, List<ExpirationListener>> findExpirationListener() {
        CacheProperties.ExpiringMap expiringMap = getProperties().getExpiringMap();
        List<Class<? extends ExpirationListener>> expirationListenerClasses =
                expiringMap.getExpirationListenerClasses();
        String[] listeningPackages = getProperties().getExpiringMap().getListeningPackages();
        if (ArrayUtils.isNotEmpty(listeningPackages)) {
            Set<Class<ExpirationListener>> subTypesOf =
                    ScanUtils.getSubTypesOf(ExpirationListener.class, listeningPackages);
            if (CollectionUtils.isNotEmpty(subTypesOf)) {
                expirationListenerClasses.addAll(subTypesOf);
            }
        }
        if (CollectionUtils.isNotEmpty(expirationListenerClasses)) {
            expirationListenerClasses =
                    expirationListenerClasses.stream().distinct().collect(Collectors.toList());
        } else {
            DefaultConsole.info("There is no class object provided for the scanning" +
                    " package path or class that can be registered and used by the listener.");
            return Collections.emptyMap();
        }
        List<ExpirationListener> sync = new ArrayList<>();
        List<ExpirationListener> async = new ArrayList<>();
        Map<String, List<ExpirationListener>> listenerMap = new HashMap<>();
        for (Class<? extends ExpirationListener> expirationListenerClass : expirationListenerClasses) {
            if (Modifier.isAbstract(expirationListenerClass.getModifiers())
                    || Modifier.isInterface(expirationListenerClass.getModifiers())) {
                continue;
            }
            Method target;
            if (Arrays.stream(expirationListenerClass.getMethods()).noneMatch(METHOD_PREDICATE)) {
                continue;
            } else {
                target = Arrays.stream(expirationListenerClass.getMethods()).filter(METHOD_PREDICATE)
                        .findFirst()
                        .orElse(null);
            }
            if (target == null) {
                continue;
            }
            ExpirationListener listener;
            try {
                listener = ReflectUtils.newInstance(expirationListenerClass);
            } catch (Throwable e) {
                Console.warn("[" + expirationListenerClass.getName() + "] newInstanceForNoArgs" +
                        " failed : [" + e.getMessage() + "]");
                continue;
            }
            SyncListener syncListener = expirationListenerClass.getAnnotation(SyncListener.class);
            if (syncListener == null) {
                AsyncListener asyncListener = expirationListenerClass.getAnnotation(AsyncListener.class);
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
