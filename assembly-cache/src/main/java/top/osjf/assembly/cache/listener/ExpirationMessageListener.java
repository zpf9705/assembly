package top.osjf.assembly.cache.listener;

/**
 * Expired listener implementation interface.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.8
 */
public interface ExpirationMessageListener {

    /**
     * An abstract method for notifying expired messages,
     * which can be implemented to obtain expired key/value
     * cache values in a timely manner.
     * @param message A {@code message} implementation.
     */
    void onMessage(ObjectMessage message);
}
