package top.osjf.assembly.util.data;

import top.osjf.assembly.util.SerialUtils;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * The generic type of {@link Identify} is the {@code byte[]} implementation class.
 *
 * <p>The authentication object of byte numbers mainly solves the problem of byte
 * serialization address values changing.
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
        byte[] byCompare = getData();
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
                            compareTo = ((Comparable) d0by).compareTo(d0);
                        }
                    } else {
                        String d0s = (String) d0;
                        String d0bys = (String) d0by;
                        // %% / %- / -%
                        if (d0bys.startsWith(d0s)
                                || d0bys.endsWith(d0s)
                                || d0bys.contains(d0s)) {
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
