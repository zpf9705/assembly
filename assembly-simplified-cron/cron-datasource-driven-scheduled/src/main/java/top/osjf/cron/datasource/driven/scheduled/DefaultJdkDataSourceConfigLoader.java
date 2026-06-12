/*
 * Copyright 2026-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package top.osjf.cron.datasource.driven.scheduled;

import org.intellij.lang.annotations.Language;
import top.osjf.cron.core.util.AssertUtils;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The default JDBC implementation of {@link DataSourceConfigLoader}, which loads configuration records
 * from relational databases via standard JDBC DataSource.
 *
 * <ul>
 *     <li>
 *         Auto manage JDBC resources, use try-with-resources to close all AutoCloseable objects automatically,
 *         avoid connection/cursor leak
 *     </li>
 *     <li>
 *         Fully compatible with JDBC connection pool. Connection.close() just return connection to pool instead
 *         of closing physical TCP link
 *     </li>
 *     <li>
 *         Support custom config value column name for different table schema
 *     </li>
 *     <li>
 *         The input sql must contain exactly one '?' placeholder to bind config key parameter
 *     </li>
 * </ul>
 *
 * <p>Usage Example：
 * <pre>{@code
 * SELECT config_key, CONFIG_VALUE FROM t_system_config WHERE config_key = ?
 * }</pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultJdkDataSourceConfigLoader implements JdkDataSourceConfigLoader {

    /**
     * Default column name of config value in ResultSet (uppercase column standard)
     */
    public static final String CONFIG_VALUE_COLUMN_NAME = "CONFIG_VALUE";

    /**
     * Database DataSource instance, usually a connection pool object
     */
    private final DataSource dataSource;

    /**
     * SQL statement for query config, must contain exactly one '?' placeholder for config key binding
     */
    @Language("SQL") private final String queryConfigSql;

    /**
     * Custom column name for config value, default value is {@link #CONFIG_VALUE_COLUMN_NAME}
     */
    private String configValueColumnName = CONFIG_VALUE_COLUMN_NAME;

    /**
     * Constructs a new {@link DefaultJdkDataSourceConfigLoader} with given {@link DataSource}
     * and {@code queryConfigSql}.
     * @param dataSource     Database resource instance object.
     * @param queryConfigSql Query sql string, cannot be blank, must contain one '?' placeholder
     */
    public DefaultJdkDataSourceConfigLoader(DataSource dataSource, @Language("SQL") String queryConfigSql) {
        AssertUtils.assertNotNull(dataSource, "javax.sql.DataSource must not be null");
        AssertUtils.assertNotBlank(queryConfigSql, "queryConfigSql must not be blank");
        this.dataSource = dataSource;
        this.queryConfigSql = queryConfigSql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfigValueColumnName(String configValueColumnName) {
        AssertUtils.assertNotBlank(configValueColumnName, "configValueColumnName must not be blank");
        this.configValueColumnName = configValueColumnName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQueryConfigSQL() {
        return queryConfigSql;
    }

    /**
     * {@inheritDoc}
     *
     * This default implementation is read-only and does not support writing configurations.
     *
     * <p>Implement {@link ConfigurableDataSourceConfigLoader} with custom write logic if create/update
     * config is required.
     */
    @Override
    public void setConfig(String configKey, String configValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @throws java.sql.SQLException if a database access error occurs
     */
    @Nullable
    @Override
    public String getConfig(String configKey) throws java.sql.SQLException {

        // Auto close connection & prepared statement ...
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(queryConfigSql)) {

            // Bind config key parameter to sql placeholder ...
            ps.setString(1, configKey);

            // Execute query, result set auto closed ...
            try (ResultSet resultSet = ps.executeQuery()) {

                // Move cursor to check record exists, return null if no data to avoid
                // "Before start of result set" exception
                return resultSet.next() ? resultSet.getString(configValueColumnName) : null;
            }
        }
    }
}
