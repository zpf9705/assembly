package top.osjf.assembly.cache.autoconfigure;

import org.springframework.context.annotation.Import;
import top.osjf.assembly.cache.listener.ExpirationMessageListener;
import top.osjf.assembly.cache.persistence.ListeningRecovery;

import java.lang.annotation.*;

/**
 * The enable annotation for automatic registration of listeners can
 * help developers using this caching framework to automatically inject
 * listeners such as {@link ExpirationMessageListener} and {@link ListeningRecovery}
 * into cache configuration.
 *
 * <p>It has been marked in {@link CacheAutoConfiguration} automatic
 * configuration by default.
 *
 * <p>If the former automatic configuration is disabled, then this
 * annotation needs to be manually added to enable it.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Import(ListenersAutoRegisterConfiguration.class)
public @interface EnableListenersAutoRegister {
}
