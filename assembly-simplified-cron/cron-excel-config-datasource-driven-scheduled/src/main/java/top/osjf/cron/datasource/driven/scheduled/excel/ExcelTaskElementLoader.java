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

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.metadata.ReadWorkbook;
import com.alibaba.excel.support.ExcelTypeEnum;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.external.file.ExternalFileTaskElementLoader;

import java.io.InputStream;
import java.util.List;

/**
 * The Yaml loader for {@link ExcelTaskElement} loading.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class ExcelTaskElementLoader extends ExternalFileTaskElementLoader<ExcelTaskElement> {

    /** Default config file named task-config.xlsx */
    private static final String DEFAULT_CONFIG_FILE_NAME = "task-config.xlsx";

    private ExcelTypeEnum excelType;

    @Override
    public void initialize() {
        super.initialize();
        ReadWorkbook readWorkbook = new ReadWorkbook();
        readWorkbook.setFile(getConfigFile());
        excelType = ExcelTypeEnum.valueOf(readWorkbook);
    }

    @Override
    protected void refresh() throws DataSourceDrivenException {
        try {
            EasyExcel.write(getConfigFile())
                    .head(ExcelTaskElement.class).excelType(excelType).sheet().doWrite(taskElements);
        }
        catch (Throwable ex) {
            throw new DataSourceDrivenException("Failed to write file : " + getConfigFile().getPath(), ex);
        }
    }

    @Override
    protected List<ExcelTaskElement> loadingInternal(InputStream is) {
        return EasyExcel.read(is).head(ExcelTaskElement.class).excelType(excelType).sheet().doReadSync();
    }

    @Override
    protected String defaultConfigFileName() {
        return DEFAULT_CONFIG_FILE_NAME;
    }
}
