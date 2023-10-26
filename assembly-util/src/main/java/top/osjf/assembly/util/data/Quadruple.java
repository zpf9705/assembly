package top.osjf.assembly.util.data;

import java.util.Objects;

/**
 * The encapsulation mode of four objects.
 * @param <V1> One parameter.
 * @param <V2> Two parameter.
 * @param <V3> Three parameter.
 * @param <V4> Four parameter.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class Quadruple<V1, V2, V3, V4> extends Triple<V1, V2, V3> {

    private static final long serialVersionUID = -8796183124403343408L;

    private Triple<V1, V2, V3> triple;

    private V4 v4;

    public Quadruple() {
        super();
    }

    public Quadruple(Triple<V1, V2, V3> triple) {
        this(triple, null);
    }

    public Quadruple(V4 v4) {
        this.v4 = v4;
    }

    public Quadruple(Triple<V1, V2, V3> triple, V4 v4) {
        Objects.requireNonNull(triple);
        this.triple = triple;
        setSingle(triple.getSingle());
        setDouble(triple.getDouble());
        setV1(triple.getV1());
        setV2(triple.getV2());
        setV3(triple.getV3());
        this.v4 = v4;
    }

    public Quadruple(V3 v3, V4 v4) {
        super(v3);
        this.v4 = v4;
    }

    public Quadruple(V2 v2, V3 v3, V4 v4) {
        super(v2, v3);
        this.v4 = v4;
    }

    public Quadruple(V1 v1, V2 v2, V3 v3, V4 v4) {
        super(v1, v2, v3);
        this.v4 = v4;
    }

    public V4 getV4() {
        return v4;
    }

    public void setV4(V4 v4) {
        this.v4 = v4;
    }

    public boolean isNotNull() {
        return v4 != null;
    }

    public boolean isChinNotNull() {
        return super.isChinNotNull() && isNotNull();
    }

    public Triple<V1, V2, V3> getTriple() {
        return triple;
    }

    public void setTriple(Triple<V1, V2, V3> triple) {
        this.triple = triple;
    }

    public Triple<V1, V2, V3> getSelfTriple() {
        return this;
    }

    public static <V1, V2, V3, V4> Quadruple<V1, V2, V3, V4> emptyQuadruple() {
        return new Quadruple<>();
    }

    public static <V1, V2, V3, V4> Quadruple<V1, V2, V3, V4> ofQuadruple(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new Quadruple<>(v1, v2, v3, v4);
    }

    public static <V1, V2, V3, V4> Quadruple<V1, V2, V3, V4> ofQuadruple(Triple<V1, V2, V3> triple, V4 v4) {
        return new Quadruple<>(triple, v4);
    }

    public static <V1, V2, V3, V4> Quadruple<V1, V2, V3, V4> ofQuadrupleWithTriple(V1 v1, V2 v2, V3 v3, V4 v4) {
        return ofQuadruple(ofTripleWithDouble(v1, v2, v3), v4);
    }

    @Override
    public String toString() {
        return toString0(getV1(), getV2(), getV3(), v4);
    }
}
