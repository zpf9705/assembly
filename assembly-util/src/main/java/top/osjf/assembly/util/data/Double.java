package top.osjf.assembly.util.data;

/**
 * Double object encapsulation mode.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class Double<V1, V2> extends Single<V1> {

    private static final long serialVersionUID = 6107071398688956138L;

    private V2 v2;

    public Double() {
        super();
    }

    public Double(V2 v2) {
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
}
