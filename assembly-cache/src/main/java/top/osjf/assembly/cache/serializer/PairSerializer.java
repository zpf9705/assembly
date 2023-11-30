package top.osjf.assembly.cache.serializer;

/**
 * The serialization method interface is mainly a method
 * definition for byte array serialization and deserialization,
 * with a generic definition being the converted object type after
 * deserialization.
 *
 * @param <T> Object type.
 * @author zpf
 * @since 1.0.0
 **/
public interface PairSerializer<T> {

    /**
     * Serialize object to byte array.
     *
     * @param t Serialize target object
     * @return no be {@literal  null}
     */
    byte[] serialize(T t);

    /**
     * Deserialize byte group numbers into real objects.
     *
     * @param bytes byte array
     * @return {@link Object}
     */
    T deserialize(byte[] bytes);

    /**
     * Obtain a class object for deserializing generics
     *
     * @return Target class
     */
    Class<T> serializerType();
}
