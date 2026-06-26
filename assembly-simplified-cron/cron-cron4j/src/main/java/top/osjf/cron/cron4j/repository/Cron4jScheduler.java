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


package top.osjf.cron.cron4j.repository;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;
import top.osjf.commons.lang.Nullable;

/**
 * Based on the original {@link Scheduler} extended scheduler, compatible with task
 * scheduling related operations.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
class Cron4jScheduler extends Scheduler {

    private final Cron4jCronTaskRepository repository;

    public Cron4jScheduler(Cron4jCronTaskRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     *
     * Block tasks that are prohibited from concurrent execution when compatibility
     * tasks are triggered.
     */
    @Override
    @Nullable
    protected TaskExecutor spawnExecutor(Task task) {
        String id = task.getId().toString();
        if (repository.hasDisallowConcurrentExecution(id)) {
            if (repository.isTaskRunning(id)) {
                return null;
            }
        }
        return super.spawnExecutor(task);
    }
}
