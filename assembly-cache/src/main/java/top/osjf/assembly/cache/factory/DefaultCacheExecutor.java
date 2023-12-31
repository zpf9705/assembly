package top.osjf.assembly.cache.factory;

import top.osjf.assembly.util.annotation.CanNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Default form {@link CacheExecutor} with interface.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface DefaultCacheExecutor extends CacheExecutor {

    @CanNull
    @Override
    default Long delete(byte[]... keys) {
        return keyCommands().delete(keys);
    }

    @Override
    default Map<byte[], byte[]> deleteType(byte[] key) {
        return keyCommands().deleteType(key);
    }

    @Override
    default Boolean deleteAll() {
        return keyCommands().deleteAll();
    }

    @Override
    default Boolean hasKey(byte[] key) {
        return keyCommands().hasKey(key);
    }

    @CanNull
    @Override
    default Long getExpiration(byte[] key) {
        return keyCommands().getExpiration(key);
    }

    @CanNull
    @Override
    default Long getExpiration(byte[] key, TimeUnit unit) {
        return keyCommands().getExpiration(key, unit);
    }

    @CanNull
    @Override
    default Long getExpectedExpiration(byte[] key) {
        return keyCommands().getExpectedExpiration(key);
    }

    @CanNull
    @Override
    default Long getExpectedExpiration(byte[] key, TimeUnit unit) {
        return keyCommands().getExpectedExpiration(key, unit);
    }

    @Override
    default Boolean setExpiration(byte[] key, Long duration, TimeUnit unit) {
        return keyCommands().setExpiration(key, duration, unit);
    }

    @Override
    default Boolean resetExpiration(byte[] key) {
        return keyCommands().resetExpiration(key);
    }

    @CanNull
    @Override
    default Boolean set(byte[] key, byte[] value) {
        return pairCommands().set(key, value);
    }

    @Override
    default Boolean setE(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        return pairCommands().setE(key, value, duration, unit);
    }

    @CanNull
    @Override
    default Boolean setNX(byte[] key, byte[] value) {
        return pairCommands().setNX(key, value);
    }

    @CanNull
    @Override
    default Boolean setEX(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        return pairCommands().setEX(key, value, duration, unit);
    }

    @Override
    default List<byte[]> getSimilarKeys(byte[] rawKey) {
        return keyCommands().getSimilarKeys(rawKey);
    }

    @CanNull
    @Override
    default byte[] get(byte[] key) {
        return keyCommands().get(key);
    }

    @CanNull
    @Override
    default byte[] getAndSet(byte[] key, byte[] newValue) {
        return pairCommands().getAndSet(key, newValue);
    }
}
