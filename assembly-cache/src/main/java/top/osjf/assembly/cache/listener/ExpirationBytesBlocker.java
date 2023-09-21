package top.osjf.assembly.cache.listener;

import net.jodah.expiringmap.ExpirationListener;

import java.io.Closeable;
import java.io.Serializable;

/**
 * Cache blocker with {@link ExpirationListener} or other Listeners and auto close expiry source elements.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface ExpirationBytesBlocker extends ExpirationListener<byte[], byte[]>, Closeable, Serializable {
}
