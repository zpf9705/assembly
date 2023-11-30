package top.osjf.assembly.cache.command.expiremap;

import net.jodah.expiringmap.ExpiringMap;
import top.osjf.assembly.cache.command.CachePairCommands;
import top.osjf.assembly.cache.factory.ExpiringMapCacheExecutor;

import java.util.concurrent.TimeUnit;

/**
 * About the {@code Pair} within {@link ExpiringMap} operating instructions.
 *
 * @author zpf
 * @since 1.0.0
 */
public class ExpiringMapPairCommands implements CachePairCommands {

    private final ExpiringMapCacheExecutor delegate;

    public ExpiringMapPairCommands(ExpiringMapCacheExecutor delegate) {
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#set(Object, Object)
     */
    @Override
    public Boolean set(byte[] key, byte[] value) {
        return this.delegate.put(key, value);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#setE(Object, Object,Long,TimeUnit)
     */
    @Override
    public Boolean setE(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        return this.delegate.putDuration(key, value, duration, unit);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#setNX(Object, Object)
     */
    @Override
    public Boolean setNX(byte[] key, byte[] value) {
        return this.delegate.putIfAbsent(key, value);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#setEX(Object, Object,Long,TimeUnit)
     */
    @Override
    public Boolean setEX(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        return this.delegate.putIfAbsentDuration(key, value, duration, unit);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#getAndSet(Object, Object)
     */
    @Override
    public byte[] getAndSet(byte[] key, byte[] newValue) {
        return this.delegate.replace(key, newValue);
    }
}
