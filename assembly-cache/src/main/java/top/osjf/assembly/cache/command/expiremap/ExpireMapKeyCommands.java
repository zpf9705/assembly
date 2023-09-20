package top.osjf.assembly.cache.command.expiremap;

import top.osjf.assembly.cache.command.ExpireKeyCommands;
import top.osjf.assembly.cache.help.expiremap.ExpireMapHelper;
import top.osjf.assembly.util.annotation.CanNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * About the key {@code key} operating instructions
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapKeyCommands implements ExpireKeyCommands {

    private final ExpireMapHelper delegate;

    public ExpireMapKeyCommands(ExpireMapHelper delegate) {
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#delete(byte[]...)
     */
    @CanNull
    @Override
    public Long delete(byte[]... keys) {
        return this.delegate.deleteReturnSuccessNum(keys);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#deleteType(byte[]...)
     */
    @Override
    public Map<byte[], byte[]> deleteType(byte[] key) {
        return this.delegate.deleteSimilarKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#deleteAll()
     */
    @Override
    public Boolean deleteAll() {
        return this.delegate.reboot();
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#hasKey(byte[])
     */
    @Override
    public Boolean hasKey(byte[] key) {
        return this.delegate.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpiration(byte[])
     */
    @Override
    public Long getExpiration(byte[] key) {
        return this.delegate.getExpirationWithKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpiration(byte[], TimeUnit)
     */
    @Override
    public Long getExpiration(byte[] key, TimeUnit unit) {
        return this.delegate.getExpirationWithUnit(key, unit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpectedExpiration(byte[])
     */
    @Override
    public Long getExpectedExpiration(byte[] key) {
        return this.delegate.getExpectedExpirationWithKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpectedExpiration(byte[], TimeUnit)
     */
    @Override
    public Long getExpectedExpiration(byte[] key, TimeUnit unit) {
        return this.delegate.getExpectedExpirationWithUnit(key, unit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#setExpiration(byte[], Long, TimeUnit)
     */
    @Override
    public Boolean setExpiration(byte[] key, Long duration, TimeUnit timeUnit) {
        return this.delegate.setExpirationDuration(key, duration, timeUnit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#resetExpiration(byte[])
     */
    @Override
    public Boolean resetExpiration(byte[] key) {
        return this.delegate.resetExpirationWithKey(key);
    }
}
