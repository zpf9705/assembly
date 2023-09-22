package top.osjf.assembly.cache.listener;

import top.osjf.assembly.cache.factory.AbstractRecordActivationCenter;
import top.osjf.assembly.util.CloseableUtils;
import top.osjf.assembly.util.data.ByteIdentify;

/**
 * Default abstract Expiring Load Listener container of key{@code  Object} and value {@code Object}
 *
 * @author zpf
 * @since 1.0.0
 */
public abstract class DefaultMessageContainer implements ExpirationBytesBlocker {

    private static final long serialVersionUID = -3836350084112628115L;

    private MessageCapable capable;

    @Override
    public void expired(ByteIdentify key, ByteIdentify value) {
        this.capable = Message.serial(key.getVar(), value.getVar());
        //notify
        onMessage(this.capable);
        //clean Persistence with key and value
        CloseableUtils.close(this);
    }

    @Override
    public void close() {
        AbstractRecordActivationCenter.getSingletonCenter().cleanSupportingElements(capable);
    }

    /**
     * An abstract method for notifying expired messages, which can be implemented to
     * obtain expired key/value cache values in a timely manner.
     *
     * @param capable A {@code message} implementation.
     */
    public abstract void onMessage(MessageCapable capable);
}
