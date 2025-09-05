/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.spring.autoconfigure.cron.datasouce.driven.scheduled;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.excel.ExcelDatasourceTaskElementsOperation;
import top.osjf.cron.spring.datasource.driven.scheduled.ExcelDatabaseDrivenScheduledConfiguration;

/**
 * {@link Configuration Configuration} for {@link ExcelDatasourceTaskElementsOperation}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2025.09.05
 */
@Configuration(proxyBeanMethods = false)
@Import(ExcelDatabaseDrivenScheduledConfiguration.class)
@ConditionalOnClass(ExcelDatasourceTaskElementsOperation.class)
@ConditionalOnMissingBean(DatasourceTaskElementsOperation.class)
@Conditional(DatasourceDrivenCondition.class)
class ExcelDatasourceTaskElementsOperationConfiguration {
}
