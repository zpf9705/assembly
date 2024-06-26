package top.osjf.assembly.cache.listener;

import top.osjf.assembly.cache.config.Configuration;
import top.osjf.assembly.cache.factory.AbstractRecordActivationCenter;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.List;

/**
 * The processing plan after expiration, including the notification
 * of custom expiration listeners and the processing of cached files.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.8
 */
public abstract class AbstractExpirationHandler {

    /**
     * Handle expired cache information, including expiration callbacks
     * from listeners and possible persistent cache cleaning.
     *
     * @param message Expired information.
     */
    protected void handlerExpirationMessage(DefaultMessage message) {
        if (message == null) {
            return;
        }
        List<ExpirationMessageListener> listeners = Configuration.getGlobalConfiguration().
                unmodifiableExpirationMessageListeners();
        if (CollectionUtils.isNotEmpty(listeners)) {
            listeners.forEach((listener) -> listener.onMessage(message));
        }
        AbstractRecordActivationCenter.getGlobalCenter().cleanSupportingElements(message);
    }
}
