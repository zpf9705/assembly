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
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.simpl.SimpleThreadPool;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.lifecycle.InitializeProperties;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.quartz.QuartzUtils;
import top.osjf.cron.quartz.listener.JobListenerImpl;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The {@link CronTaskRepository} implementation class of quartz.
 *
 * <p>This implementation class includes the construction and lifecycle management
 * of the quartz build scheduler, as well as operations related to tasks and listeners.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class QuartzCronTaskRepository extends AbstractCronTaskRepository implements Supplier<ListenerManager> {

    /**
     * The thread count property name.
     */
    private static final String PROP_THREAD_COUNT = "org.quartz.threadPool.threadCount";

    /**
     * The default thread count.
     */
    private static final int DEFAULT_THREAD_COUNT = 10;

    /**
     * The  property name of when the scheduler is closed, wait for the task to complete execution.
     */
    public static final String PROP_NAME_OF_IF_STOP_WAIT_JOB_COMPLETE = "quartz.customize.waitForJobsToCompleteWhenStop";

    /**
     * The default value of when the scheduler is closed, wait for the task to complete execution.
     */
    public static final boolean DEFAULT_IF_STOP_WAIT_JOB_COMPLETE_VALUE = false;

    private Properties quartzProperties = System.getProperties();

    private Executor taskExecutor;

    private RunnableJobFactory jobFactory = new RunnableJobFactory();

    /**
     * The scheduled task management class of Quartz.
     */
    private Scheduler scheduler;

    /**
     * The quartz management interface for the listener.
     */
    private ListenerManager listenerManager;

    private boolean waitForJobsToCompleteWhenStop;

    private boolean setWaitForJobsToCompleteWhenStop;

    /**
     * @since 1.0.3
     */
    private final JobListenerImpl jobListener = new JobListenerImpl(this);

    /**
     * @since 1.0.3
     */
    public QuartzCronTaskRepository() {
    }

    /**
     * Set the parameter {@link InitializeProperties} object for building the quartz task
     * factory, compatible with the Cron framework startup parameter series.
     *
     * <p>The configuration file cannot overwrite the value set by the external active
     * call to the set method.
     *
     * @param initializeProperties {@link InitializeProperties} object for building the quartz
     *                           task factory.
     * @since 3.0.0
     */
    @Override
    public void setInitializeProperties(InitializeProperties initializeProperties) {
        super.setInitializeProperties(initializeProperties);
        if (quartzProperties != null && !initializeProperties.isEmpty()) {
            this.quartzProperties = initializeProperties;
            if (!setWaitForJobsToCompleteWhenStop)
                setWaitForJobsToCompleteWhenStop(initializeProperties
                        .getBoolean(PROP_NAME_OF_IF_STOP_WAIT_JOB_COMPLETE, DEFAULT_IF_STOP_WAIT_JOB_COMPLETE_VALUE));
        }
    }

    /**
     * Set up a thread pool instance for executing a quartz framework task.
     *
     * @param taskExecutor a thread pool instance for executing.
     * @since 1.0.3
     */
    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Set inheritance and subclass factory instance of {@link RunnableJobFactory}.
     * <p>The {@link JobDetail} building rules must follow the specifications
     * in {@link RunnableJobFactory#newJob}.
     *
     * @param jobFactory inheritance and subclass factory instance of
     *                   {@link RunnableJobFactory}.
     * @since 1.0.3
     */
    public void setJobFactory(RunnableJobFactory jobFactory) {
        if (jobFactory != null) {
            this.jobFactory = jobFactory;
        }
    }

    /**
     * Set whether to wait for all tasks to complete when closing the scheduler.
     *
     * @param waitForJobsToCompleteWhenStop if set to true, wait for all tasks to complete
     *                                      before closing the executor, otherwise it will
     *                                      not.
     */
    public void setWaitForJobsToCompleteWhenStop(boolean waitForJobsToCompleteWhenStop) {
        this.waitForJobsToCompleteWhenStop = waitForJobsToCompleteWhenStop;
        setWaitForJobsToCompleteWhenStop = true;
    }

    /**
     * Initialize the scheduled task manager based on the provided attributes.
     *
     * @throws SchedulerException Possible {@code SchedulerException} error objects generated
     *                            during initialization process.
     * @since 1.0.3
     */
    @Override
    public void initialize() throws Exception {
        super.initialize();
        if (!quartzProperties.containsKey(StdSchedulerFactory.PROP_THREAD_POOL_CLASS)) {
            if (taskExecutor != null) {
                TaskExecutorDelegateThreadPool.setTaskExecutor(taskExecutor);
                quartzProperties.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,
                        TaskExecutorDelegateThreadPool.class.getName());
            } else {
                quartzProperties.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,
                        SimpleThreadPool.class.getName());
                quartzProperties.setProperty(PROP_THREAD_COUNT, Integer.toString(DEFAULT_THREAD_COUNT));
            }
        }
        if (!quartzProperties.containsKey(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME)) {
            quartzProperties.putIfAbsent(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME,
                    Scheduler.class.getName() + UUID.randomUUID());
        }
        scheduler = new StdSchedulerFactory(quartzProperties).getScheduler();
        scheduler.setJobFactory(jobFactory);
        listenerManager = scheduler.getListenerManager();
        listenerManager.addJobListener(jobListener);
        listenerManager.addSchedulerListener(jobListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getName() {
        return "QUARTZ_SCHEDULER@" + super.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkSupportedExpression(@NotNull String expression) throws CronExpressionInvalidException {
        try {
            new CronExpression(expression);
        }
        catch (ParseException ex) {
            throw new CronExpressionInvalidException(expression, getName(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull Runnable runnable) throws CronInternalException {
        return doRegister(expression, new JobKeyWrapperdRunnable(runnable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull CronMethodRunnable runnable) throws CronInternalException {
        return register(expression, (Runnable) runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull RunnableTaskBody body) throws CronInternalException {
        return register(expression, body.getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull TaskBody body) {
        if (body.isWrapperFor(RunnableTaskBody.class)) {
            return register(expression, ((RunnableTaskBody) body).getRunnable());
        }
        throw new UnsupportedTaskBodyException(body.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull CronTask task) {
        return register(task.getExpression(), task.getRunnable());
    }

    private String doRegister(String expression, JobKeyWrapperdRunnable runnable) {
        return RepositoryUtils.doRegister(() -> {
            JobKey jobKey = runnable.getJobKey();
            TriggerKey triggerKey = new TriggerKey(jobKey.getName(), jobKey.getGroup());
            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(expression));
            JobDetail jobDetail = JobBuilder.newJob(RunnableJob.class)
                            .withIdentity(jobKey.getName(), jobKey.getGroup()).build();
            String jobId = QuartzUtils.getJobIdentity(jobKey);
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.put(JobConstants.RUNNABLE_PROPERTY, runnable);
            jobDataMap.put(JobConstants.ID_PROPERTY, jobId);
            getInitializedScheduler().scheduleJob(jobDetail, triggerBuilder.build());
            return jobId;
        }, ParseException.class);
    }

    @Override
    public boolean hasCronTaskInfoInternal(@NotNull String id) {
        JobKey jobKey = QuartzUtils.getJobKey(id);
        try {
            return getInitializedScheduler().checkExists(jobKey);
        } catch (SchedulerException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public CronTaskInfo getCronTaskInfoInternal(@NotNull String id) {
        return buildCronTaskInfo(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<CronTaskInfo> getAllCronTaskInfo() {
        try {
            return getInitializedScheduler().getJobKeys(GroupMatcher.anyGroup())
                    .stream()
                    .map(this::buildCronTaskInfo)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Build a {@link CronTaskInfo} by given id.
     *
     * @param id the given id.
     * @return new {@link CronTaskInfo} by given id.
     */
    @Nullable
    private CronTaskInfo buildCronTaskInfo(String id) {
        JobKey jobKey = QuartzUtils.getJobKey(id);
        return buildCronTaskInfo(jobKey);
    }

    /**
     * Build a {@link CronTaskInfo} by given {@code JobKey}.
     *
     * @param jobKey the given {@code JobKey}.
     * @return new {@link CronTaskInfo} by given {@code JobKey}.
     */
    @Nullable
    private CronTaskInfo buildCronTaskInfo(JobKey jobKey) {
        String group = jobKey.getGroup();
        try {
            Set<JobKey> jobKeys = getInitializedScheduler().getJobKeys(GroupMatcher.groupEquals(group));
            Trigger trigger = getInitializedScheduler().getTrigger(new TriggerKey(jobKey.getName(),
                    jobKey.getGroup()));
            if (!jobKeys.contains(jobKey)) {
                return null;
            }

            String expression = QuartzUtils.getTriggerExpression(trigger);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            String id = (String) jobDataMap.get(JobConstants.ID_PROPERTY);
            JobKeyWrapperdRunnable jobKeyWrapperdRunnable
                    = (JobKeyWrapperdRunnable) jobDataMap.get(JobConstants.RUNNABLE_PROPERTY);
            Runnable runnable = jobKeyWrapperdRunnable.getRaw();
            runnable = unwrapRunnable(runnable);
            Object target = null;
            Method method = null;
            if (runnable instanceof CronMethodRunnable) {
                CronMethodRunnable cr = (CronMethodRunnable) runnable;
                target = cr.getTarget();
                method = cr.getMethod();
            }
            return customizeCronTaskInfo(new CronTaskInfo(id, expression, runnable, target, method));
        }
        catch (SchedulerException ex) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateInternal(@NotNull String id, @NotNull String newExpression) {
        JobKey jobKey = QuartzUtils.getJobKey(id);
        TriggerKey triggerKey = new TriggerKey(jobKey.getName(), jobKey.getGroup());
        RepositoryUtils.doVoidInvoke(() -> getInitializedScheduler().rescheduleJob(triggerKey,
                TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule(newExpression))
                        .build()), ParseException.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeInternal(@NotNull String id) {
        RepositoryUtils.doVoidInvoke(() ->
                        getInitializedScheduler().deleteJob(QuartzUtils.getJobKey(id)),
                null);
    }

    @Override
    @NotNull
    protected CronListenerCollector getCronListenerCollector() {
        return jobListener;
    }

    /**
     * Return the listener management instance {@link ListenerManager} of this
     * quartz task repository.
     *
     * <p>This repository only supports dynamic addition of {@link JobListener}
     * listeners, that is, {@link JobListenerImpl} instances. If developers want
     * to extend and add other listeners, such as {@link SchedulerListener},
     * {@link TriggerListener}, etc., they can call this method to obtain
     * {@link ListenerManager} instances and add and process them themselves.
     *
     * @return the listener management instance {@link ListenerManager} of this
     * quartz task repository.
     */
    @Override
    @NotNull
    public ListenerManager get() {
        return listenerManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        try {
            getInitializedScheduler().start();
        } catch (SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        try {
            getInitializedScheduler().shutdown(waitForJobsToCompleteWhenStop);
        }
        catch (SchedulerException ex) {
            throw new IllegalStateException(ex);
        }
        closeMonitoringExecutor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        try {
            return getInitializedScheduler().isStarted();
        } catch (SchedulerException e) {
            return false;
        }
    }

    /**
     * @return Return {@link Scheduler} after an initialization action {@link #initialize()}.
     * @since 3.0.1
     */
    private Scheduler getInitializedScheduler() {
        ensureInitialized();

        return scheduler;
    }
}
