package top.osjf.assembly.util.data;

/**
 * The encapsulation mode of five objects.
 * @param <V1> One parameter.
 * @param <V2> Two parameter.
 * @param <V3> Three parameter.
 * @param <V4> Four parameter.
 * @param <V5> Five parameter.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class Fivefold<V1, V2, V3, V4, V5> extends Quadruple<V1, V2, V3, V4> {

    private static final long serialVersionUID = -4309128249184547633L;

    private V5 v5;

    public Fivefold() {
        super();
    }

    public Fivefold(V5 v5) {
        this.v5 = v5;
    }

    public Fivefold(V4 v4, V5 v5) {
        super(v4);
        this.v5 = v5;
    }

    public Fivefold(V3 v3, V4 v4, V5 v5) {
        super(v3, v4);
        this.v5 = v5;
    }

    public Fivefold(V2 v2, V3 v3, V4 v4, V5 v5) {
        super(v2, v3, v4);
        this.v5 = v5;
    }

    public Fivefold(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
        super(v1, v2, v3, v4);
        this.v5 = v5;
    }

    public V5 getV5() {
        return v5;
    }

    public void setV5(V5 v5) {
        this.v5 = v5;
    }

    public boolean isNotNull() {
        return v5 != null;
    }

    public Quadruple<V1, V2, V3, V4> getQuadruple() {
        return this;
    }

    public static <V1, V2, V3, V4, V5> Fivefold<V1, V2, V3, V4, V5> emptyFivefold() {
        return new Fivefold<>();
    }

    public static <V1, V2, V3, V4, V5> Fivefold<V1, V2, V3, V4, V5> ofFivefold(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
        return new Fivefold<>(v1, v2, v3, v4, v5);
    }
}
