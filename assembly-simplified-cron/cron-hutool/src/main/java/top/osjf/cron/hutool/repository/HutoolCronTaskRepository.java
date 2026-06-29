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

package top.osjf.cron.hutool.repository;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.cron.CronException;
import cn.hutool.cron.Scheduler;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.pattern.parser.PatternParser;
import cn.hutool.cron.task.InvokeTask;
import cn.hutool.cron.task.RunnableTask;
import cn.hutool.cron.task.Task;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.StringUtils;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lifecycle.InitializeProperties;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.hutool.listener.TaskListenerImpl;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * The {@link CronTaskRepository} implementation class of hutool.
 *
 * <p>This implementation class includes the construction and lifecycle management
 * of the hutool build scheduler, as well as operations related to tasks and listeners.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronTaskRepository extends AbstractCronTaskRepository {

    /**
     * The {@link #daemon} property name of hutool.
     */
    public static final String PROPERTY_NAME_OF_DAEMON = "isDaemon";
    private static final boolean DEFAULT_VALUE_OF_DAEMON = false;
    /**
     * The {@link #timeZone} property name of hutool.
     */
    public static final String PROPERTY_NAME_OF_TIMEZONE = "timezone";
    private static final TimeZone DEFAULT_VALUE_OF_TIMEZONE = TimeZone.getDefault();
    /**
     * The {@link #isMatchSecond} property name of hutool.
     */
    public static final String PROPERTY_NAME_OF_MATCH_SECOND = "isMatchSecond";
    private static final boolean DEFAULT_VALUE_OF_MATCH_SECOND = true;
    /**
     * The {@link #isMatchSecond} property name of hutool.
     */
    public static final String PROPERTY_NAME_OF_IF_STOP_CLEAR_TASK = "isIfStopClearTasks";
    private static final boolean DEFAULT_VALUE_OF_IF_STOP_CLEAR_TASK = true;

    private ExecutorService executorService;

    private boolean isMatchSecond = DEFAULT_VALUE_OF_MATCH_SECOND;

    private boolean daemon;

    /**
     * This flag indicates whether to clear the related task list when closing the scheduler.
     * If {@code #daemon == true}, this value does not need to be set, otherwise it needs to
     * be monitored.
     */
    private boolean ifStopClearTasks;

    private TimeZone timeZone = TimeZone.getDefault();

    private Scheduler scheduler;

    private boolean setMatchSecond;
    private boolean setDaemon;
    private boolean setTimeZone;
    private boolean setIfStopClearTasks;

    /**
     * @since 1.0.3
     */
    private final TaskListenerImpl taskListener
            = new TaskListenerImpl(this).initRunningHolder().unwrap(TaskListenerImpl.class);

    /**
     * @since 1.0.3
     */
    public HutoolCronTaskRepository() {
    }

    /**
     * Set custom thread pool.
     *
     * <p>When customizing a thread pool, it is necessary to consider whether the thread
     * executing the method is a daemon thread {@link #setDaemon}.
     *
     * @param executorService Custom thread pool service instance.
     * @since 1.0.3
     */
    public void setThreadExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Set the parameter {@link InitializeProperties} object for building the hutool task
     * scheduler, compatible with the Cron framework startup parameter series.
     *
     * <p>The configuration file cannot overwrite the value set by the external active
     * call to the set method.
     *
     * @param initializeProperties {@link InitializeProperties} object for building the hutool
     *                           task scheduler.
     * @since 1.0.3
     */
    @Override
    public void setInitializeProperties(InitializeProperties initializeProperties) {
        super.setInitializeProperties(initializeProperties);
        if (initializeProperties != null && !initializeProperties.isEmpty()) {
            if (!setDaemon)
                setDaemon(initializeProperties.getBoolean(PROPERTY_NAME_OF_DAEMON, DEFAULT_VALUE_OF_DAEMON));
            if (!setMatchSecond)
                setMatchSecond(initializeProperties.getBoolean(PROPERTY_NAME_OF_MATCH_SECOND, DEFAULT_VALUE_OF_MATCH_SECOND));
            if (!setTimeZone) {
                TimeZone zone = DEFAULT_VALUE_OF_TIMEZONE;
                String zoneID = initializeProperties.getProperty(PROPERTY_NAME_OF_TIMEZONE);
                if (StringUtils.isNotBlank(zoneID)) {
                    zone = TimeZone.getTimeZone(zoneID);
                }
                setTimeZone(zone);
            }
            if (!setIfStopClearTasks) {
                setIfStopClearTasks(initializeProperties.getBoolean(PROPERTY_NAME_OF_IF_STOP_CLEAR_TASK,
                        DEFAULT_VALUE_OF_IF_STOP_CLEAR_TASK));
            }
        }
    }

    /**
     * Set whether to support second matching.
     *
     * <p>This method is used to define whether to use the second matching mode.
     * If it is {@code true}, the first digit in the timed task expression is seconds,
     * otherwise it is minutes, and the default is minutes.
     *
     * @param matchSecond {@code true} supports, {@code false} does not support.
     * @since 1.0.3
     */
    public void setMatchSecond(boolean matchSecond) {
        isMatchSecond = matchSecond;
        setMatchSecond = true;
    }

    /**
     * Set whether to be a guardian thread.
     *
     * <p>If true, the scheduled task executed immediately after calling the
     * {@link Scheduler#stop()} method ends;otherwise, it waits for execution to
     * complete before ending. Default non daemon thread.
     *
     * <p>If the user calls {@link #setThreadExecutor} custom thread pool, this
     * parameter is invalid.
     *
     * @param daemon {@code true} is a daemon thread, otherwise it is not a daemon thread
     * @since 1.0.3
     */
    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
        setDaemon = true;
    }

    /**
     * Set time zone.
     *
     * @param timeZone time Zone.
     * @since 1.0.3
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        setTimeZone = true;
    }

    /**
     * Set a boolean flag to control whether to clear tasks when closing the scheduler.
     *
     * @param ifStopClearTasks the boolean flag of when stop clear tasks.
     * @since 1.0.3
     */
    public void setIfStopClearTasks(boolean ifStopClearTasks) {
        this.ifStopClearTasks = ifStopClearTasks;
        setIfStopClearTasks = true;
    }

    /**
     * Initialize the scheduled task manager based on the provided attributes.
     *
     * @since 1.0.3
     */
    @Override
    public void initialize() throws Exception {
        super.initialize();
        scheduler = new HutoolScheduler(this);
        scheduler.setDaemon(daemon);
        scheduler.setMatchSecond(isMatchSecond);
        scheduler.setTimeZone(timeZone);
        scheduler.setThreadExecutor(executorService);
        scheduler.addListener(taskListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getName() {
        return "HUTOOL_SCHEDULER@" + super.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkSupportedExpression(@NotNull String expression) throws CronExpressionInvalidException {
        try {
            PatternParser.parse(expression);
        }
        catch (CronException ex) {
            throw new CronExpressionInvalidException(expression, getName(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull Runnable runnable) {
        IDGenerator idGenerator = getIDGenerator();
        if (idGenerator != null) {
            String id = idGenerator.generate();
            getInitializedScheduler().schedule(id, expression, runnable);
            return id;
        }
        return getInitializedScheduler().schedule(expression, runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull CronMethodRunnable runnable) {
        return registerInternal(expression, (Runnable) runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull RunnableTaskBody body) {
        if (body instanceof DefineIDRunnableTaskBody) {
            return registerInternal(expression, (TaskBody) body);
        }
        return registerInternal(expression, body.getRunnable());
    }

    /**
     * {@inheritDoc}
     *
     * @param expression {@inheritDoc}
     * @param body       {@link RunnableTaskBody} or {@link DefineIDRunnableTaskBody} or {@link InvokeTaskBody}
     *                   or {@link SettingTaskBody}.
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull TaskBody body) throws UnsupportedTaskBodyException{
        if (body.isWrapperFor(DefineIDRunnableTaskBody.class)) {
            DefineIDRunnableTaskBody defineIDRunnableTaskBody = body.unwrap(DefineIDRunnableTaskBody.class);
            String id = defineIDRunnableTaskBody.getId();
            Task task = getInitializedScheduler().getTask(id);
            if (task != null) {
                throw new CronInternalException("The task corresponding to id <" + id + "> already exists!");
            }
            getInitializedScheduler().schedule(id, expression, defineIDRunnableTaskBody.getRunnable());
            return id;
        }
        else if (body.isWrapperFor(InvokeTaskBody.class)) {
            InvokeTask invokeTask = body.unwrap(InvokeTaskBody.class).getInvokeTask();
            IDGenerator idGenerator = getIDGenerator();
            if (idGenerator != null) {
                String id = idGenerator.generate();
                getInitializedScheduler().schedule(id, expression, invokeTask);
                return id;
            }
            return getInitializedScheduler().schedule(expression, invokeTask);
        }
        else if (body.isWrapperFor(RunnableTaskBody.class)) {
            return registerInternal(expression, body.unwrap(RunnableTaskBody.class));
        }
        else if (body.isWrapperFor(SettingTaskBody.class)) {
            SettingTaskBody settingTaskBody = body.unwrap(SettingTaskBody.class);
            getInitializedScheduler().schedule(settingTaskBody.getSetting());
            /* the IDs in the order of configuration. */
            return getInitializedScheduler().getTaskTable().getIds().stream()
                    .filter(id -> id.startsWith("id_")).collect(Collectors.joining(","));
        }
        throw new UnsupportedTaskBodyException(body.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull CronTask task) {
        return registerInternal(task.getExpression(), new RunnableTaskBody(task.getRunnable()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<String> getAllRegisteredTaskIds() {
        return getInitializedScheduler().getTaskTable().getIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<String> getAllRunningTaskIds() {
        return ((HutoolScheduler) getInitializedScheduler())
                .getTaskExecutorManager().getExecutors().stream()
                .map(taskExecutor -> taskExecutor.getCronTask().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public Long getNextExecuteTime(@NotNull String id) {
        CronPattern pattern = getInitializedScheduler().getPattern(id);
        if (pattern == null) {
            return null;
        }
        try {
            return pattern.nextMatchAfter(Calendar.getInstance()).getTimeInMillis();
        }
        catch (Exception ex) {
            /**
             * {@code cn.hutool.cron.pattern.matcher.PartMatcher#getMin}
             */
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateInternal(@NotNull String taskId, @NotNull String newExpression) {
        getInitializedScheduler().updatePattern(taskId, new CronPattern(newExpression));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeInternal(@NotNull String taskId) {
        getInitializedScheduler().descheduleWithStatus(taskId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void removeAllInternal() {
        getInitializedScheduler().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCronTaskInfo(@NotNull String id) {
        return getInitializedScheduler().getTask(id) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public CronTaskInfo getCronTaskInfoInternal(@NotNull String id) {
        Task task = scheduler.getTask(id);
        CronPattern pattern = scheduler.getPattern(id);
        if (task == null || pattern == null) {
            return null;
        }
        Runnable runnable = null;
        Object target = null;
        Method method = null;
        try {
            Task sourceTask = task instanceof cn.hutool.cron.task.CronTask ?
                    ((cn.hutool.cron.task.CronTask) task).getRaw() : task;
            if (sourceTask instanceof RunnableTask) {
                runnable = (Runnable) ReflectUtil.getFieldValue(sourceTask, "runnable");
                runnable = unwrapRunnable(runnable);
                if (runnable instanceof CronMethodRunnable) {
                    CronMethodRunnable cmr = (CronMethodRunnable) runnable;
                    target = cmr.getTarget();
                    method = cmr.getMethod();
                }
            } else if (sourceTask instanceof InvokeTask) {
                target = ReflectUtil.getFieldValue(sourceTask, "obj");
                method = (Method) ReflectUtil.getFieldValue(sourceTask, "method");
            }
        }
        catch (UtilException ignored) {
        }
        if (runnable == null) runnable = task::execute;
        return new CronTaskInfo(id, pattern.toString(), runnable, target, method);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void terminateInternal(@NotNull String id) {
        taskListener.removeRunningThreads(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void terminateAllInternal() {
        taskListener.removeAllRunningThreads();
    }

    @Override
    @NotNull
    protected CronListenerCollector getCronListenerCollector() {
        return taskListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (isStarted()) {
            throw new IllegalStateException("Scheduler has been started, please stop it first!");
        }
        getInitializedScheduler().start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (!isStarted()) {
            throw new IllegalStateException("Scheduler not started !");
        }
        getInitializedScheduler().stop(ifStopClearTasks);
        closeMonitoringExecutor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return getInitializedScheduler().isStarted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reStart() {
        if (isStarted()) {
            stop();
            start();
        }
        else {
            start();
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

    @NotNull
    @Override
    protected Runnable asRunnable(@NotNull TaskBody body) throws UnsupportedTaskBodyException {
        if (body instanceof InvokeTaskBody) {
            return () -> ((InvokeTaskBody) body).getInvokeTask().execute();
        }

        return super.asRunnable(body);
    }
}
