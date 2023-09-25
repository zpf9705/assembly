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
        T compare = o.getData();
        T byCompare = getData();
        int compareValue;
        if (!compare.getClass().getName().equals(byCompare.getClass().getName())) {
            compareValue = -1;
        } else {
            if (byCompare instanceof byte[]) {
                throw new UnsupportedOperationException("Please refer to top.osjf.assembly.util.data.ByteIdentify");
            } else {
                if (compare instanceof String) {
                    String d0s = (String) compare;
                    String d0bys = (String) byCompare;
                    // %% / %- / -%
                    if (d0bys.startsWith(d0s)
                            || d0bys.endsWith(d0s)
                            || d0bys.contains(d0s)) {
                        compareValue = 1;
                    } else {
                        compareValue = -1;
                    }
                } else {
                    if (compare instanceof Comparable) {
                        compareValue = ((Comparable) byCompare).compareTo(compare);
                    } else {
                        compareValue = -1;
                    }
                }
            }
        }
        return compareValue;
    }
}
