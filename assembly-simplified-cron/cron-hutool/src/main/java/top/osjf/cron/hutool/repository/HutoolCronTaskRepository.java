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

package top.osjf.cron.hutool.repository;

import cn.hutool.cron.CronException;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.Scheduler;
import cn.hutool.cron.pattern.CronPattern;
import top.osjf.cron.core.annotation.NotNull;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronTaskNoExistException;
import top.osjf.cron.core.repository.CronListenerRepository;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.hutool.listener.HutoolCronListener;

/**
 * The Hutool cron task {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronTaskRepository implements CronTaskRepository<String, Runnable>,
        CronListenerRepository<HutoolCronListener> {

    /*** scheduler management*/
    private final Scheduler scheduler;

    /*** The construction method of scheduler management class {@link Scheduler}.*/
    public HutoolCronTaskRepository() {
        this.scheduler = CronUtil.getScheduler();
    }

    @Override
    public String register(String cronExpression, Runnable runnable) throws Exception {
        try {
            CronPattern.of(cronExpression);
        } catch (CronException e) {
            throw new CronExpressionInvalidException(cronExpression, e);
        }
        return scheduler.schedule(cronExpression, runnable);
    }

    @Override
    public void update(String taskId, String newCronExpression) throws Exception {
        if (scheduler.getTask(taskId) == null) {
            throw new CronTaskNoExistException(taskId);
        }
        CronPattern newCronPattern;
        try {
            newCronPattern = CronPattern.of(newCronExpression);
        } catch (CronException e) {
            throw new CronExpressionInvalidException(newCronExpression, e);
        }
        scheduler.updatePattern(taskId, newCronPattern);
    }

    @Override
    public void remove(String taskId) {
        if (!scheduler.descheduleWithStatus(taskId)) {
            throw new CronTaskNoExistException(taskId);
        }
    }

    @Override
    public void addCronListener(@NotNull HutoolCronListener cronListener) {
        scheduler.addListener(cronListener);
    }

    @Override
    public void removeCronListener(@NotNull HutoolCronListener cronListener) {
        scheduler.removeListener(cronListener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CronListenerRepository<HutoolCronListener> getCronListenerRepository() {
        return this;
    }
}
