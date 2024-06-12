package top.osjf.assembly.cache.serializer;

import top.osjf.assembly.cache.persistence.CachePersistenceThreadLocal;

import java.util.function.Supplier;

/**
 * The type of serialization operation.
 *
 * <p>Retrieve cache persistent variables related to
 * key and value from the current thread based on their type.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
public enum SerializerOperationType implements Supplier<String> {
    KEY {
        @Override
        public String get() {
            return CachePersistenceThreadLocal.getKeyPairSerializerName();
        }
    }, VALUE {
        @Override
        public String get() {
            return CachePersistenceThreadLocal.getValuePairSerializerName();
        }
    };
}
