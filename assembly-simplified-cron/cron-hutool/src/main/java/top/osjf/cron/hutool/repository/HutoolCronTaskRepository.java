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
import top.osjf.cron.core.CronTask;
import top.osjf.cron.core.RepositoryUtils;
import top.osjf.cron.core.lang.NotNull;
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
    @NotNull
    public String register(@NotNull String cronExpression, @NotNull Runnable runnable) {
        return RepositoryUtils.doRegister(() ->
                scheduler.schedule(cronExpression, runnable), CronException.class);
    }

    @Override
    @NotNull
    public String register(@NotNull CronTask task) {
        return register(task.getExpression(), task.getRunnable());
    }

    @Override
    public void update(@NotNull String taskId, @NotNull String newExpression) {
        RepositoryUtils.doVoidInvoke(() ->
                scheduler.updatePattern(taskId, new CronPattern(newExpression)), CronException.class);
    }

    @Override
    public void remove(@NotNull String taskId) {
        RepositoryUtils.doVoidInvoke(() -> scheduler.descheduleWithStatus(taskId), null);

    }

    @Override
    public void addCronListener(HutoolCronListener cronListener) {
        scheduler.addListener(cronListener);
    }

    @Override
    public void removeCronListener(HutoolCronListener cronListener) {
        scheduler.removeListener(cronListener);
    }
}
