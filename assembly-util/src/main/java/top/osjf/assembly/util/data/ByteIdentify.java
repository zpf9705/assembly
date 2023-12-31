package top.osjf.assembly.util.data;

import top.osjf.assembly.util.serial.SerialUtils;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Arrays;
import java.util.Objects;

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

    public ByteIdentify(byte[] var) {
        super(var);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int compareTo(@NotNull ByteIdentify o) {
        byte[] compare = o.getData();
        byte[] byCompare = this.getData();
        int compareTo;
        boolean equals = Arrays.equals(compare, byCompare);
        if (equals) {
            compareTo = 0;
        } else {
            Object d0 = SerialUtils.deserialize(compare);
            Object d0by = SerialUtils.deserialize(byCompare);
            if (d0by == null || d0 == null) {
                compareTo = -1;
            } else {
                if (!Objects.equals(d0by.getClass().getName(), d0.getClass().getName())) {
                    compareTo = -1;
                } else {
                    if (!(d0by instanceof String)) {
                        if (!(d0by instanceof Comparable)) {
                            compareTo = -1;
                        } else {
                            compareTo = ((Comparable) d0).compareTo(d0by);
                        }
                    } else {
                        String d0s = (String) d0;
                        String d0bys = (String) d0by;
                        // %% / %- / -%
                        if (d0s.startsWith(d0bys)
                                || d0s.endsWith(d0bys)
                                || d0s.contains(d0bys)) {
                            compareTo = 1;
                        } else {
                            compareTo = -1;
                        }
                    }
                }
            }
        }
        return compareTo;
    }

    @Override
    public String toString() {
        return String.format("Byte array = %s , real value = %s", Arrays.toString(getData()),
                SerialUtils.deserialize(getData()));
    }
}
