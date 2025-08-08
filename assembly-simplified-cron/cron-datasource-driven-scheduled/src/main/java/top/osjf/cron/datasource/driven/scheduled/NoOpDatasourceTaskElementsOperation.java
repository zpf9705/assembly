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


package top.osjf.cron.datasource.driven.scheduled;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * A basic, non-operational {@link DatasourceTaskElementsOperation} implementation is
 * used to disable data source acquisition task, typically used to support data source
 * acquisition task operation without an actual backup operation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class NoOpDatasourceTaskElementsOperation implements DatasourceTaskElementsOperation {

    @Override
    public void purgeDatasourceTaskElements() {
        // do noting.
    }

    @Override
    public List<TaskElement> getDatasourceTaskElements() {
        return Collections.emptyList();
    }

    @Override
    public void afterStart(List<TaskElement> fulledDatasourceTaskElement) {
        // do noting.
    }

    @Override
    public List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {
        return Collections.emptyList();
    }

    @Override
    public void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement) {
        // do noting.
    }

    @Nullable
    @Override
    public TaskElement getElementById(String id) {
        return null;
    }

    @Override
    public String toString() {
        return " Non operational datasourceTaskElementsOperation implementation class. ";
    }
}
