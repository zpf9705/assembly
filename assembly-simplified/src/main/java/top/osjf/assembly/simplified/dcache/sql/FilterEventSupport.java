package top.osjf.assembly.simplified.dcache.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Support for {@link CacheDruidFilterEvent}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public final class FilterEventSupport {

    /*** Keep the changes made to the current thread.*/
    private static final Pattern DEFAULT_BOTTOM_PATTERN =
            Pattern.compile("\\b(?:SELECT\\b.*?\\bFROM\\b\\s+(\\w+)|INSERT\\b.*?\\bINTO\\b\\s+(\\w+)|UPDATE\\b\\s+(\\w+)|DELETE\\b.*?\\bFROM\\b\\s+(\\w+))\\b",
                    Pattern.CASE_INSENSITIVE);

    /* Use the table name retrieval tool provided by Druid to indicate retrieval.
    If it cannot be obtained, use regularization.*/
    public static String getOperationTableName(String sql, DbType dbType) {
        if (dbType != null) {
            List<String> tableNames = SQLParserUtils.getTables(sql, dbType);
            if (CollectionUtils.isNotEmpty(tableNames)) {
                return tableNames.get(0);
            }
        }
        return getOperationTableNameUseDefaultBottomPattern(sql);
    }

    /* Use the default regular expression to obtain the table name.*/
    public static String getOperationTableNameUseDefaultBottomPattern(String sql) {
        Matcher matcher = DEFAULT_BOTTOM_PATTERN.matcher(sql);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String tableName = matcher.group(i);
                if (StringUtils.isNotBlank(tableName)) {
                    return tableName;
                }
            }
        }
        return null;
    }
}
