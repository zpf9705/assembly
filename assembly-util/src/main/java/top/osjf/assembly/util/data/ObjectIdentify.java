package top.osjf.assembly.util.data;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * The generic type of {@link Identify} is the {@link Object} implementation class.
 *
 * <p>Rewritten {@link #compareTo(ObjectIdentify)} method requires consistent class and
 * requires {@link Comparable} to be implemented, calling its own method to rewrite logic.
 *
 * @author zpf
 * @since 1.0.0
 */
public class ObjectIdentify<T> extends Identify<T, ObjectIdentify<T>> {

    private static final long serialVersionUID = -8542006961214155172L;

    public ObjectIdentify(T data) {
        super(data);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int compareTo(@NotNull ObjectIdentify<T> o) {
        T byCompare = o.getData();
        T compare = getData();
        if (!compare.getClass().getName().equals(byCompare.getClass().getName())) {
            return -1;
        }
        int compareValue;
        if (compare instanceof Comparable) {
            compareValue = ((Comparable) byCompare).compareTo(compare);
        } else if (compare instanceof byte[]) {
            throw new UnsupportedOperationException("Please refer to top.osjf.assembly.util.data.ByteIdentify");
        } else {
            throw new UnsupportedOperationException("Unsupported data type");
        }
        return compareValue;
    }
}
