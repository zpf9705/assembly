package top.osjf.assembly.util.data;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * Expand the functionality of interface {@link Comparable} to convert results greater than
 * {@code 0} to {@code true} output, and results less than {@code 0} to {@code false} output.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface ComparableBool<T> extends Comparable<T> {

    @Override
    int compareTo(@NotNull T o);

    default boolean compareToReturnsBool(T o) {
        int to = compareTo(o);
        return to > 0 || to == 0;
    }
}
