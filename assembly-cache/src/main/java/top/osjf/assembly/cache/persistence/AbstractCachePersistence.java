package top.osjf.assembly.cache.persistence;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import top.osjf.assembly.cache.command.CacheInvocationHandler;
import top.osjf.assembly.cache.logger.Console;
import top.osjf.assembly.cache.exceptions.CachePersistenceException;
import top.osjf.assembly.cache.exceptions.OnOpenPersistenceException;
import top.osjf.assembly.cache.factory.*;
import top.osjf.assembly.cache.operations.ValueOperations;
import top.osjf.assembly.cache.util.CodecUtils;
import top.osjf.assembly.util.CloseableUtils;
import top.osjf.assembly.util.ScanUtils;
import top.osjf.assembly.util.SerialUtils;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Helper for the persistent cache And restart the reply , And the only implementation
 * <ul>
 *     <li>{@link ByteCachePersistence}</li>
 * </ul>
 * Here about key match byte array , Mainly to block out the Java more generic data of differences
 * Here are about cache persistence of the whole process of life
 * He all methods of the realization of all rely on this method, the generic content {@code byte[]}
 * <ul>
 *     <li>{@link CachePersistenceWriteProcess}</li>
 *     <li>{@link CachePersistenceReduction}</li>
 * </ul>
 * Briefly describes the implementation process :
 * <p>
 *  1、By manipulating the data into , Proxy objects method execution may step in cache persistence method
 *  <ul>
 *      <li>{@link ValueOperations}</li>
 *      <li>{@link PersistenceExec}</li>
 *      <li>{@link CacheInvocationHandler#persistenceExec(Object, PersistenceExec, Object[])}</li>
 *  </ul>
 *  And provides a cache persistence mode of operation , Asynchronous and synchronous
 *  <ul>
 *      <li>{@link MethodRunnableCapable#run(Runnable, Consumer)}</li>
 *      <li>{@link Runner}</li>
 *  </ul>
 *  Its operation is completely thread-safe, because here is introduced into the read-write lock
 * <p>
 *  {@link #readLock}
 * <p>
 *  {@link #writeLock}
 * <p>
 *  Read not write, write can't read, to ensure the safety of the file system of the thread
 * <p>
 *  2、Will, depending on the type of execution method after different persistence API calls
 *  <ul>
 *      <li>{@link CachePersistenceSolver}</li>
 *      <li>{@link BytesCachePersistenceSolver}</li>
 *  </ul>
 * <p>
 *  3、Persistent cache will be stored in the form of a particular file
 *  The relevant configuration information will be cached in advance {@link #CACHE_MAP}
 *  Attached to the {@link CodecUtils}
 * <p>
 *  {@link #AT}
 * <p>
 *  {@link #PREFIX_BEFORE}
 * <p>
 *  {@link #configuration}
 * <p>
 *  4、When attached project restart automatically read persistence file in memory
 *  <ul>
 *      <li>{@link CachePersistenceReduction#reductionUseString(StringBuilder)}</li>
 *      <li>{@link CachePersistenceReduction#reductionUsePath(String)}</li>
 *      <li>{@link CachePersistenceReduction#reductionUseFile(File)}</li>
 *  </ul>
 *  And provides asynchronous takes up the recovery of the main thread
 * <p>
 *  5、At final , on the analysis of unexpired value for recovery operations
 *  Mainly the {@link AbstractCachePersistence} implementation class according to
 *  provide the name of the factory to obtain corresponding overloading interface classes
 * <p>
 *  The following help center helps to implement overloading and deletion in persistent caching
 *  <ul>
 *      <li>{@link ReloadCenter}</li>
 *      <li>{@link HelpCenter}</li>
 *      <li>{@link Center}</li>
 *  </ul>
 *
 * @author zpf
 * @since 1.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
public abstract class AbstractCachePersistence<K, V> extends AbstractPersistenceFileManager implements
        CachePersistenceWriteProcess<K, V>, CachePersistenceReduction, Serializable {

    //The system configuration
    private static Configuration configuration;

    private static final Object lock = new Object();

    //The persistent file suffix
    public static final String PREFIX_BEFORE = ".aof";

    //The file content difference between symbols
    public static final String AT = "@";

    //The default configuration symbol
    public static final String DEFAULT_WRITE_PATH_SIGN = "default";

    //Persistent information model
    private Persistence<K, V> persistence;

    //Implementation type object
    private Class<? extends AbstractCachePersistence> globePersistenceClass;

    //Realize persistent information type object
    private Class<? extends Persistence> persistenceClass;

    private static final String DEALT = "$*&";

    //Open the persistent identifier
    private static boolean OPEN_PERSISTENCE;

    //Global information persistent cache
    private static final Map<String, AbstractCachePersistence> CACHE_MAP = new ConcurrentHashMap<>();

    //Key values of the hash value of the cache object
    private static final Map<String, String> KEY_VALUE_HASH = new ConcurrentHashMap<>();

    //The key  toSting cache object
    private static final Map<String, Object> TO_STRING = new ConcurrentHashMap<>();

    //Read-write lock
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    static {
        try {
            resolveCacheProperties();
        } catch (Exception e) {
            Console.error("Load CacheProperties error {}", e.getMessage());
        }
    }

    /**
     * load static cacheProperties
     */
    static void resolveCacheProperties() {
        configuration = Configuration.getConfiguration();
        OPEN_PERSISTENCE = configuration.getOpenPersistence();
        //if you open persistence will auto create directory
        if (OPEN_PERSISTENCE) {
            checkDirectory(configuration.getPersistencePath());
        }
    }

    public AbstractCachePersistence() {
    }

    public AbstractCachePersistence(Persistence<K, V> persistence, String writePath) {
        this.persistence = persistence;
        setWritePath(writePath);
    }

    /**
     * Set in cache map and get an {@code ExpireByteGlobePersistence} with {@code Entry<K,V>}
     * and {@code FactoryBeanName} whether you need to query in the cache.
     *
     * @param globePersistenceClass must not be {@literal null}
     * @param persistenceClass      must not be {@literal null}
     * @param entry                 must not be {@literal null}
     * @param <G>                   Inherit generic.
     * @param <P>                   Inherit son generic.
     * @param <K>                   key generic.
     * @param <V>                   value generic.
     * @return Inherit instance classes from {@link AbstractCachePersistence}.
     */
    public static <G extends AbstractCachePersistence, P extends Persistence, K, V> G ofSet
    (@NotNull Class<G> globePersistenceClass,
     @NotNull Class<P> persistenceClass,
     @NotNull Entry<K, V> entry) {
        checkOf(entry);
        String rawHash = rawHash(entry.getKey(), entry.getValue());
        String writePath = rawWritePath(entry.getKey());
        AbstractCachePersistence<K, V> persistence = CACHE_MAP.get(rawHash);
        if (persistence == null) {
            synchronized (lock) {
                persistence = CACHE_MAP.get(rawHash);
                if (persistence == null) {
                    persistence =
                            reflectForInstance(globePersistenceClass, persistenceClass, entry, expired(entry),
                                    writePath);
                    CACHE_MAP.putIfAbsent(rawHash, persistence);
                    persistence = CACHE_MAP.get(rawHash);
                }
            }
        }
        /*
         * Must conform to the transformation of idea down
         */
        return (G) persistence;
    }

    /**
     * Set in cache map and get an {@code extends AbstractCachePersistence} with {@code extends Persistence}
     * And extends AbstractCachePersistence class type and don't query is in the cache.
     *
     * @param globePersistenceClass can be {@literal null}
     * @param persistence           must not be {@literal null}
     * @param <G>                   Inherit generic
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @return Inherit instance classes from {@link AbstractCachePersistence}.
     */
    public static <G extends AbstractCachePersistence, K, V> G ofSetPersistence(
            @NotNull Class<G> globePersistenceClass,
            @NotNull Persistence<K, V> persistence) {
        checkPersistence(persistence);
        Entry<K, V> entry = persistence.entry;
        AbstractCachePersistence<K, V> globePersistence =
                CACHE_MAP.computeIfAbsent(rawHash(entry.getKey(), entry.getValue()), v ->
                        reflectForInstance(globePersistenceClass, persistence, rawWritePath(entry.getKey())));
        /*
         * @see Single#just(Object)
         */
        return convert(globePersistence, globePersistenceClass);
    }

    /**
     * Use a derived class of this object class object and made a reflection structure subclasses class
     * object instantiation, build a singleton object cache and apply to cache the runtime phase.
     *
     * @param globePersistenceClass must not be {@literal null}
     * @param persistenceClass      must not be {@literal null}
     * @param entry                 must not be {@literal null}
     * @param expired               must not be {@literal null}
     * @param writePath             must not be {@literal null}
     * @param <G>                   Inherit generic
     * @param <P>                   Inherit son generic
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @return Inherit instance classes from {@link AbstractCachePersistence}.
     */
    public static <G extends AbstractCachePersistence, P extends Persistence, K, V> G reflectForInstance
    (@NotNull Class<G> globePersistenceClass,
     @NotNull Class<P> persistenceClass,
     @NotNull Entry<K, V> entry,
     @NotNull Long expired,
     @NotNull String writePath) {
        G globePersistence;
        try {
            //instance for persistenceClass
            P persistence = ReflectUtil.newInstance(persistenceClass, entry);
            //set expired timestamp
            persistence.setExpire(expired);
            //instance for globePersistenceClass
            globePersistence = ReflectUtil.newInstance(globePersistenceClass, persistence, writePath);
            //record class GlobePersistenceClass type and persistenceClass
            globePersistence.recordCurrentType(globePersistenceClass, persistenceClass);
        } catch (Throwable e) {
            throw new CachePersistenceException(e.getMessage());
        }
        return globePersistence;
    }

    /**
     * Use a derived class of this object class object and made a reflection structure subclasses class
     * object instantiation, build a singleton object cache and apply to restart recovery stage.
     *
     * @param globePersistenceClass can be {@literal null}
     * @param persistence           must not be {@literal null}
     * @param writePath             must not be {@literal null}
     * @param <G>                   Inherit generic
     * @param <P>                   Inherit son generic
     * @return Inherit instance classes from {@link AbstractCachePersistence}.
     */
    public static <G extends AbstractCachePersistence, P extends Persistence> G reflectForInstance
    (@NotNull Class<G> globePersistenceClass, // construct have default value can nullable
     @NotNull P persistence,
     @NotNull String writePath) {
        G globePersistence;
        try {
            //instance for globePersistenceClass
            globePersistence = ReflectUtil.newInstance(globePersistenceClass, persistence, writePath);
            //record class GlobePersistenceClass type and persistenceClass
            globePersistence.recordCurrentType(globePersistenceClass, persistence.getClass());
        } catch (Throwable e) {
            throw new CachePersistenceException(e.getMessage());
        }
        return globePersistence;
    }

    /**
     * Get an {@code AbstractCachePersistence<K,V>} in cache map with {@code key} and {@code value}.
     *
     * @param key                   must not be {@literal null}.
     * @param value                 can be {@literal null}.
     * @param globePersistenceClass can be {@literal null}
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @param <G>                   Inherit generic
     * @return Inherit instance classes from {@link AbstractCachePersistence}.
     */
    public static <G extends AbstractCachePersistence, K, V> G ofGet(@NotNull K key, @CanNull V value,
                                                                     @CanNull Class<G> globePersistenceClass) {
        checkOpenPersistence();
        Assert.notNull(key, "Key no be null");
        String persistenceKey;
        if (value == null) {
            String hashKey = CodecUtils.rawHashWithType(key);
            String hashValue = KEY_VALUE_HASH.getOrDefault(hashKey, null);
            Assert.hasText(hashValue, "Key [" + key + "] no be hash value");
            persistenceKey = rawHashComb(hashKey, hashValue);
        } else {
            persistenceKey = rawHash(key, value);
        }
        AbstractCachePersistence<K, V> expireGlobePersistence = CACHE_MAP.get(persistenceKey);
        if (expireGlobePersistence == null) {
            throw new CachePersistenceException("Key: [" + key + "] raw hash no found persistence");
        }
        return convert(expireGlobePersistence, globePersistenceClass);
    }

    /**
     * Get an {@code AbstractCachePersistence<K,V>} in cache map with {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @param <G> Inherit generic
     * @return Inherit instance classes from {@link AbstractCachePersistence}.
     */
    public static <G extends AbstractCachePersistence, K> G ofGet(@NotNull K key) {
        return ofGet(key, null, null);
    }

    /**
     * Get any {@code AbstractCachePersistence<K,V>} in cache map with similar {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @param <G> Inherit generic
     * @return Inherit instance classes from {@link AbstractCachePersistence}.
     */
    public static <G extends AbstractCachePersistence, K> List<G> ofGetSimilar(@NotNull K key) {
        checkOpenPersistence();
        String realKey = CodecUtils.toStingBeReal(key);
        List<String> similarElement = CodecUtils.findSimilarElement(TO_STRING.keySet(), realKey);
        if (CollectionUtils.isEmpty(similarElement)) {
            return Collections.emptyList();
        }
        return similarElement.stream().map(toStringKey -> {
            G g;
            Object keyObj = TO_STRING.get(toStringKey);
            if (keyObj == null) {
                return null;
            }
            String keyHash = CodecUtils.rawHashWithType(keyObj);
            String valueHash = KEY_VALUE_HASH.get(CodecUtils.rawHashWithType(keyObj));
            if (StringUtils.isNotBlank(valueHash)) {
                g = (G) CACHE_MAP.get(rawHashComb(keyHash, valueHash));
            } else {
                g = null;
            }
            return g;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Convert {@code AbstractCachePersistence<K,V> simpleGlobePersistence} to provider globePersistenceClass.
     *
     * @param expireGlobePersistence must not be {@literal null}
     * @param globePersistenceClass  can be {@literal null}
     * @param <G>                    Extend AbstractCachePersistence class generic
     * @param <K>                    key generic
     * @param <V>                    value generic
     * @return Inherit instance classes from {@link AbstractCachePersistence}.
     */
    public static <G extends AbstractCachePersistence, K, V> G convert(@NotNull AbstractCachePersistence<K, V>
                                                                               expireGlobePersistence,
                                                                       //can have no value and
                                                                       // default value be this class
                                                                       @CanNull Class<G> globePersistenceClass) {
        try {
            if (globePersistenceClass == null) return (G) expireGlobePersistence;
            return Convert.convert(globePersistenceClass, expireGlobePersistence);
        } catch (Throwable e) {
            throw new CachePersistenceException(
                    "Provider obj no instanceof" +
                            "Can visit the site to check the transformation of knowledge points down ：" +
                            "https://blog.csdn.net/listeningdu/article/details/127944496"
            );
        }
    }

    /**
     * Check whether accord with standard of cache persistence
     *
     * @param entry must not be {@literal null}.
     * @param <K>   key generic
     * @param <V>   value generic
     */
    static <V, K> void checkOf(@NotNull Entry<K, V> entry) {
        checkOpenPersistence();
        checkEntry(entry);
        //empty just pass
        if (entry.getDuration() == null || entry.getTimeUnit() == null) {
            if (configuration.isDefaultCompareWithExpirePersistence()) {
                return;
            }
            throw new CachePersistenceException("Default setting no support be persisted");
        }
        if (entry.getTimeUnit().toMillis(entry.getDuration()) >=
                configuration.getDefaultNoPersistenceExpireTimeToMille()) {
            return;
        }
        throw new CachePersistenceException("Only more than or == " +
                configuration.getNoPersistenceOfExpireTime() + " " +
                configuration.getNoPersistenceOfExpireTimeUnit() +
                " can be persisted so key [" + entry.getKey() + "] value [" + entry.getValue() + "] no persisted ");
    }

    /**
     * Check the data persistence
     *
     * @param persistence Check the item
     * @param <K>         key generic
     * @param <V>         value generic
     */
    static <V, K> void checkPersistence(@NotNull Persistence<K, V> persistence) {
        Assert.notNull(persistence.getEntry(), "Persistence Entry no be null");
        Assert.notNull(persistence.getExpire(), "Expire no be null");
        checkEntry(persistence.getEntry());
    }

    /**
     * Check the data entry
     *
     * @param entry Check the item
     * @param <K>   key generic
     * @param <V>   value generic
     */
    static <V, K> void checkEntry(@NotNull Entry<K, V> entry) {
        Assert.notNull(entry.getKey(), "Key no be null");
        Assert.notNull(entry.getValue(), "Value no be null");
    }

    /**
     * Check whether the open the persistent cache
     */
    static void checkOpenPersistence() {
        if (!OPEN_PERSISTENCE) {
            throw new OnOpenPersistenceException();
        }
    }

    /**
     * Write the server file path combination.
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @return final write path
     */
    static <K> String rawWritePath(@NotNull K key) {
        Assert.notNull(key, "Key no be null ");
        return configuration.getPersistencePath()
                //md5 sign to prevent the file name is too long
                + CodecUtils.md5Hex(key)
                + PREFIX_BEFORE;
    }

    /**
     * Combination the hash mark of key/value.
     *
     * @param hashKey   must not be {@literal null}.
     * @param hashValue must not be {@literal null}.
     * @return hash mark
     */
    static String rawHashComb(@NotNull String hashKey, @NotNull String hashValue) {
        return DEALT + hashKey + hashValue;
    }

    /**
     * Calculate the hash mark of key/value.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @param <K>   key generic
     * @param <V>   value generic
     * @return hash mark
     */
    static <K, V> String rawHash(@NotNull K key, @NotNull V value) {
        String rawHashKey = CodecUtils.rawHashWithType(key);
        String rawHashValue = KEY_VALUE_HASH.get(rawHashKey);
        if (StringUtils.isBlank(rawHashValue)) {
            rawHashValue = CodecUtils.rawHashWithType(value);
            KEY_VALUE_HASH.putIfAbsent(rawHashKey, rawHashValue);
            recordContentToKeyString(key);
        }
        return DEALT + rawHashKey + rawHashValue;
    }

    /**
     * Converts the String mark of key.
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     */
    static <K> void recordContentToKeyString(@NotNull K key) {
        TO_STRING.putIfAbsent(CodecUtils.toStingBeReal(key), key);
    }

    /**
     * Record the current implementation of the class object type
     * In order to facilitate the subsequent recovery.
     *
     * @param globePersistenceClass {{@link #setGlobePersistenceClass(Class)}}
     * @param persistenceClass      {@link #setPersistenceClass(Class)}
     */
    void recordCurrentType(@NotNull Class<? extends AbstractCachePersistence> globePersistenceClass,
                           @NotNull Class<? extends Persistence> persistenceClass) {
        setGlobePersistenceClass(globePersistenceClass);
        setPersistenceClass(persistenceClass);
    }

    /**
     * Set a son for {@code AbstractCachePersistence}.
     *
     * @param globePersistenceClass {@link AbstractCachePersistence}
     */
    public void setGlobePersistenceClass(Class<? extends AbstractCachePersistence> globePersistenceClass) {
        this.globePersistenceClass = globePersistenceClass;
    }

    /**
     * Set a son for {@code Persistence}.
     *
     * @param persistenceClass {@link Persistence}
     */
    public void setPersistenceClass(Class<? extends Persistence> persistenceClass) {
        this.persistenceClass = persistenceClass;
    }

    /**
     * Get or default with AbstractCachePersistence.
     *
     * @return Clazz for {@link AbstractCachePersistence}.
     */
    public Class<? extends AbstractCachePersistence> getGlobePersistenceClass() {
        if (this.globePersistenceClass == null) {
            return AbstractCachePersistence.class;
        }
        return this.globePersistenceClass;
    }

    /**
     * Get or default with Persistence.
     *
     * @return {@link Persistence}
     */
    public Class<? extends Persistence> getPersistenceClass() {
        if (this.persistenceClass == null) {
            return Persistence.class;
        }
        return persistenceClass;
    }

    @Override
    public void write() {
        //write
        writeLock.lock();
        try {
            this.writeSingleFileLine(this.persistence.toString());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean persistenceExist() {
        readLock.lock();
        try {
            return this.existCurrentWritePath();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void setExpirationPersistence(Long duration, TimeUnit timeUnit) {
        Assert.notNull(duration, "Duration no be null");
        Assert.notNull(timeUnit, "TimeUnit no be null");
        Persistence<K, V> p = this.persistence;
        Entry<K, V> entry = p.getEntry();
        //Verify expiration
        Assert.isTrue(expireOfCache(),
                "Already expire key [" + entry.getKey() + "] value [" + entry.getValue() + "]");
        writeLock.lock();
        try {
            //Delete the old file to add a new file
            this.delWithCurrentWritePath();
            //Refresh time due to the key value no change don't need to delete the application cache
            entry.refreshOfExpire(duration, timeUnit);
            //Redefine the cache
            ofSet(getGlobePersistenceClass(), getPersistenceClass(), Entry.of(entry.getKey(), entry.getValue(),
                    duration, timeUnit)).write();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void resetExpirationPersistence() {
        Persistence<K, V> per = this.persistence;
        //验证是否过期
        Assert.isTrue(expireOfCache(),
                "Already expire key [" + per.getEntry().getKey() + "] value [" +
                        per.getEntry().getValue() + "]");
        writeLock.lock();
        try {
            //Delete the old file to add a new key value no change don't need to delete the application cache
            this.delWithCurrentWritePath();
            //To write a cache file
            ofSet(getGlobePersistenceClass(), getPersistenceClass(), per.getEntry()).write();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void replacePersistence(V newValue) {
        Assert.notNull(newValue, "NewValue no be null");
        Persistence<K, V> per = this.persistence;
        Entry<K, V> entry = per.getEntry();
        //Verify expiration
        Assert.isTrue(expireOfCache(),
                "Already expire key [" + per.entry.getKey() + "] value [" + per.entry.getValue() + "]");
        writeLock.lock();
        try {
            //Delete the old file to add a new file
            this.delWithCurrentWritePath();
            //Delete the cache because the value changes
            CACHE_MAP.remove(rawHash(entry.getKey(), entry.getValue()));
            //To write a cache file
            ofSet(getGlobePersistenceClass(), getPersistenceClass(),
                    Entry.of(entry.getKey(), newValue, entry.getDuration(), entry.getTimeUnit())).write();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removePersistence() {
        Entry<K, V> entry = this.persistence.getEntry();
        writeLock.lock();
        try {
            //Delete the persistent file
            this.delWithCurrentWritePath();
            //Delete the cache
            CACHE_MAP.remove(rawHash(entry.getKey(), entry.getValue()));
            KEY_VALUE_HASH.remove(CodecUtils.rawHashWithType(entry.getKey()));
        } finally {
            writeLock.unlock();
        }
    }

    public static void cleanAllCacheFile() {
        if (MapUtils.isEmpty(CACHE_MAP)) {
            return;
        }
        try {
            for (AbstractCachePersistence value : CACHE_MAP.values()) {
                //del persistence
                value.removePersistence();
            }
            CACHE_MAP.clear();
            KEY_VALUE_HASH.clear();
            TO_STRING.clear();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void removeAllPersistence() {
        if (MapUtils.isEmpty(CACHE_MAP)) {
            return;
        }
        writeLock.lock();
        try {
            for (AbstractCachePersistence value : CACHE_MAP.values()) {
                //del persistence
                value.removePersistence();
            }
            CACHE_MAP.clear();
            KEY_VALUE_HASH.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean expireOfCache() {
        //Expiration date after the current time
        return this.persistence.getExpire() > System.currentTimeMillis();
    }

    @Override
    public Entry<K, V> getEntry() {
        return this.persistence.getEntry();
    }

    @Override
    public Persistence<K, V> getPersistence() {
        return this.persistence;
    }

    @Override
    public String getReduction() {
        return AbstractCachePersistence.class.getName();
    }

    @Override
    public void reductionUsePath(@CanNull String path) {
        if (StringUtils.isBlank(path) || Objects.equals(path, DEFAULT_WRITE_PATH_SIGN)) {
            path = configuration.getPersistencePath();
        }
        List<File> files = null;
        if (StringUtils.isBlank(path)) {
            Console.info("Path no be blank , but you provide null");
        } else {
            if (!isDirectory(path)) {
                Console.info("This path [{}] belong file no a directory", path);
            } else {
                files = loopFiles(path, v -> v.isFile() && v.getName().endsWith(PREFIX_BEFORE));
                if (CollectionUtils.isEmpty(files)) {
                    Console.info("This path [{}] no found files", path);
                }
            }
        }
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        //Loop back
        List<File> finalFiles = files;
        //Start a new thread for file recovery operations
        new Thread(() -> finalFiles.forEach(v -> {
            try {
                reductionUseFile(v);
            } catch (Throwable e) {
                Console.warn("Restore cache file {}  error : {}", v.getName(), e.getMessage());
            }
        }), "Expiry-Record-Cache-Thread").start();
    }

    @Override
    public void reductionUseFile(@NotNull File file) {
        Assert.notNull(file, "File no be null");
        InputStream in = null;
        BufferedReader read = null;
        StringBuilder buffer = new StringBuilder();
        try {
            URL url = file.toURI().toURL();
            in = url.openStream();
            read = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = read.readLine()) != null) {
                //@
                // - This form
                // @
                if (AT.equals(line)) {
                    continue;
                }
                buffer.append(line);
            }
        } catch (Throwable e) {
            throw new CachePersistenceException("Buff read cache error [" + e.getMessage() + "]");
        } finally {
            CloseableUtils.close(in, read);
        }
        //Perform follow-up supplement
        reductionUseString(buffer);
    }

    @Override
    public void reductionUseString(@NotNull StringBuilder buffer) {
        String json = buffer.toString();
        //check json
        Assert.isTrue(JSON.isValid(json), "Buffer data [" + json + "] no a valid json");
    }

    /**
     * Using the {@code Center} cache data check for the rest of removing, and recovery.
     *
     * @param t   must not be {@literal null}.
     * @param <T> extends for {@link AbstractCachePersistence}.
     */
    public <T extends AbstractCachePersistence<K, V>> void deserializeWithEntry(@NotNull T t) {
        //current time
        long currentTimeMillis = System.currentTimeMillis();
        Persistence<K, V> persistence = t.getPersistence();
        //When the time is equal to or judged failure after now in record time
        if (persistence.getExpire() == null || currentTimeMillis >= persistence.getExpire()) {
            //Delete the persistent file
            t.delWithCurrentWritePath();
            throw new CachePersistenceException("File [" + t.getWritePath() + "] record time [" + persistence.getExpire() +
                    "] before or equals now");
        }
        //save key/value with byte[]
        Entry<K, V> entry = persistence.getEntry();
        //check entry
        checkEntry(entry);
        //Calculate remaining time units
        Long condition = condition(currentTimeMillis, persistence.getExpire(), entry.getTimeUnit());
        //reload
        AbstractRecordActivationCenter.getSingletonCenter().reload(entry.getKey(),
                entry.getValue(),
                condition,
                entry.getTimeUnit());
        //Callback for restoring cached keys and values
        Set<Class<ListeningRecovery>> subTypesOf =
                ScanUtils.getSubTypesOf(ListeningRecovery.class, configuration.getListeningRecoverySubPath());
        if (CollectionUtils.isNotEmpty(subTypesOf)) {
            subTypesOf.forEach(clazz -> {
                ListeningRecovery recovery;
                try {
                    recovery = ReflectUtil.newInstance(clazz);
                    recovery.recovery(
                            SerialUtils.deserialize((byte[]) entry.getKey()),
                            SerialUtils.deserialize((byte[]) entry.getValue()));
                    recovery.expired(condition, entry.getTimeUnit());
                } catch (Exception e) {
                    Console.info("Cache recovery callback exception , throw an error : {}", e.getMessage());
                }
            });
        }
    }

    /**
     * Calculating the cache recovery time remaining with {@code TimeUnit}.
     *
     * @param now    must not be {@literal null}
     * @param expire must not be {@literal null}
     * @param unit   must not be {@literal null}
     * @return long result
     */
    public Long condition(@NotNull Long now, @NotNull Long expire, @NotNull TimeUnit unit) {
        long difference = expire - now;
        return unit.convert(difference, TimeUnit.MILLISECONDS);
    }

    /**
     * Calculate the expiration time.
     *
     * @param entry must not be {@literal null}
     * @param <K>   key generic
     * @param <V>   value generic
     * @return result
     */
    private static <V, K> Long expired(@NotNull Entry<K, V> entry) {
        Long expired;
        if (entry.haveDuration()) {
            expired = plusCurrent(entry.getDuration(), entry.getTimeUnit());
        } else {
            expired = plusCurrent(null, null);
        }
        return expired;
    }

    /**
     * Calculate the expiration time with {@code plus}.
     *
     * @param duration can be {@literal null}
     * @param timeUnit can be {@literal null}
     * @return result
     */
    private static Long plusCurrent(@CanNull Long duration, @CanNull TimeUnit timeUnit) {
        if (duration == null) {
            duration = configuration.getDefaultExpireTime();
        }
        if (timeUnit == null) {
            timeUnit = configuration.getDefaultExpireTimeUnit();
        }
        return System.currentTimeMillis() + timeUnit.toMillis(duration);
    }

    /**
     * The persistent attribute model.
     *
     * @param <K> key generic
     * @param <V> value generic
     */
    public abstract static class Persistence<K, V> implements Serializable {
        private static final long serialVersionUID = 5916681709307714445L;
        private Entry<K, V> entry;
        private Long expire;
        static final String FORMAT = AT + "\n" + "%s" + "\n" + AT;

        public Persistence() {
        }

        public Persistence(Entry<K, V> entry) {
            this.entry = entry;
        }

        public void setEntry(Entry<K, V> entry) {
            this.entry = entry;
        }

        public Entry<K, V> getEntry() {
            return entry;
        }


        public void setExpire(@NotNull Long expire) {
            this.expire = expire;
        }

        public Long getExpire() {
            return expire;
        }

        @Override
        public String toString() {
            return String.format(FORMAT, JSON.toJSONString(this));
        }
    }
}
