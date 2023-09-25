package top.osjf.assembly.util.data;

import top.osjf.assembly.util.annotation.NotNull;

import java.util.Arrays;

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
    public int compareTo(@NotNull ByteIdentify o) {
        int compare;
        String s0 = Arrays.toString(o.getData())
                .replace("[", "")
                .replace("]", "");
        String s01 = Arrays.toString(getData())
                .replace("[", "")
                .replace("]", "");
        int s0l = s0.length();
        int s01l = s01.length();
        if (s0.contains(s01)) {
            if (s0l == s01l) {
                compare = 0;
            } else {
                if (s0l > s01l) {
                    compare = 1;
                } else {
                    compare = 2;
                }
            }
        } else {
            compare = -1;
        }
        return compare;
    }

    @Override
    public String toString() {
        return "ByteContain{" +
                "var=" + Arrays.toString(getData()) +
                '}';
    }
}
