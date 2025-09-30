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


package top.osjf.cron.quartz.listener;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import top.osjf.cron.core.repository.TimeoutMonitoringRunnable;
import top.osjf.cron.core.repository.TimeoutMonitoringRunnableContext;

/**
 * {@code TimeoutMonitoringJobListener} is a listener function during a single execution
 * lifecycle, designed to add contextual {@link TimeoutMonitoringRunnable} information
 * to timeout monitoring mechanisms.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class TimeoutMonitoringJobListener implements JobListener {

    /**
     * The attribute name of the {@link TimeoutMonitoringRunnable} timeout
     * control information set in {@link JobDetail#getJobDataMap()}.
     */
    public static final String TIMEOUT_PROPERTY = "TIMEOUT";

    @Override
    public String getName() {
        return "TIMEOUT_MONITORING_JOB_LISTENER";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        Object o = context.getJobDetail().getJobDataMap().get(TIMEOUT_PROPERTY);
        if (o != null) {
            TimeoutMonitoringRunnableContext.set((TimeoutMonitoringRunnable) o);
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        TimeoutMonitoringRunnableContext.set(null);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        TimeoutMonitoringRunnableContext.set(null);
    }
}
