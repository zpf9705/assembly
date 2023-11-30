package top.osjf.assembly.cache.listener;

import net.jodah.expiringmap.ExpirationListener;
import top.osjf.assembly.util.data.ByteIdentify;

/**
 * The default expiration synchronization listener for {@link net.jodah.expiringmap.ExpiringMap}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.8
 */
public class DefaultExpiringmapExpirationListener extends AbstractExpirationHandler implements
        ExpirationListener<ByteIdentify, ByteIdentify> {

    public static final DefaultExpiringmapExpirationListener LISTENER = new DefaultExpiringmapExpirationListener();

    private DefaultExpiringmapExpirationListener() {
    }

    @Override
    public void expired(ByteIdentify key, ByteIdentify value) {
        expiredMessageCapable(new DefaultMessage(key.getData(), value.getData()));
    }
}
