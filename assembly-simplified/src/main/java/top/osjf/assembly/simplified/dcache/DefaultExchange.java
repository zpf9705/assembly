package top.osjf.assembly.simplified.dcache;

/**
 * Default Impl for {@link Exchange}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public class DefaultExchange implements Exchange {

    private final String value;

    public DefaultExchange(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
