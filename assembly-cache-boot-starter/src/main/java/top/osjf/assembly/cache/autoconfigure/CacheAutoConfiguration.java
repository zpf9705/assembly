package top.osjf.assembly.cache.autoconfigure;

import org.springframework.beans.factory.ObjectProvider;
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
 * <p>This layer is {@link net.jodah.expiringmap.ExpiringMap}</p>
 * Additional data on the bottom will adopt a byte type for storage in order to enhance
 * the cache restart recovery.
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
                                  ObjectProvider<List<ConfigurationCustomizer>> listObjectProvider) {
        this.properties = properties;
        this.configurationCustomizers = listObjectProvider.getIfAvailable();
    }

    @Override
    public void afterPropertiesSet() {
        this.printBanner(this.environment, getSourceClass(), System.out);
        if (CollectionUtils.isNotEmpty(configurationCustomizers)) {
            configurationCustomizers.forEach(v -> v.customize(this.properties));
        }
        top.osjf.assembly.cache.config.Configuration.setGlobalConfiguration(properties.getGlobeConfiguration());
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

    @Bean
    @ConditionalOnMissingBean
    public CacheTemplate<String, Object> cacheTemplate(CacheFactory cacheFactory) {
        CacheTemplate<String, Object> template = new CacheTemplate<>();
        template.setCacheFactory(cacheFactory);
        template.setKeySerializer(new StringPairSerializer());
        template.setValueSerializer(new SerializerAdapter<>(Object.class));
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public StringCacheTemplate stringCacheTemplate(CacheFactory cacheFactory) {
        StringCacheTemplate template = new StringCacheTemplate();
        template.setCacheFactory(cacheFactory);
        return template;
    }

    @Bean("StringObjectValueOperations")
    @ConditionalOnMissingBean
    public ValueOperations<String, Object> valueOperations(CacheTemplate<String, Object> cacheTemplate) {
        return cacheTemplate.opsForValue();
    }

    @Bean("StringStringValueOperations")
    @ConditionalOnMissingBean
    public ValueOperations<String, String> valueOperations(StringCacheTemplate stringCacheTemplate) {
        return stringCacheTemplate.opsForValue();
    }

    @Bean("StringObjectTimeOperations")
    @ConditionalOnMissingBean
    public TimeOperations<String, Object> timeOperations(CacheTemplate<String, Object> cacheTemplate) {
        return cacheTemplate.opsForTime();
    }

    @Bean("StringStringTimeOperations")
    @ConditionalOnMissingBean
    public TimeOperations<String, String> timeOperations(StringCacheTemplate stringCacheTemplate) {
        return stringCacheTemplate.opsForTime();
    }

    @Bean
    @ConditionalOnProperty(prefix = "assembly.cache", name = "globeConfiguration.enablePersistence",
            havingValue = "true")
    @ConditionalOnBean(CacheTemplate.class)
    public PersistenceReductionProcess persistenceReductionProcess(
            @Value("${assembly.cache.persistence-path:default}") String path) {
        return new PersistenceReductionProcess(path);
    }
}
