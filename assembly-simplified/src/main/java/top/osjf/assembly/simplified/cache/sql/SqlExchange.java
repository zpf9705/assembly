package top.osjf.assembly.simplified.cache.sql;

import top.osjf.assembly.simplified.cache.Exchange;

/**
 * The information interface for SQL related execution changes.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public interface SqlExchange extends Exchange {

    /**
     * Set the impact value of SQL updates, deletions,
     * and creations on the number of products.
     *
     * @param updateCount the impact value of SQL updates, deletions, and
     *                    creations on the number of products.
     */
    void setUpdateCount(Integer updateCount);

    /**
     * Returns the impact value of SQL updates, deletions,
     * and creations on the number of products.
     *
     * @return the impact value of SQL updates, deletions, and
     * creations on the number of products.
     */
    Integer getUpdateCount();

    /**
     * Returns whether SQL execution has affected the current table data.
     * @return if {@code true} affected its table data,{@code false} otherwise.
     */
    @Override
    boolean result();
}
