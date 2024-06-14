package top.osjf.assembly.util.data;

import cn.hutool.core.lang.hash.CityHash;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.serial.SerialUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

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
 *
 * @param <T>    The type of packaging data.
 * @param <SELF> Compare the types of data.
 * @author zpf
 * @since 1.0.0
 */
public abstract class Identify<T, SELF extends Identify<T, SELF>> implements Comparable<SELF>, SimilarAble<SELF>,
        Serializable {

    private static final long serialVersionUID = -7461905822697156104L;

    private final T data;

    public Function<Object, byte[]> serializeFc = SerialUtils::serialize;

    public Identify(T data) {
        Objects.requireNonNull(data, "Identify data not be null");
        this.data = data;
    }

    public T getData() {
        return data;
    }

    @Override
    public boolean similarTo(SELF o) {
        T data = getData();
        T dataChallenge = o.getData();
        if (data instanceof String && dataChallenge instanceof String) {
            String dataStr = (String) data;
            String dataChallengeStr = (String) dataChallenge;
            // == or %% or -% or %-
            return dataChallengeStr.equals(dataStr) || dataChallengeStr.contains(dataStr);
        }
        return false;
    }

    /**
     * Set serialization function.
     *
     * @param serializeFc serialization function.
     * @since 1.1.3
     */
    public void setSerializeFc(Function<Object, byte[]> serializeFc) {
        this.serializeFc = serializeFc;
    }

    /**
     * Return serialization function.
     *
     * @return serialization function.
     * @since 1.1.3
     */
    public Function<Object, byte[]> getSerializeFc() {
        return serializeFc;
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
            bytes = serializeFc.apply(data);
        }
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
