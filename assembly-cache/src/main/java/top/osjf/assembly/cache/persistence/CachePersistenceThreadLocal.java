package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.cache.serializer.PairSerializer;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * A related class used to temporarily store the inheritance {@link ThreadLocal}
 * function of cache persistence process related data, in version 1.1.4, to add
 * support for custom {@link PairSerializer}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
@SuppressWarnings("rawtypes")
public class CachePersistenceThreadLocal extends ThreadLocal<CachePersistenceThreadLocal.CachePersistenceThreadData> {

    private static final CachePersistenceThreadLocal INSTANCE = new CachePersistenceThreadLocal();

    private CachePersistenceThreadLocal() {
    }

    //—————————————————————————————————————————————— static data  ————————————————————————————————————

    /*
     * (non-Javadoc)
     * @see #setData(CachePersistenceThreadData)
     */
    public static void putData(CachePersistenceThreadData data) {
        INSTANCE.setData(data);
    }

    /*
     * (non-Javadoc)
     * @see #setData(PairSerializer, PairSerializer)
     */
    public static void putData(PairSerializer keyPairSerializer,
                               PairSerializer valuePairSerializer) {
        INSTANCE.setData(keyPairSerializer, valuePairSerializer);
    }

    /*
     * (non-Javadoc)
     * @see #get()
     */
    public static CachePersistenceThreadData getData() {
        return INSTANCE.get();
    }

    /*
     * (non-Javadoc)
     * @see CachePersistenceThreadData#getKeyPairSerializerName()
     */
    public static String getKeyPairSerializerName() {
        CachePersistenceThreadData data = getData();
        return data.getKeyPairSerializerName();
    }

    /*
     * (non-Javadoc)
     * @see CachePersistenceThreadData#getValuePairSerializerName()
     */
    public static String getValuePairSerializerName() {
        CachePersistenceThreadData data = getData();
        return data.getValuePairSerializerName();
    }

    //—————————————————————————————————————————————— instance method  ————————————————————————————————————

    /**
     * Set a {@link CachePersistenceThreadData} restriction to operate within this thread
     * within key {@link PairSerializer} and value {@link PairSerializer}.
     *
     * @param keyPairSerializer   key {@link PairSerializer}.
     * @param valuePairSerializer value {@link PairSerializer}.
     */
    public void setData(PairSerializer keyPairSerializer,
                        PairSerializer valuePairSerializer) {
        setData(CachePersistenceThreadData.ofPairSerializer(keyPairSerializer, valuePairSerializer));
    }

    /**
     * Set a {@link CachePersistenceThreadData} restriction to operate within this thread.
     *
     * @param data Cache persistent thread data objects.
     */
    public void setData(CachePersistenceThreadData data) {
        if (data == null) {
            remove();
        } else {
            set(data);
        }
    }

    //—————————————————————————————————————————————— instance data  ————————————————————————————————————

    @SuppressWarnings("rawtypes")
    public static class CachePersistenceThreadData {

        private final PairSerializer keyPairSerializer;
        private final PairSerializer valuePairSerializer;

        private CachePersistenceThreadData(@NotNull PairSerializer keyPairSerializer,
                                           @NotNull PairSerializer valuePairSerializer) {
            this.keyPairSerializer = keyPairSerializer;
            this.valuePairSerializer = valuePairSerializer;
        }

        public static CachePersistenceThreadData ofPairSerializer(PairSerializer keyPairSerializer,
                                                                  PairSerializer valuePairSerializer) {
            AbstractCachePersistence.cachePairSerializers(keyPairSerializer, valuePairSerializer);
            return new CachePersistenceThreadData(keyPairSerializer, valuePairSerializer);
        }

        /**
         * Returns the fully qualified name of the serialized object class
         * {@link PairSerializer} for the current thread operation key.
         *
         * @return {@link PairSerializer} for the current thread operation key.
         */
        public String getKeyPairSerializerName() {
            return keyPairSerializer.getClass().getName();
        }

        /**
         * Returns the fully qualified name of the serialized object class
         * {@link PairSerializer} for the current thread operation value.
         *
         * @return {@link PairSerializer} for the current thread operation value.
         */
        public String getValuePairSerializerName() {
            return valuePairSerializer.getClass().getName();
        }
    }
}
