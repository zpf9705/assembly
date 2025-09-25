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


package top.osjf.cron.core.repository;

import top.osjf.cron.core.lang.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class AbstractCronTaskRepository
        extends AbstractRunTimeoutRegistrarRepository implements CronTaskRepository {

    /**
     * Return remaining number of runs of the specify id task.
     * @param taskId the specify task id.
     * @return Remaining number of runs of the specify id task,
     * the unlimited number of times is {@code -1}, and there
     * are no tasks with {@code 0}. Otherwise, it is the remaining
     * number of runs.
     * @since 3.0.1
     */
    protected long getTaskRemainingNumberOfRuns(String taskId) {
        AtomicInteger count = getTaskRunTimesMap().getOrDefault(taskId, null);
        return count == null ? hasCronTaskInfo(taskId) ? -1 : 0 : count.get();
    }

    /**
     * Customize a specified {@link CronTaskInfo}.
     * @param cronTaskInfo a specified {@link CronTaskInfo}.
     * @return a specified {@link CronTaskInfo}.
     * @since 3.0.1
     */
    @Nullable
    protected CronTaskInfo customizeCronTaskInfo(CronTaskInfo cronTaskInfo) {
        if (cronTaskInfo == null) {
            return null;
        }
        // Setting remaining number of runs.
        cronTaskInfo.setRemainingNumberOfRuns(getTaskRemainingNumberOfRuns(cronTaskInfo.getId()));

        return cronTaskInfo;
    }
}
