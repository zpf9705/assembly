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
import top.osjf.cron.core.exception.CronTaskRepositoryExecutionException;
import top.osjf.cron.core.listener.CronListener;
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
public class QuartzCronTaskRepository implements CronTaskRepository<JobKey, JobDetail> {

    /*** the scheduled task management class of Quartz.*/
    private final Scheduler scheduler;

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
    public JobKey register(String cronExpression, JobDetail jobDetail) throws CronExpressionInvalidException {
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
        try {
            scheduler.scheduleJob(jobDetail, triggerBuilder.build());
        } catch (SchedulerException e) {
            throw new CronTaskRepositoryExecutionException(e);
        }
        return jobDetail.getKey();
    }

    @Override
    public void update(JobKey jobKey, String newCronExpression) throws CronExpressionInvalidException,
            CronTaskNoExistException {
        JobDetail jobDetail;
        try {
            jobDetail = scheduler.getJobDetail(jobKey);
        } catch (SchedulerException e) {
            throw new CronTaskRepositoryExecutionException(e);
        }
        remove(jobKey);
        register(newCronExpression, jobDetail);
    }

    @Override
    public void remove(JobKey jobKey) throws CronTaskNoExistException {
        JobDetail jobDetail;
        try {
            jobDetail = scheduler.getJobDetail(jobKey);
        } catch (SchedulerException e) {
            throw new CronTaskRepositoryExecutionException(e);
        }
        if (jobDetail == null) {
            throw new CronTaskNoExistException(jobKey.toString());
        }
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new CronTaskRepositoryExecutionException(e);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void addCronListener(@NotNull CronListener cronListener) {
        if (cronListener instanceof QuartzCronListener) {
            try {
                scheduler.getListenerManager().addJobListener((QuartzCronListener) cronListener);
            } catch (SchedulerException e) {
                throw new CronTaskRepositoryExecutionException(e);
            }
        }
    }
}
