package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.cache.exceptions.CachePersistenceException;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.Asserts;
import top.osjf.assembly.util.lang.FastJsonUtils;
import top.osjf.assembly.util.serial.SerialUtils;

import java.util.List;

/**
 * The only Special extend for {@link AbstractCachePersistence} with generic {@code byte[]}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class ByteCachePersistence extends AbstractCachePersistence<byte[], byte[]> {

    private static final long serialVersionUID = 5518995337588214891L;

    public ByteCachePersistence() {
        super();
    }

    public ByteCachePersistence(BytePersistence persistence, String writePath) {
        super(persistence, writePath);
    }

    /**
     * Set in cache map {@code ByteCachePersistence} with {@code Entry<byte[], byte[]>} and {@code FactoryBeanName}.<br>
     * Whether you need to query in the cache.
     *
     * @param entry must not be {@literal null}
     * @return {@link ByteCachePersistence}
     */
    public static ByteCachePersistence ofSetBytes(@NotNull Entry<byte[], byte[]> entry) {
        return ofSet(ByteCachePersistence.class, BytePersistence.class, entry);
    }

    /**
     * Set in cache map {@code ByteCachePersistence} with {@code Persistence<byte[],byte[]>}.<br>
     * Don't query is in the cache.
     *
     * @param persistence must not be {@literal null}
     * @return {@link ByteCachePersistence}
     */
    public static ByteCachePersistence ofSetPersistenceBytes(@NotNull BytePersistence persistence) {
        return ofSetPersistence(ByteCachePersistence.class, persistence);
    }

    /**
     * Get an {@code ByteCachePersistence} with {@code key}  in cache map.
     *
     * @param key must not be {@literal null}.
     * @return {@link ByteCachePersistence}
     */
    public static ByteCachePersistence ofGetBytes(byte[] key) {
        return ofGet(key, ByteCachePersistence.class);
    }

    /**
     * Get any {@code ByteCachePersistence} with similar {@code key}  in cache map.
     *
     * @param key must not be {@literal null}.
     * @return {@link ByteCachePersistence}.
     */
    public static List<ByteCachePersistence> ofGetSimilarBytes(byte[] key) {
        return ofGetSimilar(key);
    }

    @Override
    public String getReduction() {
        return ByteCachePersistence.class.getName();
    }

    @Override
    public void reductionUseString(@NotNull StringBuilder buffer) {
        super.reductionUseString(buffer);
        //parse json
        BytePersistence persistence;
        try {
            persistence = FastJsonUtils.parseObject(buffer.toString(), new FastJsonUtils
                    .TypeReferences<BytePersistence>() {});
        } catch (Exception e) {
            throw new CachePersistenceException("Buffer data [" + buffer + " ] parse Persistence error " +
                    "[" + e.getMessage() + "]");
        }
        //No cache in the cache
        ByteCachePersistence globePersistence = ofSetPersistence(ByteCachePersistence.class, persistence);
        Asserts.notNull(globePersistence, "GlobePersistence no be null");
        this.reductionUseEntry(globePersistence);
    }

    @Override
    public Object recoveryDeserializeKey(byte[] key) {
        return SerialUtils.deserialize(key);
    }

    @Override
    public Object recoveryDeserializeValue(byte[] value) {
        return SerialUtils.deserialize(value);
    }

    public static class BytePersistence extends AbstractPersistenceStore<byte[], byte[]> {

        private static final long serialVersionUID = -368192386828860022L;

        public BytePersistence() {
            super();
        }

        public BytePersistence(Entry<byte[], byte[]> entry) {
            super(entry);
        }
    }
}
