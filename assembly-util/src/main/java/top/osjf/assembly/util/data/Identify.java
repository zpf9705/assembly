package top.osjf.assembly.util.data;

import cn.hutool.core.lang.hash.CityHash;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.serial.SerialUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * <h3>Hashcode Rewrite rule:</h3>
 * <p>Rewrite the {@link #hashCode()} method to mask address value verification and instead add byte
 * array values to obtain a new {@code hashCode} value.<br>
 *
 * <p>Prioritize using self rewritten {@link #hashCode()} methods.<br>
 *
 * <p>If not rewritten, use {@link CityHash#hash32(byte[])} for hashcode calculation.
 * <h3>Equals Rewrite rule:</h3>
 *
 * <p>Rewritten the {@link #equals(Object)} method, requiring the class class to
 * be {@link Identify} or its subclasses in order to perform {@code hashcode} calculations,
 * and requiring equality.
 * @param <T> The type of packaging data.
 * @param <SELF> Compare the types of data.
 * @author zpf
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public abstract class Identify<T, SELF> implements ComparableBool<SELF>, Serializable {

    private final T data;

    public Identify(T data) {
        Objects.requireNonNull(data, "Identify data not be null");
        this.data = data;
    }

    public T getData() {
        return data;
    }

    @Override
    public int hashCode() {
        Method method = ReflectUtils.getMethod(data.getClass(), "hashCode");
        if (method != null) {
            if (!method.getDeclaringClass().getName().equals("java.lang.Object")) {
                return data.hashCode();
            }
        }
        byte[] bytes;
        if (data instanceof byte[]) {
            bytes = (byte[]) data;
        } else {
            bytes = SerialUtils.serialize(data);
        }
        Objects.requireNonNull(bytes, "Hash bytes not be null");
        return CityHash.hash32(bytes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Identify)) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }
}
