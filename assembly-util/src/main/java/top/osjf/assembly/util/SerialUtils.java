package top.osjf.assembly.util;

import org.apache.commons.lang3.SerializationException;
import top.osjf.assembly.util.annotation.CanNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provide tools related to object serialization, deserialization, and group deserialization.
 *
 * @author zpf
 * @since 1.0.0
 */
public abstract class SerialUtils {

    static final int init_size = 1024;

    /**
     * A given object serialization to a byte array.
     *
     * @param object the object to serialize.
     * @return A byte array, on behalf of the moving object.
     */
    @CanNull
    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream(init_size);
        try (ObjectOutputStream oos = new ObjectOutputStream(stream)) {
            oos.writeObject(object);
            oos.flush();
        } catch (Throwable e) {
            throw new SerializationException("Failed to serialize object with ex msg" + e.getMessage());
        }
        return stream.toByteArray();
    }

    /**
     * Deserialized object into a byte array.
     *
     * @param bytes a serialized object.
     * @return The results of the deserialization bytes.
     */
    @CanNull
    public static Object deserialize(@CanNull byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return ois.readObject();
        } catch (Throwable e) {
            throw new SerializationException("Failed to deserialize bytes with ex msg" + e.getMessage());
        }
    }

    /**
     * Deserialized object into any byte array.
     *
     * @param bytesList any serialized object.
     * @return The results of the deserialization bytes.
     */
    public static List<?> deserializeAny(@CanNull List<byte[]> bytesList) {
        if (bytesList == null || bytesList.isEmpty()) {
            return Collections.emptyList();
        }
        return bytesList.stream().map(SerialUtils::deserialize).collect(Collectors.toList());
    }
}
