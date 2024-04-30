package top.osjf.assembly.simplified.cache.sql;

import top.osjf.assembly.simplified.cache.DefaultExchange;

/**
 * Default Impl for {@link SqlExchange}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public class DefaultSqlExchange extends DefaultExchange implements SqlExchange {

    private Integer updateCount;

    public DefaultSqlExchange(String value) {
        this(value, null);
    }

    public DefaultSqlExchange(String value, Integer updateCount) {
        super(value);
        this.updateCount = updateCount;
    }

    @Override
    public void setUpdateCount(Integer updateCount) {
        this.updateCount = updateCount;
    }

    @Override
    public Integer getUpdateCount() {
        return updateCount;
    }

    @Override
    public boolean result() {
        return getUpdateCount() != null && getUpdateCount() > 0;
    }
}
