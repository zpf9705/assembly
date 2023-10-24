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

    private V3 v3;

    public Triple() {
        super();
    }

    public Triple(V3 v3) {
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
}
