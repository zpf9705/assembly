package top.osjf.assembly.util.data;

/**
 * The encapsulation mode of five objects.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
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
}
