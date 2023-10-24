package top.osjf.assembly.util.data;

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

    private V4 v4;

    public Quadruple() {
        super();
    }

    public Quadruple(V4 v4) {
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

    public Triple<V1, V2, V3> getTriple() {
        return this;
    }

    public static <V1, V2, V3, V4> Quadruple<V1, V2, V3, V4> emptyQuadruple() {
        return new Quadruple<>();
    }

    public static <V1, V2, V3, V4> Quadruple<V1, V2, V3, V4> ofQuadruple(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new Quadruple<>(v1, v2, v3, v4);
    }
}
