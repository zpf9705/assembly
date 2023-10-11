package top.osjf.assembly.cache.autoconfigure;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import top.osjf.assembly.cache.factory.CacheFactory;
import top.osjf.assembly.cache.operations.*;
import top.osjf.assembly.cache.persistence.CachePersistenceReduction;
import top.osjf.assembly.cache.persistence.CachePersistenceReductionSelector;
import top.osjf.assembly.cache.serializer.SerializerAdapter;
import top.osjf.assembly.cache.serializer.StringPairSerializer;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.io.PrintStream;
import java.util.List;

/**
 * This configuration is dependent on the spring autowire mechanism of the boot.
 *
 * <p>The principle of the automatic assembly depends on spring mechanism of SPI
 * specific performance for {@code resources/META-INF/spring.factories}
 *
 * <p>Show the annotation {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration}
 *
 * <p>Automatic assembly paradigm for here :
 * <pre>
 *        {@code CacheTemplate<String, String> = new StringCacheTemplate()}
 *        {@code CacheTemplate<String, Object> = new CacheTemplate()}
 * </pre>
 * You can go to see the specific class To learn more.
 *
 * <p>Also provides Jane in operating interface {@link ValueOperations} to simple operations.
 *
 * <p>At the same time you can use {@link ConfigurationCustomizer} to provide personalized
 * configuration expiring , but can be by  {@link ObjectProvider} use an array collection
 * mode faces interface configuration mode.
 *
 * <p>Now according to Spring - the boot - starters - data - redis encapsulation mode.
 *
 * <p>Open in the form of the client to build Expiring, each is implemented in the client.
 *
 * <p>Such as {@link top.osjf.assembly.cache.config.expiringmap.ExpiringMapClients}
 * All the operation will be placed on the Helper and simulate the join operation such
 * as {@link top.osjf.assembly.cache.factory.CacheExecutor}.<br>
 *
 * <p>This layer is {@link top.osjf.assembly.cache.net.jodah.expiringmap.ExpiringMap}</p>
 * Additional data on the bottom will adopt a byte type for storage in order to enhance the cache restart recovery.
 *
 * @author zpf
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({CacheCommonsOperations.class})
@EnableConfigurationProperties({CacheProperties.class})
@Import(ExpiringMapConfiguration.class)
public class CacheAutoConfiguration implements CacheBannerDisplayDevice, EnvironmentAware {

    private final CacheProperties properties;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    public Environment environment;

    public CacheAutoConfiguration(CacheProperties properties,
                                          ObjectProvider<List<ConfigurationCustomizer>> customizerS) {
        this.properties = properties;
        this.configurationCustomizers = customizerS.getIfAvailable();
    }

    @Override
    public void afterPropertiesSet() {
        this.printBanner(this.environment, getSourceClass(), System.out);
        if (CollectionUtils.isNotEmpty(configurationCustomizers)) {
            configurationCustomizers.forEach(v -> v.customize(this.properties));
        }
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        /*
         * print expire starters version and banner info
         */
        StartUpBannerExecutor.printBanner(environment, getStartUpBanner(), sourceClass, out);
    }

    @Override
    @NotNull
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    @NotNull
    public StartUpBanner getStartUpBanner() {
        return new CacheStarterBanner();
    }

    @Bean(DEFAULT_SO_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SO_TEMPLATE)
    public CacheTemplate<String, Object> cacheTemplate(CacheFactory factory) {
        CacheTemplate<String, Object> template = new CacheTemplate<>();
        template.setCacheFactory(factory);
        template.setKeySerializer(new StringPairSerializer());
        template.setValueSerializer(new SerializerAdapter<>(Object.class));
        return template;
    }

    @Bean(DEFAULT_SS_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SS_TEMPLATE)
    public StringCacheTemplate stringCacheTemplate(CacheFactory factory) {
        StringCacheTemplate template = new StringCacheTemplate();
        template.setCacheFactory(factory);
        return template;
    }

    @Bean(DEFAULT_SO_TEMPLATE_OPERATION)
    @ConditionalOnBean(name = DEFAULT_SO_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SO_TEMPLATE_OPERATION)
    public ValueOperations<String, Object> valueOperations(@Qualifier(DEFAULT_SO_TEMPLATE)
                                                           CacheTemplate<String, Object> template) {
        return template.opsForValue();
    }

    @Bean(DEFAULT_SS_TEMPLATE_OPERATION)
    @ConditionalOnBean(name = DEFAULT_SS_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SS_TEMPLATE_OPERATION)
    public ValueOperations<String, String> valueOperations(@Qualifier(DEFAULT_SS_TEMPLATE)
                                                           StringCacheTemplate template) {
        return template.opsForValue();
    }

    @Bean(DEFAULT_SO_TEMPLATE_OPERATION_E)
    @ConditionalOnBean(name = DEFAULT_SO_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SO_TEMPLATE_OPERATION_E)
    public TimeOperations<String, Object> timeOperations(@Qualifier(DEFAULT_SO_TEMPLATE)
                                                                     CacheTemplate<String, Object> template) {
        return template.opsForTime();
    }

    @Bean(DEFAULT_SS_TEMPLATE_OPERATION_E)
    @ConditionalOnBean(name = DEFAULT_SS_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SS_TEMPLATE_OPERATION_E)
    public TimeOperations<String, String> timeOperations(@Qualifier(DEFAULT_SS_TEMPLATE)
                                                                     StringCacheTemplate template) {
        return template.opsForTime();
    }

    @Bean("auto::persistenceReduction")
    @ConditionalOnProperty(prefix = "assembly.cache", name = "open-persistence", havingValue = "true")
    @ConditionalOnBean(CacheTemplate.class)
    public String persistenceReduction(@Value("${assembly.cache.persistence-path:default}") String path) {
        Class<?> reductionClass = this.properties.getPersistenceReductionClass();
        if (reductionClass == null) {
            return "Open persistence now , but provider factoryClass is null so persistenceRegain failed";
        }
        String name = reductionClass.getName();
        CachePersistenceReduction reduction = CachePersistenceReductionSelector.getReductionByClass(reductionClass);
        if (reduction == null) {
            return "Named [" + name + "] persistenceRegain failed";
        }
        reduction.reductionUsePath(path);
        return "Named [" + name + "] exec reduction yet";
    }


    //-----------------------------------Bean name example-------------------------------------
    public static final String DEFAULT_SO_TEMPLATE = "DEFAULT_SO_TEMPLATE";

    public static final String OPERATION = "_OPERATION";

    public static final String OPERATION_E = "_OPERATION_E";

    public static final String DEFAULT_SO_TEMPLATE_OPERATION = DEFAULT_SO_TEMPLATE + OPERATION;

    public static final String DEFAULT_SO_TEMPLATE_OPERATION_E = DEFAULT_SO_TEMPLATE + OPERATION_E;

    public static final String DEFAULT_SS_TEMPLATE = "DEFAULT_SS_TEMPLATE";

    public static final String DEFAULT_SS_TEMPLATE_OPERATION = DEFAULT_SS_TEMPLATE + OPERATION;

    public static final String DEFAULT_SS_TEMPLATE_OPERATION_E = DEFAULT_SS_TEMPLATE + OPERATION_E;
}
