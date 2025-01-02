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
import org.quartz.simpl.SimpleThreadPool;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lifestyle.StartupProperties;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.CronTask;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.repository.RepositoryUtils;
import top.osjf.cron.core.repository.TaskBody;
import top.osjf.cron.core.util.ReflectUtils;
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.quartz.IDJSONConversion;
import top.osjf.cron.quartz.MethodLevelJob;
import top.osjf.cron.quartz.MethodLevelJobFactory;
import top.osjf.cron.quartz.listener.QuartzCronListener;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * The Quartz cron task repository {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class QuartzCronTaskRepository implements CronTaskRepository {

    /**
     * The thread count property.
     */
    public static final String PROP_THREAD_COUNT = "org.quartz.threadPool.threadCount";

    /**
     * The default thread count.
     */
    public static final int DEFAULT_THREAD_COUNT = 10;

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

    private boolean setSchedulerName;
    private boolean setSchedulerFactoryClass;

    /**
     * @since 1.0.3
     */
    public QuartzCronTaskRepository() {
    }

    /**
     * Creates a new {@code QuartzCronTaskRepository} using given {@code Scheduler}.
     *
     * @param scheduler quartz task scheduler instance.
     * @since 1.0.3
     */
    public QuartzCronTaskRepository(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Creates a new {@code QuartzCronTaskRepository} using given {@code SchedulerFactory}.
     *
     * @param schedulerFactory quartz {@code Scheduler} product factory instance.
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
     * Set the parameter {@link StartupProperties} object for building the quartz task
     * factory, compatible with the Cron framework startup parameter series.
     *
     * <p>The configuration file cannot overwrite the value set by the external active
     * call to the set method.
     *
     * @param startupProperties {@link StartupProperties} object for building the quartz
     *                          task factory.
     * @since 1.0.3
     */
    public void setQuartzProperties(StartupProperties startupProperties) {
        if (quartzProperties != null) {
            this.quartzProperties = startupProperties.asProperties();
            if (!setSchedulerFactoryClass)
                startupProperties.getProperty("schedulerFactoryClass", StdSchedulerFactory.class);
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
     * Initialize the scheduled task manager based on the provided attributes.
     *
     * @throws SchedulerException Possible {@code SchedulerException} error objects generated
     *                            during initialization process.
     * @since 1.0.3
     */
    @PostConstruct
    public void initialize() throws SchedulerException {
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
                    scheduler.setJobFactory(jobFactory);
                }
            }
        } else {
            scheduler.setJobFactory(jobFactory);
        }
        listenerManager = scheduler.getListenerManager();
    }

    /**
     * Return a quartz {@code Scheduler} after initialize.
     *
     * @return a quartz {@code Scheduler} after initialize.
     */
    public Scheduler getScheduler() {
        return scheduler;
    }


    @Override
    @NotNull
    public String register(@NotNull String expression, @NotNull TaskBody body) {
        JobDetail jobDetail = body.unwrap(JobDetailTaskBody.class).getJobDetail();
        JobKey key = jobDetail.getKey();
        checkJobKeyRules(key);
        return RepositoryUtils.doRegister(() -> {
            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(key.getName(), key.getGroup())
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(expression));
            scheduler.scheduleJob(jobDetail, triggerBuilder.build());
            return IDJSONConversion.convertJobKeyAsJSONID(key);
        }, ParseException.class);
    }

    private void checkJobKeyRules(JobKey key) {
        String declaringClassName = key.getGroup();
        if (StringUtils.isBlank(declaringClassName)) {
            throw new IllegalArgumentException
                    ("The attribute <org.quartz.JobKey#group> of <org.quartz.JobKey> is required and is" +
                            " a fully qualified name for the existing class.");
        }
        String methodName = key.getName();
        if (StringUtils.isBlank(methodName)) {
            throw new IllegalArgumentException
                    ("The attribute <org.quartz.JobKey#name> of <org.quartz.JobKey> is required and is" +
                            " the executable method in class <" + declaringClassName + ">.");
        }
        Class<?> declaringClass;
        try {
            declaringClass = ReflectUtils.forName(declaringClassName);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("The input requirement for the <org.quartz.JobKey#group> attribute of " +
                            "<org.quartz.JobKey> is the fully qualified name of the executing class.");
        }
        try {
            ReflectUtils.getMethod(declaringClass, methodName);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("The input requirement for the <org.quartz.JobKey#name> attribute of " +
                            "<org.quartz.JobKey> is the executable method in class <" + declaringClassName + ">.");
        }
    }

    @Override
    @NotNull
    public String register(@NotNull CronTask task) {
        Method method = task.getRunnable().getMethod();
        return register(task.getExpression(), new JobDetailTaskBody(JobBuilder.newJob(MethodLevelJob.class)
                .withIdentity(method.getName(), method.getDeclaringClass().getName()).build()));
    }

    @Override
    public void update(@NotNull String id, @NotNull String newExpression) {
        JobKey jobKey = IDJSONConversion.convertJSONIDAsJobKey(id);
        String name = jobKey.getName();
        RepositoryUtils.doVoidInvoke(() -> scheduler.rescheduleJob(new TriggerKey(name), TriggerBuilder.newTrigger()
                .withIdentity(name)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(newExpression))
                .build()), ParseException.class);
    }

    @Override
    public void remove(@NotNull String id) {
        RepositoryUtils.doVoidInvoke(() ->
                scheduler.deleteJob(IDJSONConversion.convertJSONIDAsJobKey(id)), null);
    }

    @Override
    public void addListener(@NotNull CronListener listener) {
        listenerManager.addJobListener(listener.unwrap(QuartzCronListener.class));
    }

    @Override
    public void removeListener(@NotNull CronListener listener) {
        listenerManager.removeJobListener(listener.unwrap(QuartzCronListener.class).getName());
    }
}
