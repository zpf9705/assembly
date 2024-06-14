package top.osjf.assembly.util.data;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.SimilarAble;

import java.util.Objects;

/**
 * The generic type of {@link Identify} is the {@link Object} implementation class.
 *
 * <p>Rewritten {@link #compareTo(ObjectIdentify)} method requires consistent class and
 * requires {@link Comparable} to be implemented, calling its own method to rewrite logic.
 *
 * <p>The {@link String} type is handled by a separate API.</p>
 *
 * @author zpf
 * @since 1.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ObjectIdentify<T> extends Identify<T, ObjectIdentify<T>> {

    private static final long serialVersionUID = -8542006961214155172L;

    public ObjectIdentify(T data) {
        super(data);
    }

    @Override
    public int compareTo(@NotNull ObjectIdentify<T> o) {
        T data = getData();
        T dataChallenge = o.getData();
        if (Objects.equals(data, dataChallenge)) {
            return 0;
        }
        if (data instanceof Comparable) {
            return ((Comparable) data).compareTo(dataChallenge);
        }
        return -1;
    }

    @Override
    public boolean similarTo(ObjectIdentify<T> o) {
        T data = getData();
        T dataChallenge = o.getData();
        if (data instanceof SimilarAble) {
            //Note type cast exceptions.
            return ((SimilarAble) data).similarTo(dataChallenge);
        }
        return super.similarTo(data, dataChallenge);
    }
}
