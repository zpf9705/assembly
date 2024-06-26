package top.osjf.assembly.simplified.cache;

import java.util.Objects;

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

    @Override
    public boolean result() {
        return true;
    }

    @Override
    public void ifSetResult() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultExchange that = (DefaultExchange) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
