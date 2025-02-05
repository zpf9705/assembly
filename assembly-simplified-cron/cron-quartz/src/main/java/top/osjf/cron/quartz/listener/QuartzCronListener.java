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
import top.osjf.cron.quartz.IDJSONConversion;

/**
 * The Quartz cron task listener {@link CronListener}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface QuartzCronListener extends JobListener, CronListener {

    /**
     * @return The default value is the fully qualified name of the
     * currently running class.
     */
    @Override
    default String getName() {
        return getClass().getName();
    }

    /**
     * {@inheritDoc}
     *
     * <strong>Note:</strong>
     * <p>If this method is rewritten, the {@link #start} and {@link #startWithId}
     * methods will become invalid and need to be handled by oneself.
     *
     * @param context {@inheritDoc}
     */
    @Override
    default void jobToBeExecuted(JobExecutionContext context) {
        start(newQuartzListenerContent(context));
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

    /**
     * When {@code JobExecutionException} is {@literal null}, it is judged to have
     * run successfully, otherwise it will run unsuccessfully.
     * <p>
     * <strong>Note:</strong>
     * <p>If this method is rewritten, the {@link #success} and {@link #successWithId}
     * and {@link #failed} and {@link #failedWithId} methods will become invalid and need
     * to be handled by oneself.
     *
     * @param context      {@inheritDoc}
     * @param jobException {@inheritDoc}
     */
    @Override
    default void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (jobException != null) {
            failed(newQuartzListenerContent(context), jobException);
        } else {
            success(newQuartzListenerContent(context));
        }
    }

    /**
     * Creates a new {@code QuartzListenerContent} by given {@code JobExecutionContext}.
     *
     * @param context quartz callback listener params {@code JobExecutionContext}.
     * @return a new {@code QuartzListenerContent}.
     */
    static QuartzListenerContent newQuartzListenerContent(JobExecutionContext context) {
        return new QuartzListenerContent(IDJSONConversion.convertJobKeyAsJSONID(context.getJobDetail().getKey()),
                context);
    }

    @Override
    default void startWithId(String id) {
    }

    @Override
    default void successWithId(String id) {
    }

    @Override
    default void failedWithId(String id, Throwable exception) {
    }
}
