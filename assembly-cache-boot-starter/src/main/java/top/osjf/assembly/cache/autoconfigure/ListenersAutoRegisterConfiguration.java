package top.osjf.assembly.cache.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.cache.listener.ExpirationMessageListener;
import top.osjf.assembly.cache.persistence.ListeningRecovery;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.List;

/**
 * Automatic registration configuration of cache related listeners.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ListenersAutoRegisterConfiguration implements ConfigurationCustomizer {

    private List<ExpirationMessageListener> expirationMessageListeners;
    private List<ListeningRecovery> listeningRecoveries;

    @Autowired(required = false)
    public void setExpirationMessageListeners(List<ExpirationMessageListener> expirationMessageListeners) {
        this.expirationMessageListeners = expirationMessageListeners;
    }

    @Autowired(required = false)
    public void seListeningRecoveries(List<ListeningRecovery> listeningRecoveries) {
        this.listeningRecoveries = listeningRecoveries;
    }

    @Override
    public void customize(CacheProperties properties) {
        if (CollectionUtils.isNotEmpty(expirationMessageListeners)) {
            properties.getGlobeConfiguration().addExpirationMessageListeners(expirationMessageListeners);
        }
        if (CollectionUtils.isNotEmpty(listeningRecoveries)) {
            properties.getGlobeConfiguration().addListeningRecoveries(listeningRecoveries);
        }
    }
}
