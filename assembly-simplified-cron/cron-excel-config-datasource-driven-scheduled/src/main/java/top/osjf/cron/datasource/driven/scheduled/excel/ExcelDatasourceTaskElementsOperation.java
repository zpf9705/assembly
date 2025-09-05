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


package top.osjf.cron.datasource.driven.scheduled.excel;

import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.external.file.ExternalFileDatasourceTaskElementsOperation;

/**
 * Abstract class for excel-configured of scheduled task datasource operation.
 *
 * <p>This class implements {@link DatasourceTaskElementsOperation} and provides excel-based task configuration
 * management.
 * Key features include:
 * <ul>
 *   <li>Loading task configurations from excel files</li>
 *   <li>Parsing/serializing configurations using EasyExcel</li>
 *   <li>Managing persistent configuration updates</li>
 *   <li>Providing interfaces for task element cleanup and retrieval</li>
 * </ul>
 * Task configurations are encapsulated by {@link ExcelTaskElement}, enabling runtime configuration
 * batch updates.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class ExcelDatasourceTaskElementsOperation
        extends ExternalFileDatasourceTaskElementsOperation<ExcelTaskElement> {

    /**
     * Constructs an empty {@code ExcelDatasourceTaskElementsOperation} and init
     * an {@link ExcelTaskElementLoader} instance.
     */
    public ExcelDatasourceTaskElementsOperation() {
        super(new ExcelTaskElementLoader());
    }

    @Override
    public ExcelTaskElementLoader getLoader() {
        return (ExcelTaskElementLoader) super.getLoader();
    }
}
