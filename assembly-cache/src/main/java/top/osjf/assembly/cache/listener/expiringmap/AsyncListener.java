package top.osjf.assembly.cache.listener.expiringmap;


import java.lang.annotation.*;

/**
 * Asynchronous listener
 * <pre>
 *     {@code
 *     {@code AsyncListener}
 *     public class ExpiringListenerManager extends MessageExpiringContainer {
 *
 *     {@code @Override}
 *     public void onMessage(Message message) {
 *         GeneralLog.info("Cache expiration key [{}]", message.getKey());
 *     }
 * }
 *     }
 * </pre>
 *
 * @author zpf
 * @since 1.0.0
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncListener {
}
