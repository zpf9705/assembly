package top.osjf.assembly.cache.persistence;

import java.util.concurrent.TimeUnit;

/**
 * Cache persistent file writing process definition method interface.
 * @param <K> the type of keys maintained by this cache.
 * @param <V> the type of cache values.
 * @author zpf
 * @since 1.0.0
 */
public interface CachePersistenceWriteProcess<K, V> {

    /**
     * Write cache files to local disk.
     */
    void write();

    /**
     * Determine whether the persistent cache file exists.
     *
     * @return Returns {@link Boolean} is true represent existence, and vice versa.
     */
    boolean persistenceExist();

    /**
     * Set expiration persistent file to the hard disk.
     * <p>The existence time will be reset.</p>
     *
     * @param duration the expiration duration.
     * @param timeUnit the expiration duration timeUnit.
     */
    void setExpirationPersistence(Long duration, TimeUnit timeUnit);

    /**
     * Reset the duration of persistent files.
     */
    void resetExpirationPersistence();

    /**
     * Replace the content value value of the persistent cache file.
     * <p>The existence time will be reset.</p>
     *
     * @param newValue new value.
     */
    void replacePersistence(V newValue);

    /**
     * Delete the currently cached persistent file.
     */
    void removePersistence();

    /**
     * Delete all the cache Persistence files.
     */
    void removeAllPersistence();

    /**
     * Determine whether the current cache persistence file has expired.
     *
     * @return Returns {@link Boolean} is true no expiry, and vice versa.
     */
    boolean expireOfCache();

    /**
     * Obtain a storage variable model for cache persistence.
     *
     * @return Should be a subclass of the abstract model.
     */
    AbstractCachePersistence.AbstractPersistenceStore<K, V> getAttributeStore();

    /**
     * Obtain the specific value of the cache attribute value.
     *
     * @return Please check the specific class {@link Entry}.
     */
    Entry<K, V> getEntry();
}
