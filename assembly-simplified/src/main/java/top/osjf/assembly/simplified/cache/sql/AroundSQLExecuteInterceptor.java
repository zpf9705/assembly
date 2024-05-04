package top.osjf.assembly.simplified.cache.sql;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import top.osjf.assembly.simplified.cache.CacheContextSupport;

import java.sql.Statement;

import static top.osjf.assembly.simplified.cache.sql.AroundSQLExecuteInterceptor.BATCH_SQL_TYPE_NAME;
import static top.osjf.assembly.simplified.cache.sql.AroundSQLExecuteInterceptor.UPDATE_SQL_TYPE_NAME;

/**
 * Using the interceptor of Mybatis for SQL interception is mainly
 * to obtain the result of the number of SQL execution impacts
 * through this interception, in order to determine whether the
 * cache needs to be reconfigured.
 *
 * <p>This interceptor is similar to post processing, occurring after
 * {@link CacheDruidFilterEvent} has collected SQL information and
 * supplementing the collected information {@link SqlExchange}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see CacheDruidFilterEvent
 * @since 2.2.4
 */
@Intercepts({@Signature(type = StatementHandler.class, method = UPDATE_SQL_TYPE_NAME, args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = BATCH_SQL_TYPE_NAME, args = {Statement.class})})
public class AroundSQLExecuteInterceptor implements Interceptor {

    /*** The changes of adding, deleting, and updating are collectively referred to as.*/
    public static final String UPDATE_SQL_TYPE_NAME = "update";

    /*** The operation name for the batch operation.*/
    public static final String BATCH_SQL_TYPE_NAME = "batch";
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //Pre execute SQL and collect relevant information about SQL.
        Object proceed = invocation.proceed();

        //Obtain SQL changes for the current execution order.
        SqlExchange exchange = CacheContextSupport.currentOrderExchange();

        //Placement affects the results.
        if (exchange != null && proceed != null) {

            exchange.setUpdateCount((Integer) proceed);
        }

        return proceed;
    }
}