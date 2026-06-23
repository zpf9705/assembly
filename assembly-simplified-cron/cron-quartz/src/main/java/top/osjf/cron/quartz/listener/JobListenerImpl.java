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

import org.quartz.*;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.listener.DefaultCronListenerCollector;
import top.osjf.cron.core.listener.ListenerContext;
import top.osjf.cron.core.listener.ListenerContextTypeProvider;
import top.osjf.cron.quartz.repository.QuartzCronTaskRepository;

/**
 * The default Quartz task listener implementation class extends {@link CronListenerCollector}
 * to implement broadcast mode for {@link top.osjf.cron.core.listener.CronListener}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@ListenerContextTypeProvider(QuartzListenerContent.class)
public class JobListenerImpl extends DefaultCronListenerCollector implements JobListener, SchedulerListener {

    /**
     * @param repository The resource class used for listening to callbacks
     *                   in {@link ListenerContext}.
     * @since 3.0.2
     */
    public JobListenerImpl(QuartzCronTaskRepository repository) {
        super(repository);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        ContextHolder.runWithContext(context, () -> doStartListener(context));
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        ContextHolder.runWithContext(context, () -> doFailedListener(context, new JobExecutionVetoedException()));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        ContextHolder.runWithContext(context, () -> {
            if (jobException != null) {
                doFailedListener(context, jobException);
            } else {
                doSuccessListener(context);
            }
        });
    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        JobExecutionContext context = ContextHolder.getContext();
        if (context != null) {
            doFailedListener(context, cause);
            ContextHolder.setContext(null);
        }
    }


    private static class ContextHolder {

        private static final ThreadLocal<JobExecutionContext> CONTEXT_LOCAL = new ThreadLocal<>();

        /**
         * The given {@link Runnable} is executed within the context information set and cleared
         * gap, so there is no need to worry about {@link JobExecutionContext} encountering an
         * exception and failing to clear temporary information. According to Quartz's execution
         * logic, any exceptions encountered by the listener will ultimately be called back within
         * {@link SchedulerListener#schedulerError}, so temporary information is cleared after
         * being used within {@link JobExecutionContext}.
         *
         * @param context   the {@code JobExecutionContext}.
         * @param runnable  the {@code Runnable}.
         * @see org.quartz.core.QuartzScheduler#notifyJobListenersToBeExecuted
         * @see org.quartz.core.QuartzScheduler#notifyJobListenersWasVetoed
         * @see org.quartz.core.QuartzScheduler#notifyJobListenersWasExecuted
         * @see org.quartz.core.QuartzScheduler#notifySchedulerListenersError
         */
        static void runWithContext(JobExecutionContext context, Runnable runnable) {
            setContext(context);
            runnable.run();
            setContext(null);
        }

        static void setContext(JobExecutionContext context) {
            if (context == null) {
                CONTEXT_LOCAL.remove();
            }
            else {
                CONTEXT_LOCAL.set(context);
            }
        }

        static JobExecutionContext getContext() {
            return CONTEXT_LOCAL.get();
        }
    }
    @Override
    public void jobScheduled(Trigger trigger) {

    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {

    }

    @Override
    public void triggerFinalized(Trigger trigger) {

    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {

    }

    @Override
    public void triggersPaused(String triggerGroup) {

    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {

    }

    @Override
    public void triggersResumed(String triggerGroup) {

    }

    @Override
    public void jobAdded(JobDetail jobDetail) {

    }

    @Override
    public void jobDeleted(JobKey jobKey) {

    }

    @Override
    public void jobPaused(JobKey jobKey) {

    }

    @Override
    public void jobsPaused(String jobGroup) {

    }

    @Override
    public void jobResumed(JobKey jobKey) {

    }

    @Override
    public void jobsResumed(String jobGroup) {

    }

    @Override
    public void schedulerInStandbyMode() {

    }

    @Override
    public void schedulerStarted() {

    }

    @Override
    public void schedulerStarting() {

    }

    @Override
    public void schedulerShutdown() {

    }

    @Override
    public void schedulerShuttingdown() {

    }

    @Override
    public void schedulingDataCleared() {

    }
}
