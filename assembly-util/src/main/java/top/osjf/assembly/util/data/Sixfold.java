package top.osjf.assembly.util.data;

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

    private V6 v6;

    public Sixfold() {
    }

    public Sixfold(V6 v6) {
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
}
