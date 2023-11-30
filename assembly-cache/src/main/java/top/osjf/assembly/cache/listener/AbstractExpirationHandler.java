package top.osjf.assembly.cache.listener;

import top.osjf.assembly.cache.config.Configuration;
import top.osjf.assembly.cache.factory.AbstractRecordActivationCenter;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.List;

/**
 * The processing plan after expiration, including the notification
 * of custom expiration listeners and the processing of cached files.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.8
 */
public abstract class AbstractExpirationHandler {

    static List<ExpirationMessageListener> expirationMessageListeners = Configuration.getExpirationMessageListeners();

    public void expiredMessageCapable(DefaultMessage message) {
        if (message == null){
            return;
        }
        //notify
        if (CollectionUtils.isNotEmpty(expirationMessageListeners)) {
            expirationMessageListeners.forEach((listener) -> listener.onMessage(message));
        }
        //clean persistence
        AbstractRecordActivationCenter.getSingletonCenter().cleanSupportingElements(message);
    }
}
