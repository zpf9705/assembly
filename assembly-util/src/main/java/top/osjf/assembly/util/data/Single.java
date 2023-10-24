package top.osjf.assembly.util.data;

import java.io.Serializable;

/**
 * The encapsulation mode of a single object.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @param <V1> One parameter.
 * @since 1.0.4
 */
public class Single<V1> implements Serializable {
    private static final long serialVersionUID = 3978229730331192998L;

    private V1 v1;

    public Single() {
        this(null);
    }

    public Single(V1 v1) {
        this.v1 = v1;
    }

    public V1 getV1() {
        return v1;
    }

    public void setV1(V1 v1) {
        this.v1 = v1;
    }
}
