package top.osjf.assembly.cache.serializer;

import top.osjf.assembly.util.annotation.CanNull;

import java.io.Serializable;

/**
 * A method should be developed to serialize all cache key values into
 * byte arrays for data consistency to ensure a unique container.
 *
 * <p>Therefore, serialization and deserialization methods need to be
 * provided.
 *
 * <p>In this interface, {@link #serialize(Object)} and {@link
 * #deserialize(byte[])} methods should be developed to solve the
 * above problems, and the class types for obtaining and deserializing
 * can also be selected.
 *
 * @param <T> Object type.
 * @author zpf
 * @since 1.0.0
 **/
public interface PairSerializer<T> extends Serializable {

    /**
     * Returns a serialized byte array.
     *
     * @param obj The object value to be serialized.
     * @return serialized byte array.
     * @throws PairSerializationException when the Serialization
     *                                    process fails exception.
     */
    byte[] serialize(T obj) throws PairSerializationException;

    /**
     * Returns an object entity deserialized from a byte array.
     *
     * @param bytes The byte array to be deserialized.
     * @return entity deserialized from a byte array.
     * @throws PairSerializationException when the Serialization
     *                                    process fails exception.
     */
    T deserialize(@CanNull byte[] bytes) throws PairSerializationException;

    /**
     * Returns a type object of the deserialized object.
     *
     * @return a type object of the deserialized object.
     */
    Class<T> serializerType();
}
