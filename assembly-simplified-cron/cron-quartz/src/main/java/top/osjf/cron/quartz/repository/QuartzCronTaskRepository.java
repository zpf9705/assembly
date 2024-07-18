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

package top.osjf.cron.quartz.repository;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import top.osjf.cron.core.annotation.NotNull;
import top.osjf.cron.core.annotation.Nullable;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronTaskNoExistException;
import top.osjf.cron.core.repository.CronListenerRepository;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.quartz.listener.QuartzCronListener;

import java.text.ParseException;
import java.util.Properties;

/**
 * The Quartz cron task {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class QuartzCronTaskRepository implements CronTaskRepository<JobKey, JobDetail>,
        CronListenerRepository<QuartzCronListener> {

    /*** the scheduled task management class of Quartz.*/
    private final Scheduler scheduler;

    /*** The Quartz management interface for the listener.*/
    private final ListenerManager listenerManager;

    /**
     * Create a construction method for {@link Scheduler} using a configuration properties.
     *
     * @param properties {@link StdSchedulerFactory} configuration properties.
     */
    public QuartzCronTaskRepository(Properties properties, @Nullable JobFactory jobFactory) {
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            schedulerFactory.initialize(properties);
            scheduler = schedulerFactory.getScheduler();
            if (jobFactory != null) {
                scheduler.setJobFactory(jobFactory);
            }
            listenerManager = scheduler.getListenerManager();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return the scheduled task management class of Quartz.
     *
     * @return {@link Scheduler}.
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public JobKey register(String cronExpression, JobDetail jobDetail) throws Exception {
        CronExpression expression;
        try {
            expression = new CronExpression(cronExpression);
        } catch (ParseException e) {
            throw new CronExpressionInvalidException(cronExpression, e);
        }
        JobKey key = jobDetail.getKey();
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(key.getName())
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(expression));
        scheduler.scheduleJob(jobDetail, triggerBuilder.build());
        return jobDetail.getKey();
    }

    @Override
    public void update(JobKey jobKey, String newCronExpression) throws Exception {
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        remove(jobKey);
        register(newCronExpression, jobDetail);
    }

    @Override
    public void remove(JobKey jobKey) throws Exception {
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            throw new CronTaskNoExistException(jobKey.toString());
        }
        scheduler.deleteJob(jobKey);
    }

    @Override
    public void addCronListener(@NotNull QuartzCronListener cronListener) {
        listenerManager.addJobListener(cronListener);
    }

    @Override
    public void removeCronListener(@NotNull QuartzCronListener cronListener) {
        listenerManager.removeJobListener(cronListener.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public CronListenerRepository<QuartzCronListener> getCronListenerRepository() {
        return this;
    }
}
