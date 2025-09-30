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
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.simpl.SimpleThreadPool;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.core.util.ReflectUtils;
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.quartz.MethodLevelJob;
import top.osjf.cron.quartz.MethodLevelJobFactory;
import top.osjf.cron.quartz.QuartzUtils;
import top.osjf.cron.quartz.listener.JobListenerImpl;
import top.osjf.cron.quartz.listener.TimeoutMonitoringJobListener;

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
     * The factory class property name.
     */
    public static final String PROP_NAME_OF_FACTORY_CLASS = "quartz.customize.schedulerFactoryClass";

    /**
     * The  property name of when the scheduler is closed, wait for the task to complete execution.
     */
    public static final String PROP_NAME_OF_IF_STOP_WAIT_JOB_COMPLETE = "quartz.customize.waitForJobsToCompleteWhenStop";

    /**
     * The default value of when the scheduler is closed, wait for the task to complete execution.
     */
    public static final boolean DEFAULT_IF_STOP_WAIT_JOB_COMPLETE_VALUE = false;

    private String schedulerName = Scheduler.class.getName() + UUID.randomUUID();

    private Properties quartzProperties = System.getProperties();

    private Executor taskExecutor;

    private MethodLevelJobFactory jobFactory = new MethodLevelJobFactory();

    private SchedulerFactory schedulerFactory;

    private Class<? extends SchedulerFactory> schedulerFactoryClass = StdSchedulerFactory.class;

    /**
     * The scheduled task management class of Quartz.
     */
    private Scheduler scheduler;

    /**
     * The quartz management interface for the listener.
     */
    private ListenerManager listenerManager;

    private boolean waitForJobsToCompleteWhenStop;

    private boolean setSchedulerName;
    private boolean setSchedulerFactoryClass;
    private boolean setWaitForJobsToCompleteWhenStop;
    private boolean jobFactorySet;

    /**
     * @since 1.0.3
     */
    private final JobListenerImpl jobListener = new JobListenerImpl();

    /**
     * @since 1.0.3
     */
    public QuartzCronTaskRepository() {
    }

    /**
     * Creates a new {@code QuartzCronTaskRepository} using given {@code Scheduler}.
     *
     * @param scheduler quartz task scheduler instance after initialize.
     * @since 1.0.3
     */
    public QuartzCronTaskRepository(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Creates a new {@code QuartzCronTaskRepository} using given {@code SchedulerFactory}.
     *
     * @param schedulerFactory quartz {@code Scheduler} product factory instance after
     *                         initialize.
     * @since 1.0.3
     */
    public QuartzCronTaskRepository(SchedulerFactory schedulerFactory) {
        this.schedulerFactory = schedulerFactory;
    }

    /**
     * Set the name of the Scheduler to create via the SchedulerFactory, as an
     * alternative to the {@code org.quartz.scheduler.instanceName} property.
     *
     * @param schedulerName the unique name of the Scheduler.
     * @since 1.0.3
     */
    public void setSchedulerName(String schedulerName) {
        if (!StringUtils.isBlank(schedulerName)) {
            this.schedulerName = schedulerName;
            setSchedulerName = true;
        }
    }

    /**
     * Set the class object of the {@code SchedulerFactory} implementation class.
     * <p>When {@link #schedulerFactory} is not set, use this class object to implement
     * class instantiation and ensure there are empty constructs.
     *
     * @param schedulerFactoryClass the class object of the {@code SchedulerFactory}
     *                              implementation class.
     * @since 1.0.3
     */
    public void setSchedulerFactoryClass(Class<? extends SchedulerFactory> schedulerFactoryClass) {
        if (schedulerFactoryClass != null) {
            this.schedulerFactoryClass = schedulerFactoryClass;
            setSchedulerFactoryClass = true;
        }
    }

    /**
     * Set the parameter {@link SuperiorProperties} object for building the quartz task
     * factory, compatible with the Cron framework startup parameter series.
     *
     * <p>The configuration file cannot overwrite the value set by the external active
     * call to the set method.
     *
     * @param superiorProperties {@link SuperiorProperties} object for building the quartz
     *                           task factory.
     * @since 3.0.0
     */
    @Override
    public void setSuperiorProperties(SuperiorProperties superiorProperties) {
        super.setSuperiorProperties(superiorProperties);
        if (quartzProperties != null && !superiorProperties.isEmpty()) {
            this.quartzProperties = superiorProperties.asProperties();
            if (!setSchedulerFactoryClass)
                setSchedulerFactoryClass(superiorProperties
                        .getProperty(PROP_NAME_OF_FACTORY_CLASS, StdSchedulerFactory.class));
            if (!setWaitForJobsToCompleteWhenStop)
                setWaitForJobsToCompleteWhenStop(superiorProperties
                        .getProperty(PROP_NAME_OF_IF_STOP_WAIT_JOB_COMPLETE, DEFAULT_IF_STOP_WAIT_JOB_COMPLETE_VALUE));
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
     * Set inheritance and subclass factory instance of {@link MethodLevelJobFactory}.
     * <p>The {@link JobDetail} building rules must follow the specifications
     * in {@link MethodLevelJobFactory#newJob}.
     *
     * @param jobFactory inheritance and subclass factory instance of
     *                   {@link MethodLevelJobFactory}.
     * @since 1.0.3
     */
    public void setJobFactory(MethodLevelJobFactory jobFactory) {
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
        if (scheduler == null) {
            if (schedulerFactory != null) {
                if (setSchedulerName) {
                    scheduler = schedulerFactory.getScheduler(schedulerName);
                } else {
                    scheduler = schedulerFactory.getScheduler();
                }
            } else {
                SchedulerFactory schedulerFactory = ReflectUtils.newInstance(schedulerFactoryClass);
                if (schedulerFactory instanceof StdSchedulerFactory) {
                    if (!quartzProperties.containsKey(StdSchedulerFactory.PROP_THREAD_POOL_CLASS)) {
                        if (this.taskExecutor != null) {
                            // Set the thread pool instance for proxy task execution and assign values
                            // during the initialization phase.
                            TaskExecutorDelegateThreadPool.setTaskExecutor(taskExecutor);
                            quartzProperties.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,
                                    TaskExecutorDelegateThreadPool.class.getName());
                        } else {
                            // Set necessary default properties here, as Quartz will not apply
                            // its default configuration when explicitly given properties.
                            quartzProperties.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,
                                    SimpleThreadPool.class.getName());
                            quartzProperties.setProperty(PROP_THREAD_COUNT, Integer.toString(DEFAULT_THREAD_COUNT));
                        }
                    }
                    // Set the name of the production task manager for the factory instance created
                    // in this class and specify it for retrieval later.
                    quartzProperties.putIfAbsent(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, schedulerName);
                    ((StdSchedulerFactory) schedulerFactory).initialize(quartzProperties);
                    schedulerFactory.getScheduler();
                    scheduler = schedulerFactory.getScheduler(schedulerName);
                }
            }
        }
        if (scheduler instanceof StdScheduler) {
            scheduler.setJobFactory(jobFactory);
            jobFactorySet = true;
        }
        listenerManager = scheduler.getListenerManager();
        listenerManager.addJobListener(jobListener);
        if (!initIdentityMonitoringExecutor) {
            listenerManager.addJobListener(new TimeoutMonitoringJobListener());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull Runnable runnable) throws CronInternalException {
        if (runnable instanceof TimeoutMonitoringRunnable) {
            runnable = ((TimeoutMonitoringRunnable) runnable).getReal();
        }
        Method method = null;
        if (runnable instanceof CronMethodRunnable) {
            CronMethodRunnable cronMethodRunnable = (CronMethodRunnable) runnable;
            method = cronMethodRunnable.getMethod();
        } else if (runnable instanceof MethodProviderRunnable) {
            method = ((MethodProviderRunnable) runnable).getMethod();
        }
        if (method == null) {
            throw new CronInternalException("Unable resolve " + runnable.getClass());
        }
        String name = method.getName();
        String group = method.getDeclaringClass().getName();
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = QuartzUtils.buildStandardJobDetail(name, group);

        if (runnable instanceof TimeoutMonitoringRunnable) {
            jobDetail.getJobDataMap().put(TimeoutMonitoringJobListener.TIMEOUT_PROPERTY, runnable);
        }

        return doRegister(expression, jobKey, jobDetail);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull CronMethodRunnable runnable) throws CronInternalException {
        return register(expression, (Runnable) runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull RunnableTaskBody body) throws CronInternalException {
        return register(expression, body.getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull TaskBody body) {
        JobDetail jobDetail;
        if (body.isWrapperFor(JobDetailTaskBody.class)) {
            jobDetail = body.unwrap(JobDetailTaskBody.class).getJobDetail();
        } else {
            throw new UnsupportedTaskBodyException(body.getClass());
        }
        QuartzUtils.checkJobClassRules(jobDetail.getJobClass());
        JobKey key = jobDetail.getKey();
        QuartzUtils.checkJobKeyRules(key);
        return doRegister(expression, key, jobDetail);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull CronTask task) {
        Method method = task.getRunnable().getMethod();
        return register(task.getExpression(), new JobDetailTaskBody(
                QuartzUtils.buildStandardJobDetail(method.getName(), method.getDeclaringClass().getName())));
    }

    private String doRegister(String expression, JobKey key, JobDetail jobDetail) {
        return RepositoryUtils.doRegister(() -> {
            TriggerKey triggerKey = new TriggerKey(key.getName(), key.getGroup());
            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(expression));
            getInitializedScheduler().scheduleJob(jobDetail, triggerBuilder.build());
            return QuartzUtils.getIdBySerializeJobKey(key);
        }, ParseException.class);
    }

    @Override
    public boolean hasCronTaskInfo(@NotNull String id) {
        JobKey jobKey = QuartzUtils.getJobKeyByDeSerializeId(id);
        try {
            return getInitializedScheduler().checkExists(jobKey);
        }
        catch (SchedulerException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public CronTaskInfo getCronTaskInfo(@NotNull String id) {
        return buildCronTaskInfo(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
        JobKey jobKey = QuartzUtils.getJobKeyByDeSerializeId(id);
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
            Runnable runnable = null;
            Object target = null;
            Method method = null;
            if (jobFactorySet) {
                MethodLevelJob job = jobFactory.getJob(jobKey);
                CronMethodRunnable cronMethodRunnable = job.getCronMethodRunnable();
                runnable = cronMethodRunnable;
                target = cronMethodRunnable.getTarget();
                method = cronMethodRunnable.getMethod();
            }
            String id = QuartzUtils.getIdBySerializeJobKey(jobKey);
            return customizeCronTaskInfo(new CronTaskInfo(id, expression, runnable, target, method));
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(@NotNull String id, @NotNull String newExpression) {
        JobKey jobKey = QuartzUtils.getJobKeyByDeSerializeId(id);
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
    public void remove(@NotNull String id) {
        RepositoryUtils.doVoidInvoke(() ->
                        getInitializedScheduler().deleteJob(QuartzUtils.getJobKeyByDeSerializeId(id)),
                null);
    }

    @Override
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
        } catch (SchedulerException e) {
            throw new IllegalStateException(e);
        }
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
