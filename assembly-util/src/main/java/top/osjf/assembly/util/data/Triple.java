package top.osjf.assembly.util.data;

/**
 * The encapsulation mode of three objects.
 * @param <V1> One parameter.
 * @param <V2> Two parameter.
 * @param <V3> Three parameter.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class Triple<V1, V2, V3> extends Double<V1, V2> {

    private static final long serialVersionUID = 2558111638535260492L;

    private Double<V1, V2> doubles;

    private V3 v3;

    public Triple() {
        super();
    }

    public Triple(Double<V1, V2> doubles) {
        this(doubles, null);
    }

    public Triple(V3 v3) {
        this.v3 = v3;
    }

    public Triple(Double<V1, V2> doubles, V3 v3) {
        this.doubles = doubles;
        this.v3 = v3;
    }

    public Triple(V2 v2, V3 v3) {
        super(v2);
        this.v3 = v3;
    }

    public Triple(V1 v1, V2 v2, V3 v3) {
        super(v1, v2);
        this.v3 = v3;
    }

    public V3 getV3() {
        return v3;
    }

    public void setV3(V3 v3) {
        this.v3 = v3;
    }

    public boolean isNotNull() {
        return v3 != null;
    }

    public boolean isChinNotNull() {
        return super.isChinNotNull() && isNotNull();
    }

    public void setDouble(Double<V1, V2> doubles) {
        this.doubles = doubles;
    }

    public Double<V1, V2> getDouble() {
        return doubles;
    }

    public Double<V1, V2> getSelfDouble() {
        return this;
    }

    public static <V1, V2, V3> Triple<V1, V2, V3> emptyTriple() {
        return new Triple<>();
    }

    public static <V1, V2, V3> Triple<V1, V2, V3> ofTriple(V1 v1, V2 v2, V3 v3) {
        return new Triple<>(v1, v2, v3);
    }

    public static <V1, V2, V3> Triple<V1, V2, V3> ofTriple(Double<V1, V2> doubles, V3 v3) {
        return new Triple<>(doubles, v3);
    }

    public static <V1, V2, V3> Triple<V1, V2, V3> ofTripleWithDouble(V1 v1, V2 v2, V3 v3) {
        return ofTriple(ofDoubleWithSingle(v1, v2), v3);
    }

    @Override
    public String toString() {
        return toString0(getV1(), getV2(), v3);
    }
}
