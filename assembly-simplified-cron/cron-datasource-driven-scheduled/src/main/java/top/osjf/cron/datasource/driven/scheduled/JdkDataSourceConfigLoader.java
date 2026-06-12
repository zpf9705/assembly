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

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Specialized intermediate interface for configuration loader based on standard JDBC DataSource.
 * As an extended sub-abstraction of {@link DataSourceConfigLoader}, this interface restricts all
 * implementations that read configurations from database tables via JDBC {@link DataSource}.
 *
 * <p>The top-level parent interface defines unified behavior for configuration loading, while this
 * intermediate interface encapsulates exclusive capabilities for database query scenarios.
 * It decouples general configuration specifications from database data source capabilities, complying
 * with Single Responsibility Principle & Open/Closed Principle, and facilitates differentiation
 * between database implementations and other configuration sources such as config centers or local files.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface JdkDataSourceConfigLoader extends ConfigurableDataSourceConfigLoader {

    /**
     * Retrieve the configuration text value stored in database by unique configuration key.
     * Overrides the standard loading method of top-level parent interface, executes predefined query SQL
     * via the JDBC DataSource held by current interface to complete configuration query.
     * Returns {@code null} if no record matching the given configKey exists in database table; throws
     * {@code SQLException} for database link failure, invalid SQL syntax or column read errors.
     *
     * @param configKey the unique identifier key of configuration item.
     * @return Matched configuration string from database; returns {@code null} when no matching record exists.
     * @throws SQLException Thrown when database access fails, including connection acquisition failure,
     * SQL execution error, result set read exception, missing target column and other scenarios.
     */
    @Nullable
    @Override
    String getConfig(String configKey) throws SQLException;

    /**
     * Returns standard {@link DataSource JDBC DataSource instance} used for querying configuration data.
     * @return a valid JDBC DataSource instance.
     */
    DataSource getDataSource();

    /**
     * Returns the predefined SQL statement for querying configuration items.
     * The SQL template must reserve one parameter placeholder to bind {@code configKey}
     * filter condition, example: <pre>{@code SELECT config_value FROM t_sys_config WHERE config_key = ?}</pre>
     *
     * @return the Complete executable SQL template string for configuration query.
     */
    String getQueryConfigSQL();

    /**
     * Dynamically specify column name which stores configuration values in database table.
     * Designed to adapt differentiated column naming of various business configuration tables
     * (e.g. val, cfg_value, config_val). Configuration text will be extracted from result set
     * via this column name during query execution.
     *
     * @param configValueColumnName  the database column name corresponding to configuration value。
     *
     */
    void setConfigValueColumnName(String configValueColumnName);
}
