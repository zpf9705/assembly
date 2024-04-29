package top.osjf.assembly.simplified.cache.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.parser.Token;
import top.osjf.assembly.simplified.cache.CacheContextSupport;
import top.osjf.assembly.simplified.cache.DefaultExchange;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.lang.StringUtils;

/**
 * Using the execution extension of the data source of Druid,
 * listen to the current updated, deleted, and added SQL statements,
 * obtain their main table names, mark them in the current thread context,
 * and wait for subsequent cache update processing.
 *
 * <p>The monitored methods include single execution, batch execution,
 * and updates, and all motivations occur after execution to capture
 * and listen. The premise is to ensure successful execution before
 * operating on cache changes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public class CacheDruidFilterEvent extends FilterEventAdapter {

    @CanNull
    private DbType dbType;

    @Override
    public void init(DataSourceProxy dataSource) {
        String dbTypeName = dataSource.getDbType();
        if (StringUtils.isNotBlank(dbTypeName)) {
            dbType = DbType.of(dbTypeName);
        }
        if (dbType == null) {
            String url = dataSource.getUrl();
            if (StringUtils.isNotBlank(url)) {
                for (DbType type : DbType.values()) {
                    if (url.contains(type.name())) {
                        dbType = type;
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        recordValueChange(sql, statement, true);
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        recordValueChange(sql, statement, false);
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        recordValueChange(statement.getBatchSql(), statement, false);
    }

    /**
     * Record the changes in the current SQL execution main table and record them.
     * @param sql        Executed SQL.
     * @param statement  SQL information related to the proxy.
     * @param checkQuery Check if it is a query operation.
     */
    private void recordValueChange(String sql, @SuppressWarnings("unused") StatementProxy statement, boolean checkQuery) {
        if (StringUtils.isBlank(sql)) return;
        if (checkQuery) {
            if (sql.startsWith(Token.SELECT.name)
                    || sql.startsWith(Token.SELECT.name.toLowerCase())) {
                return;
            }
        }
        String operationTableName = FilterEventSupport.getOperationTableName(sql, dbType);
        if (StringUtils.isBlank(operationTableName)) {
            return;
        }
        CacheContextSupport.addCurrentExchange(new DefaultExchange(operationTableName));
    }
}
