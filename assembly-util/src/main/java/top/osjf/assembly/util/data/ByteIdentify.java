package top.osjf.assembly.util.data;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.SimilarAble;
import top.osjf.assembly.util.serial.SerialUtils;

import java.util.Arrays;
import java.util.function.Function;

/**
 * The generic type of {@link Identify} is the {@code byte[]} implementation class.
 *
 * <p>The authentication object of byte numbers mainly solves the problem of byte
 * serialization address values changing.
 *
 * <p>After deserialization, use a separate API for handling {@link String} types.</p>
 *
 * @author zpf
 * @since 1.0.0
 */
public class ByteIdentify extends Identify<byte[], ByteIdentify> {

    private static final long serialVersionUID = -851217802015189183L;

    public Function<byte[], Object> deserializeFc = SerialUtils::deserialize;

    public ByteIdentify(byte[] var) {
        super(var);
    }

    /**
     * Set deserialize function.
     *
     * @param deserializeFc deserialize function.
     * @since 1.1.3
     */
    public void setDeserializeFc(Function<byte[], Object> deserializeFc) {
        this.deserializeFc = deserializeFc;
    }

    /**
     * Return deserialize function.
     *
     * @return deserializeFc deserialize function.
     * @since 1.1.3
     */
    public Function<byte[], Object> getDeserializeFc() {
        return deserializeFc;
    }

    @Override
    public int compareTo(@NotNull ByteIdentify o) {
        byte[] data = getData();
        byte[] dataChallenge = o.getData();
        int minLen = Math.min(data.length, dataChallenge.length);
        for (int i = 0; i < minLen; i++) {
            if (data[i] != dataChallenge[i]) {
                return Byte.compare(data[i], dataChallenge[i]);
            }
        }
        return Integer.compare(data.length, dataChallenge.length);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean similarTo(ByteIdentify o) {
        byte[] data = getData();
        byte[] dataChallenge = o.getData();
        if (Arrays.equals(data, dataChallenge)) {
            return true;
        }
        Object deserializeData = deserializeFc.apply(data);
        Object deserializeDataChallenge = o.getDeserializeFc().apply(dataChallenge);
        if (deserializeData instanceof SimilarAble) {
            //Note type cast exceptions.
            return ((SimilarAble) deserializeData).similarTo(deserializeDataChallenge);
        }
        return super.similarTo(deserializeData, deserializeDataChallenge);
    }

    @Override
    public String toString() {
        return String.format("Byte array = %s , real value = %s", Arrays.toString(getData()),
                deserializeFc.apply(getData()));
    }
}
