/*
 * Copyright 2024-? the original author or authors.
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


package top.osjf.cron.spring.datasource.driven.scheduled;

/**
 * Define the enumeration types of task information data sources and support different
 * data source implementation methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public enum DataSource {

    /**
     * The task information data source relies on the local configuration file of YAML.
     *
     * @see top.osjf.cron.datasource.driven.scheduled.yaml.YamlDatasourceTaskElementsOperation
     */
    YAML_CONFIG,

    /**
     * The task information data source relies on the ORM mapping of the mybatis plus framework
     * to database queries.
     *
     * @see top.osjf.cron.datasource.driven.scheduled.mp.MybatisPlusDatasourceTaskElementsOperation
     */
    MY_BATIS_PLUS_ORM_DATABASE,

    /**
     * The task information data source relies on the ORM mapping of the spring jpa framework
     * to database queries.
     *
     * @see top.osjf.cron.datasource.driven.scheduled.jpa.JpaDatasourceTaskElementsOperation
     */
    SPRING_JPA_ORM_DATABASE,

    /**
     * The task information data source relies on the local configuration file of Excel.
     *
     * @see top.osjf.cron.datasource.driven.scheduled.excel.ExcelDatasourceTaskElementsOperation
     */
    EXCEL_CONFIG
}
