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

package top.osjf.cron.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import top.osjf.cron.core.listener.CronListener;

/**
 * The Quartz cron task {@link CronListener}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface QuartzCronListener extends JobListener, CronListener<JobExecutionContext> {

    @Override
    String getName();

    @Override
    default void jobToBeExecuted(JobExecutionContext context) {
        onStart(context);
    }

    /**
     * Called when {@link org.quartz.TriggerListener} blocks the execution of a job.
     * If the vetoJobExecution method of TriggerListener returns true, the Job will
     * not execute and jobExecutionVetoed will be called This method can be used
     * to handle situations where a job has not been executed, such as logging or
     * updating status.
     *
     * @param context {@link JobExecutionContext}.
     */
    @Override
    default void jobExecutionVetoed(JobExecutionContext context) {

    }

    @Override
    default void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (jobException != null) {
            onFailed(context, jobException);
        } else {
            onSucceeded(context);
        }
    }

    @Override
    void onStart(JobExecutionContext value);

    @Override
    void onSucceeded(JobExecutionContext value);

    @Override
    void onFailed(JobExecutionContext value, Throwable exception);
}
