package top.osjf.assembly.cache.factory;

import top.osjf.assembly.cache.command.CacheKeyCommands;
import top.osjf.assembly.cache.command.CachePairCommands;
import top.osjf.assembly.cache.command.expiremap.ExpiringMapKeyCommands;
import top.osjf.assembly.cache.command.expiremap.ExpiringMapPairCommands;
import top.osjf.assembly.util.data.ByteIdentify;
import top.osjf.assembly.util.annotation.CanNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The implementation class for interface {@link ExpiringMapCacheExecutor}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class ExpiringMapCacheExecutorImpl extends AbstractCacheExecutor<ExpireMapCenter>
        implements ExpiringMapCacheExecutor {

    public ExpiringMapCacheExecutorImpl(HelpCenter<ExpireMapCenter> center) {
        super(center);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.ExpireConnection#stringCommands()
     */
    @Override
    public CachePairCommands pairCommands() {
        return new ExpiringMapPairCommands(this);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.ExpireConnection#keyCommands()
     */
    @Override
    public CacheKeyCommands keyCommands() {
        return new ExpiringMapKeyCommands(this);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     */
    @Override
    public Boolean put(byte[] key, byte[] value) {

        final ByteIdentify valueByteIdentify = identifyValueByteArray(value);

        return this.execute(new IdentifyKeyCallback<Boolean>(key) {
            @Override
            public Boolean inHelp(ByteIdentify keyByteIdentify, ExpireMapCenter helpCenter) {
                helpCenter.getSingleton().put(keyByteIdentify, valueByteIdentify);
                return true;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)
     */
    @Override
    public Boolean putDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {

        final ByteIdentify valueByteIdentify = identifyValueByteArray(value);

        return this.execute(new IdentifyKeyCallback<Boolean>(key) {
            @Override
            public Boolean inHelp(ByteIdentify keyByteIdentify, ExpireMapCenter helpCenter) {
                helpCenter.getSingleton().put(keyByteIdentify, valueByteIdentify, duration, unit);
                return true;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#putIfAbsent(Object, Object)
     */
    @Override
    public Boolean putIfAbsent(byte[] key, byte[] value) {

        final ByteIdentify valueByteIdentify = identifyValueByteArray(value);

        return this.execute(new IdentifyKeyCallback<Boolean>(key) {
            @Override
            public Boolean inHelp(ByteIdentify keyByteIdentify, ExpireMapCenter helpCenter) {
                return helpCenter.getSingleton().putIfAbsent(keyByteIdentify, valueByteIdentify) == null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    @Override
    public Boolean putIfAbsentDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {

        final ByteIdentify valueByteIdentify = identifyValueByteArray(value);

        return this.execute(new IdentifyKeyCallback<Boolean>(key) {
            @Override
            public Boolean inHelp(ByteIdentify keyByteIdentify, ExpireMapCenter helpCenter) {
                ByteIdentify old = helpCenter.getSingleton().putIfAbsent(keyByteIdentify, valueByteIdentify);
                if (old == null) {
                    helpCenter.getSingleton().setExpiration(keyByteIdentify, duration, unit);
                    return true;
                }
                return false;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#get(Object)
     */
    @Override
    public byte[] getVal(byte[] key) {
        ByteIdentify identify = this.execute((center) ->
                center.getHelpCenter().getSingleton().get(identifyKeyByteArray(key)));
        return identify == null ? null : identify.getData();
    }

    @Override
    public List<byte[]> findSimilarKeys(byte[] key) {

        return this.execute(new IdentifyKeyCallback<List<byte[]>>(key) {

            @Override
            public List<byte[]> inHelp(ByteIdentify keyByteIdentify, ExpireMapCenter helpCenter) {
                return helpCenter.getSingleton().keySet()
                        .stream()
                        .filter(keyByteIdentify::compareToReturnsBool)
                        .map(ByteIdentify::getData)
                        .collect(Collectors.toList());
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#replace(Object, Object)
     */
    @Override
    public byte[] replace(byte[] key, byte[] newValue) {

        final ByteIdentify valueByteIdentify = identifyValueByteArray(newValue);

        return this.execute(new IdentifyKeyCallback<byte[]>(key) {
            @Override
            public byte[] inHelp(ByteIdentify keyByteIdentify, ExpireMapCenter helpCenter) {
                ByteIdentify identify = helpCenter.getSingleton().replace(keyByteIdentify, valueByteIdentify);
                if (identify != null) {
                    return identify.getData();
                }
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object)
     */
    @CanNull
    @Override
    public Long deleteByKeys(byte[]... keys) {
        long count = 0L;
        for (byte[] key : keys) {

            ByteIdentify removed = this.execute((center) -> center.getHelpCenter().getSingleton()
                    .remove(identifyKeyByteArray(key)));

            if (removed != null) {
                count++;
            }
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object, Object)
     */
    @Override
    public Map<byte[], byte[]> deleteSimilarKey(byte[] key) {
        List<byte[]> keys = findSimilarKeys(key);
        Map<byte[], byte[]> delMap = new HashMap<>();

        for (byte[] _key : keys) {

            ByteIdentify removed = this.execute((center) -> center.getHelpCenter().getSingleton()
                    .remove(identifyKeyByteArray(key)));

            if (removed != null) {
                delMap.put(_key, removed.getData());
            }
        }
        return delMap;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#clear()
     */
    @Override
    public Boolean reboot() {
        return this.execute(new IdentifyKeyCallback<Boolean>() {
            @Override
            public Boolean inHelp(ByteIdentify keyByteIdentify, ExpireMapCenter helpCenter) {
                helpCenter.getSingleton().clear();
                return true;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsKey(Object)
     */
    @Override
    public Boolean containsKey(byte[] key) {
        return this.execute((center) -> center.getHelpCenter().getSingleton()
                .containsKey(identifyKeyByteArray(key)));
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsValue(Object)
     */
    @Override
    public Boolean containsValue(byte[] value) {
        return this.execute((center) -> center.getHelpCenter().getSingleton()
                .containsValue(identifyValueByteArray(value)));
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    @Override
    public Long getExpirationWithKey(byte[] key) {
        return this.execute((center) -> center.getHelpCenter().getSingleton()
                .getExpiration(identifyKeyByteArray(key)));
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    @Override
    public Long getExpirationWithUnit(byte[] key, TimeUnit unit) {
        Long expiration = this.getExpirationWithKey(key);
        if (expiration == null) return null;
        return TimeUnit.MILLISECONDS.convert(expiration, unit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    @Override
    public Long getExpectedExpirationWithKey(byte[] key) {
        return this.execute((center) -> center.getHelpCenter().getSingleton()
                .getExpectedExpiration(identifyKeyByteArray(key)));
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    @Override
    public Long getExpectedExpirationWithUnit(byte[] key, TimeUnit unit) {
        Long expectedExpiration = this.getExpectedExpirationWithKey(key);
        if (expectedExpiration == null) return null;
        return TimeUnit.MILLISECONDS.convert(expectedExpiration, unit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    @Override
    public Boolean setExpirationDuration(byte[] key, Long duration, TimeUnit timeUnit) {

        return this.execute(new IdentifyKeyCallback<Boolean>(key) {
            @Override
            public Boolean inHelp(ByteIdentify keyByteIdentify, ExpireMapCenter helpCenter) {
                helpCenter.getSingleton().setExpiration(keyByteIdentify, duration, timeUnit);
                return true;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#resetExpiration(Object)
     */
    @Override
    public Boolean resetExpirationWithKey(byte[] key) {
        return this.execute(new IdentifyKeyCallback<Boolean>(key) {
            @Override
            public Boolean inHelp(ByteIdentify keyByteIdentify, ExpireMapCenter helpCenter) {
                helpCenter.getSingleton().resetExpiration(keyByteIdentify);
                return true;
            }
        });
    }
}
