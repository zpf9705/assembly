package top.osjf.assembly.util.data;

import java.util.Objects;

/**
 * The encapsulation mode of six objects.
 * @param <V1> One parameter.
 * @param <V2> Two parameter.
 * @param <V3> Three parameter.
 * @param <V4> Four parameter.
 * @param <V5> Five parameter.
 * @param <V6> Six parameter.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class Sixfold<V1, V2, V3, V4, V5, V6> extends Fivefold<V1, V2, V3, V4, V5> {

    private static final long serialVersionUID = 6202478729301216928L;

    private Fivefold<V1, V2, V3, V4, V5> fivefold;

    private V6 v6;

    public Sixfold() {
    }

    public Sixfold(Fivefold<V1, V2, V3, V4, V5> fivefold) {
        this(fivefold, null);
    }

    public Sixfold(V6 v6) {
        this.v6 = v6;
    }

    public Sixfold(Fivefold<V1, V2, V3, V4, V5> fivefold, V6 v6) {
        Objects.requireNonNull(fivefold);
        this.fivefold = fivefold;
        setSingle(fivefold.getSingle());
        setDouble(fivefold.getDouble());
        setTriple(fivefold.getTriple());
        setQuadruple(fivefold.getQuadruple());
        setV1(fivefold.getV1());
        setV2(fivefold.getV2());
        setV3(fivefold.getV3());
        setV4(fivefold.getV4());
        setV5(fivefold.getV5());
        this.v6 = v6;
    }

    public Sixfold(V5 v5, V6 v6) {
        super(v5);
        this.v6 = v6;
    }

    public Sixfold(V4 v4, V5 v5, V6 v6) {
        super(v4, v5);
        this.v6 = v6;
    }

    public Sixfold(V3 v3, V4 v4, V5 v5, V6 v6) {
        super(v3, v4, v5);
        this.v6 = v6;
    }

    public Sixfold(V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) {
        super(v2, v3, v4, v5);
        this.v6 = v6;
    }

    public Sixfold(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) {
        super(v1, v2, v3, v4, v5);
        this.v6 = v6;
    }

    public V6 getV6() {
        return v6;
    }

    public void setV6(V6 v6) {
        this.v6 = v6;
    }

    public boolean isNotNull() {
        return v6 != null;
    }

    public boolean isChinNotNull() {
        return super.isChinNotNull() && isNotNull();
    }

    public Fivefold<V1, V2, V3, V4, V5> getFivefold() {
        return fivefold;
    }

    public void setFivefold(Fivefold<V1, V2, V3, V4, V5> fivefold) {
        this.fivefold = fivefold;
    }

    public Fivefold<V1, V2, V3, V4, V5> getSelfFivefold() {
        return this;
    }

    public static <V1, V2, V3, V4, V5, V6> Sixfold<V1, V2, V3, V4, V5, V6> emptySixfold() {
        return new Sixfold<>();
    }

    public static <V1, V2, V3, V4, V5, V6> Sixfold<V1, V2, V3, V4, V5, V6> ofSixfold(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5,
                                                                                     V6 v6) {
        return new Sixfold<>(v1, v2, v3, v4, v5, v6);
    }

    public static <V1, V2, V3, V4, V5, V6> Sixfold<V1, V2, V3, V4, V5, V6> ofSixfold(Fivefold<V1, V2, V3, V4, V5>
                                                                                             fivefold, V6 v6) {
        return new Sixfold<>(fivefold, v6);
    }

    public static <V1, V2, V3, V4, V5, V6> Sixfold<V1, V2, V3, V4, V5, V6> ofSixfoldWithFiveFold(V1 v1, V2 v2, V3 v3,
                                                                                                 V4 v4, V5 v5, V6 v6) {
        return ofSixfold(ofFivefoldWithQuadruple(v1, v2, v3, v4, v5), v6);
    }

    @Override
    public String toString() {
        return toString0(getV1(), getV2(), getV3(), getV4(), getV5(), v6);
    }
}
