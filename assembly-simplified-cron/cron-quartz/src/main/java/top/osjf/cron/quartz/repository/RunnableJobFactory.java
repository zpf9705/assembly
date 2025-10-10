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


package top.osjf.cron.quartz.repository;

import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code RunnableJobFactory} is an implementation class of {@link JobFactory},
 * used to produce cached {@link RunnableJob} task instances.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class RunnableJobFactory implements JobFactory {

    /** {@link Map} caching for {@link RunnableJob}.*/
    private final Map<String, RunnableJob> jobMap = new ConcurrentHashMap<>(64);

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        JobDataMap jobDataMap = bundle.getJobDetail().getJobDataMap();

        if (!jobDataMap.containsKey(JobConstants.ID_PROPERTY)
                || !jobDataMap.containsKey(JobConstants.RUNNABLE_PROPERTY)) {
            throw new SchedulerConfigException("Unknown task");
        }

        String id = (String) jobDataMap.get(JobConstants.ID_PROPERTY);
        Runnable runnable = (Runnable) jobDataMap.get(JobConstants.RUNNABLE_PROPERTY);

        return newJobInternal(id, runnable);
    }

    /**
     * Internal method of creating a new {@link RunnableJob}.
     * @param id        the task id.
     * @param runnable  the task {@link Runnable}.
     * @return  new {@link RunnableJob} instance.
     */
    protected RunnableJob newJobInternal(String id, Runnable runnable) {
        RunnableJob job = jobMap.get(id);
        if (job != null) {
            return job;
        }

        return jobMap.computeIfAbsent(id, s -> new RunnableJob(runnable));
    }
}
