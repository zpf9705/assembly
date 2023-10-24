package top.osjf.assembly.util.data;

import java.io.Serializable;

/**
 * The encapsulation mode of a single object.
 * @param <V1> One parameter.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class Single<V1> implements Serializable {
    private static final long serialVersionUID = 3978229730331192998L;

    private V1 v1;

    public Single() {
        this(null);
    }

    public Single(V1 v1) {
        this.v1 = v1;
    }

    public V1 getV1() {
        return v1;
    }

    public void setV1(V1 v1) {
        this.v1 = v1;
    }

    public boolean isNotNull() {
        return v1 != null;
    }

    public static <V1> Single<V1> emptySingle() {
        return new Single<>();
    }

    public static <V1> Single<V1> ofSingle(V1 v1) {
        return new Single<>(v1);
    }

    @SafeVarargs
    public final <S, T> String toString0(T... args) {
        StringBuilder builder = new StringBuilder(this.getClass().getSimpleName() + " : ");
        int index = 1;
        for (T arg : args) {
            builder.append("V").append(index).append(" = ").append(arg).append(" ");
            index++;
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return toString0(v1);
    }
}
