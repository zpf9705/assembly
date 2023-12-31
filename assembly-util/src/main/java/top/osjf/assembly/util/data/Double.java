package top.osjf.assembly.util.data;

import java.util.Objects;

/**
 * Double object encapsulation mode.
 * @param <V1> One parameter.
 * @param <V2> Two parameter.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class Double<V1, V2> extends Single<V1> {

    private static final long serialVersionUID = 6107071398688956138L;

    private Single<V1> single;

    private V2 v2;

    public Double() {
        super();
    }

    public Double(Single<V1> single) {
        this(single, null);
    }

    public Double(V2 v2) {
        this.v2 = v2;
    }

    public Double(Single<V1> single, V2 v2) {
        Objects.requireNonNull(single);
        this.single = single;
        setV1(single.getV1());
        this.v2 = v2;
    }


    public Double(V1 v1, V2 v2) {
        super(v1);
        this.v2 = v2;
    }

    public V2 getV2() {
        return v2;
    }

    public void setV2(V2 v2) {
        this.v2 = v2;
    }

    public boolean isNotNull() {
        return v2 != null;
    }

    public boolean isChinNotNull() {
        return super.isNotNull() && isNotNull();
    }

    public void setSingle(Single<V1> single) {
        this.single = single;
        setV1(single.getV1());
    }

    public Single<V1> getSingle() {
        return single;
    }

    public Single<V1> getSelfSingle() {
        return this;
    }

    public static <V1, V2> Double<V1, V2> emptyDouble() {
        return new Double<>();
    }

    public static <V1, V2> Double<V1, V2> ofDouble(V1 v1, V2 v2) {
        return new Double<>(v1, v2);
    }

    public static <V1, V2> Double<V1, V2> ofDouble(Single<V1> single, V2 v2) {
        return new Double<>(single, v2);
    }

    public static <V1, V2> Double<V1, V2> ofDoubleWithSingle(V1 v1, V2 v2) {
        return ofDouble(ofSingle(v1), v2);
    }

    @Override
    public String toString() {
        return toString0(getV1(), v2);
    }
}
