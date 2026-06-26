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


package top.osjf.cron.hutool.repository;

import cn.hutool.cron.Scheduler;
import cn.hutool.cron.TaskExecutor;
import cn.hutool.cron.TaskExecutorManager;
import cn.hutool.cron.task.CronTask;
import top.osjf.commons.lang.Nullable;

/**
 * Based on the original {@link Scheduler} extended scheduler, compatible with task
 * scheduling related operations.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
class HutoolScheduler extends Scheduler {

    private static final long serialVersionUID = 629226661941357073L;

    private final HutoolCronTaskRepository repository;

    public HutoolScheduler(HutoolCronTaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Scheduler start() {
        super.start();
        taskExecutorManager = new HutoolTaskExecutorManager(this, repository);
        return this;
    }

    /**
     * @return the {@link TaskExecutorManager} instance.
     */
    public TaskExecutorManager getTaskExecutorManager () {
        return taskExecutorManager;
    }

    private static class HutoolTaskExecutorManager extends TaskExecutorManager {

        private static final long serialVersionUID = 7191856347270424096L;

        private final HutoolCronTaskRepository repository;

        public HutoolTaskExecutorManager(Scheduler scheduler, HutoolCronTaskRepository repository) {
            super(scheduler);
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
        public TaskExecutor spawnExecutor(CronTask task) {
            String id = task.getId();
            if (repository.shouldAllowTaskExecute(id)) {
                return super.spawnExecutor(task);
            }
            return null;
        }
    }
}
