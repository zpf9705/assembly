package top.osjf.assembly.util.data;

import top.osjf.assembly.util.annotation.NotNull;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The authentication object of byte numbers mainly solves the problem of byte serialization address
 * values changing.<br>
 * Rewrite the {@link #hashCode()} method to mask address value verification and instead add byte
 * array values to obtain a new {@code hashCode} value.<br>
 * Rewritten the {@link #equals(Object)} method to determine that the value of {@link ByteIdentify}
 * remains consistent only when the tangent is lower than {@code hashcode}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class ByteIdentify implements ComparableBool<ByteIdentify>, Serializable {

    private static final long serialVersionUID = -851217802015189183L;

    private byte[] var;

    public ByteIdentify(byte[] var) {
        if (var == null) {
            throw new IllegalArgumentException("ByteIdentify parse var must no be null");
        }
        this.var = var;
    }

    public byte[] getVar() {
        return var;
    }

    public void setVar(byte[] var) {
        this.var = var;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        for (byte b : var) {
            hashcode += b;
        }
        return hashcode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ByteIdentify)) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public int compareTo(@NotNull ByteIdentify o) {
        int compare;
        String s0 = Arrays.toString(o.var)
                .replace("[", "")
                .replace("]", "");
        String s01 = Arrays.toString(this.var)
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
                "var=" + Arrays.toString(var) +
                '}';
    }
}
