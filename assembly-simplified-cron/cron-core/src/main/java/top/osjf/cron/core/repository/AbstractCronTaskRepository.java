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


package top.osjf.cron.core.repository;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.LongTaskTimer;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.Assert;
import top.osjf.commons.util.compat.ArrayUtils;
import top.osjf.cron.core.exception.*;
import top.osjf.cron.core.micrometer.MeterRegistryDelegation;
import top.osjf.cron.core.micrometer.MeterRegistryDetector;
import top.osjf.cron.core.micrometer.SystemPropertiesTags;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static top.osjf.cron.core.micrometer.RepositoryMicrometerConstants.*;

/**
 * This abstract class encapsulates the unified public logic of all cron task repository
 * implementations: parameter pre-verification, unified callback execution after task
 * registration, default implementation of general capability methods defined in
 * {@link CronTaskRepository}, including expression validity judgment, remaining execution
 * times query, task timeout configuration acquisition, task metadata supplementation and
 * wrapping Runnable unwrapping.
 *
 * <p>
 * It splits the task operation logic into two layers:
 * <ol>
 * <li>External public methods: complete unified parameter check, general cross-cutting logic
 * such as callback notification;</li>
 * <li>Protected abstract {@code xxxInternal} methods: only responsible for the underlying task
 * storage, modification, deletion and query operations, which need to be implemented by specific
 * scheduling framework repository subclasses.</li>
 * </ol>
 *
 * <p>
 * Implements the {@link top.osjf.commons.ability.Nameable} capability by default, using the fully
 * qualified class name of the subclass as the unique repository name for log tracking, exception
 * location and monitoring statistics.
 *
 * <p>Write operations including task registration, update, removal and task termination are equipped
 * with Micrometer counter metrics tagged with method signatures, which can record invocation frequency
 * for runtime traffic analysis and fault location. This class also provides long-task monitoring based
 * on {@link io.micrometer.core.instrument.LongTaskTimer} to track task execution duration, real-time
 * concurrency and blocking latency, facilitating the discovery of online risks such as thread deadlock,
 * IO blocking and task backlog.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class AbstractCronTaskRepository
        extends AbstractRunTimeoutRegistrarRepository implements CronTaskRepository {

    /** Key used to store task business name in default basic extend attribute group. */
    public static final String EXTEND_INFO_OF_NAME = "CRON_TASK_NAME";

    /** Key used to store task business description in default basic extend attribute group.*/
    public static final String EXTEND_INFO_OF_DESCRIPTION = "CRON_TASK_DESCRIPTION";

    /** A unique identity list record that prohibits concurrent scheduling of individual tasks. */
    private final CopyOnWriteArrayList<String> disallowConcurrentExecutionIds = new CopyOnWriteArrayList<>();

    /** Provide a custom task unique ID generator. */
    @Nullable private IDGenerator idGenerator;

    /** Thread-safe multi-dimensional extended attribute storage container for custom business metadata. */
    private final ConcurrentMap<String, CronTaskExtendInfo> extendInfos = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "register(String expression, Runnable runnable)"},
            description = "Counts the number of cron task registration invocations")
    @SystemPropertiesTags
    public String register(String expression, Runnable runnable) throws CronInternalException {

        Assert.hasText(expression, "expression must not be null or blank");
        Assert.notNull(runnable, "Runnable must not be null");

        checkSupportedExpression(expression);
        String id;
        try {
            id = registerInternal(expression, runnable);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
        call(expression, runnable, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "register(String expression, CronMethodRunnable runnable)"},
            description = "Counts the number of cron task registration invocations")
    @SystemPropertiesTags
    public String register(String expression, CronMethodRunnable runnable) throws CronInternalException {

        Assert.hasText(expression, "expression must not be null or blank");
        Assert.notNull(runnable, "CronMethodRunnable must not be null");

        checkSupportedExpression(expression);
        String id;
        try {
            id = registerInternal(expression, runnable);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
        call(expression, runnable, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "register(String expression, RunnableTaskBody body)"},
            description = "Counts the number of cron task registration invocations")
    @SystemPropertiesTags
    public String register(String expression, RunnableTaskBody body) throws CronInternalException {

        Assert.hasText(expression, "expression must not be null or blank");
        Assert.notNull(body, "RunnableTaskBody must not be null");

        checkSupportedExpression(expression);
        String id;
        try {
            id = registerInternal(expression, body);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
        call(expression, body, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "register(String expression, TaskBody body)"},
            description = "Counts the number of cron task registration invocations")
    @SystemPropertiesTags
    public String register(String expression, TaskBody body) throws CronInternalException {

        Assert.hasText(expression, "expression must not be null or blank");
        Assert.notNull(body, "TaskBody must not be null");

        checkSupportedExpression(expression);
        String id;
        try {
            id = registerInternal(expression, body);
        }
        catch (UnsupportedTaskBodyException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
        call(expression, body, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY, "register(CronTask task)"},
            description = "Counts the number of cron task registration invocations")
    @SystemPropertiesTags
    public String register(CronTask task) throws CronInternalException {

        Assert.notNull(task, "CronTask must not be null");

        checkSupportedExpression(task.getExpression());
        String id;
        try {
            id = registerInternal(task);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
        call(task, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = UPDATE_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY, "update(String id, String newExpression)"},
            description = "Counts the number of cron task update invocations")
    @SystemPropertiesTags
    public void update(String id, String newExpression) throws CronInternalException {

        Assert.hasText(id, "id must not be null or blank");
        Assert.hasText(newExpression, "newExpression must not be null or blank");

        checkSupportedExpression(newExpression);
        try {
            updateInternal(id, newExpression);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REMOVE_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY, "remove(String id)"},
            description = "Counts the number of cron task remove invocations")
    @SystemPropertiesTags
    public void remove(String id) throws CronInternalException {
        Assert.hasText(id, "id must not be null or blank");

        try {
            removeInternal(id);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REMOVE_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY, "removeAll()"},
            description = "Counts the number of cron task remove invocations")
    @SystemPropertiesTags
    public void removeAll() throws CronInternalException {
        try {
            removeAllInternal();
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(
            value = TERMINATE_COUNTER_KEY,
            extraTags = {
                    METHOD_SIGNATURE_TAG_KEY, "terminate(String id)"},
            description = "Counts the number of cron task terminate invocations"
    )
    @SystemPropertiesTags
    public void terminate(String id) throws CronInternalException {
        Assert.hasText(id, "id must not be null or blank");

        try {
            if (!isTaskRunning(id)) {
                return;
            }
            terminateInternal(id);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(
            value = TERMINATE_COUNTER_KEY,
            extraTags = {
                    METHOD_SIGNATURE_TAG_KEY, "terminateAll()"},
            description = "Counts the number of cron task terminate invocations"
    )
    @SystemPropertiesTags
    public void terminateAll() throws CronInternalException {
        try {
            if (getAllRunningTaskIds().isEmpty()) {
                return;
            }
            terminateAllInternal();
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCronTaskInfo(@NotNull String id) {
        Assert.hasText(id, "id must not be null or blank");
        return getAllRegisteredTaskIds().contains(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskInfo getCronTaskInfo(String id) {
        Assert.hasText(id, "id must not be null or blank");

        return Optional.ofNullable(getCronTaskInfoInternal(id))
                .map(this::customizeCronTaskInfo)
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CronTaskInfo> getAllCronTaskInfos() {
        return getAllRegisteredTaskIds().stream()
                .map(this::getCronTaskInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTaskRunning(@NotNull String id) {
        Assert.hasText(id, "id must not be null or blank");
        return getAllRunningTaskIds().contains(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Long> getNextExecuteTimes(Collection<String> ids) {
        Assert.notEmpty(ids, "ids must not be empty");

        Map<String, Long> result = new HashMap<>(ids.size());
        for (String id : ids) {
            result.put(id, getNextExecuteTime(id));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportedExpression(String expression) {
        Assert.hasText(expression, "expression must not be null or blank");

        try {
            checkSupportedExpression(expression);
            return true;
        }
        catch (CronExpressionInvalidException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTaskRemainingNumberOfRuns(String id) {
        Assert.hasText(id, "id must not be null or blank");

        AtomicInteger count = getTaskRunTimesMap().getOrDefault(id, null);
        return count == null ? hasCronTaskInfo(id) ? -1 : 0 : count.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRemainingLimitedRunTimesTaskCount() {
        return getTaskRunTimesMap().size();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public RunningTimeout getTimeoutConfig(String id) {
        Assert.hasText(id, "id must not be null or blank");

        return getTaskRunTimeoutMap().getOrDefault(id, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskInfo customizeCronTaskInfo(CronTaskInfo cronTaskInfo) {
        Assert.notNull(cronTaskInfo, "cronTaskInfo must not be null");

        String id = cronTaskInfo.getId();
        // Setting remaining number of runs.
        cronTaskInfo.setRemainingNumberOfRuns(getTaskRemainingNumberOfRuns(id));
        // Setting running timeout config.
        cronTaskInfo.setTimeoutConfig(getTimeoutConfig(id));
        // Setting running state.
        cronTaskInfo.setRunning(isTaskRunning(id));
        // Setting next execute timestamp.
        cronTaskInfo.setNextExecuteTimestamp(getNextExecuteTime(id));
        // Setting whether concurrent execution is prohibited.
        cronTaskInfo.setDisallowConcurrentExecution(hasDisallowConcurrentExecution(id));
        // Setting extend info.
        CronTaskExtendInfo extendInfo = getExtendInfo(id);
        // Setting extend name info.
        cronTaskInfo.setName(extendInfo.getString(EXTEND_INFO_OF_NAME));
        // Setting extend description info.
        cronTaskInfo.setDescription(extendInfo.getString(EXTEND_INFO_OF_DESCRIPTION));
        return cronTaskInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Runnable unwrapRunnable(Runnable given) {
        Assert.notNull(given, "Runnable must not be null");

        if (given instanceof TimeoutMonitoringRunnable) {
            return ((TimeoutMonitoringRunnable) given).getReal();
        }
        return given;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportConcurrentExecution() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDisallowConcurrentExecution(String id) {
        Assert.hasText(id, "id must not be null or blank");

        return disallowConcurrentExecutionIds.contains(id);
    }

    /**
     * Judge whether to allow the current scheduled task trigger to execute.
     * <p>The task will be permitted to run if either of the following two conditions is satisfied:
     * <ul>
     * <li>The task is not configured to disallow concurrent execution;</li>
     * <li>The task enables disallow-concurrent restriction, but there is no running task instance
     * currently.</li>
     * </ul>
     * Only when the task prohibits concurrent execution and the previous task is still running,
     * the current trigger will be rejected.
     *
     * @param id Unique identifier of the target scheduled task
     * @return {@code true} if task execution is allowed; {@code false} if concurrent conflict occurs
     * and current trigger needs to be skipped
     */
    public boolean shouldAllowTaskExecute(String id) {
        Assert.hasText(id, "id must not be null or blank");

        boolean shouldAllowTaskExecute = !hasDisallowConcurrentExecution(id) || !isTaskRunning(id);
        if (shouldAllowTaskExecute) {
            logger.warn("Task [{}] disallow concurrent execution and previous task is running, " +
                    "skip current trigger", id);
        }
        return shouldAllowTaskExecute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disallowConcurrentExecution(String id) throws NotSupportConcurrentExecutionException {
        Assert.hasText(id, "id must not be null or blank");

        if (!isSupportConcurrentExecution()) {
            throw new NotSupportConcurrentExecutionException(this);
        }
        if (!hasCronTaskInfo(id)) {
            throw new NotSupportConcurrentExecutionException
                    (String.format("Scheduled task with id [%s] does not exist.", id));
        }
        disallowConcurrentExecutionIds.addIfAbsent(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelDisallowConcurrentExecution(String id) throws CannotCancelConcurrentException {
        Assert.hasText(id, "id must not be null or blank");

        CronTaskInfo cronTaskInfo = getCronTaskInfo(id);
        if (cronTaskInfo == null) {
            throw new CannotCancelConcurrentException(String.format("Cannot cancel disallow-concurrent constraint, " +
                    "scheduled task with id [%s] does not exist.", id));
        }
        Method method = cronTaskInfo.getMethod();
        if (method != null && method.isAnnotationPresent(DisallowConcurrentExecution.class)) {
            throw new CannotCancelConcurrentException(
                    String.format("Cannot cancel disallow-concurrent constraint, task [%s] uses " +
                            "@DisallowConcurrentExecution static annotation rule which cannot be dynamically revoked.",
                            id));
        }
        if (!hasDisallowConcurrentExecution(id)) {
            throw new CannotCancelConcurrentException(String.format("Cannot cancel disallow-concurrent constraint, " +
                    "no dynamic concurrency restriction is registered for task [%s].", id));
        }
        disallowConcurrentExecutionIds.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIDGenerator(@Nullable IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public IDGenerator getIDGenerator() {
        return idGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LongTimedExecutor longTimed(String... tags) {

        Assert.state(MeterRegistryDetector.isPresent(null),
                "Micrometer MeterRegistry is required, please introduce micrometer-core dependency.");

        Optional<LongTaskTimer> timer
                = MeterRegistryDelegation.longTaskTimer(TASK_BODY_EXECUTION_TIMER_KEY,
                // Description is the name of the resource client carrier where the task runs...
                String.format("Tasks whose scheduling capability is provided by the {%s} " +
                        "repository client", getName()),
                // Fixed with the name of the current resource model attached...
                ArrayUtils.addAll(tags, // the customized tags
                        // the module name tag key-value.
                        MODULE_TAG_KEY, getName(),
                        // the wrapper runnable body tag key-value
                        WRAPPER_RUNNABLE_TYPE_TAG_KEY, runBodyWrapperClassName())
        );

        return new LongTimedExecutor() {

            @Nullable private LongTaskTimer.Sample sample;

            private boolean started;

            @Override
            public void start() {
                sample = MeterRegistryDelegation
                        .startLongTaskTimer(timer.orElse(null)).orElse(null);
                started = true;
            }

            /**
             * @throws IllegalStateException if {@link #start() Start action} has not been executed.
             */
            @Override
            public void stop() {
                Assert.state(started, "Start action has not been executed.");
                if (sample != null) {
                    MeterRegistryDelegation.stopSample(sample);
                }
            }

            @Override
            public void record(Runnable runnable) {
                start();
                try {
                    runnable.run();
                }
                finally {
                    stop();
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Builder newBuilder() {
        return CronTaskBuilder.forRepository(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TaskBody>[] getSupportTaskBodyClasses() {
        return new Class[] { RunnableTaskBody.class };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public CronTaskExtendInfo getExtendInfo(String id) {
        Assert.hasText(id, "id must not be null or blank");

        if (!hasCronTaskInfo(id)) {
            throw new CronInternalException(String.format("Scheduled task with id [%s] does not exist.", id));
        }
        return extendInfos.computeIfAbsent(id, this::newCronTaskExtendInfo);
    }

    /**
     * The class object name that runs the body wrapper returns {@link Runnable java.lang.Runnable}
     * by default.
     * @return The class object name that runs the body wrapper.
     */
    protected String runBodyWrapperClassName() {
        return "java.lang.Runnable";
    }

    /**
     * Create a new {@code CronTaskExtendedInfo} based on the ID.
     * @param id the unique identifier of the target scheduled task
     * @return the new {@code CronTaskExtendInfo}.
     */
    protected CronTaskExtendInfo newCronTaskExtendInfo(String id) {
        return new CronTaskExtendInfo(id);
    }

    /**
     * Underlying internal registration method for {@link Runnable} scheduled task.
     * <p>
     * All input parameters have passed legality verification and framework expression rule verification
     * in the outer public method.
     * Subclasses implement the task persistence and scheduling registration logic for the current framework.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param expression Valid cron or periodic expression that passed framework verification
     * @param runnable Original executable task body
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     */
    protected abstract String registerInternal(String expression, Runnable runnable) throws Exception;

    /**
     * Underlying internal registration method for {@link CronMethodRunnable} scheduled task.
     * <p>
     * All input parameters have passed legality verification and framework expression rule verification
     * in the outer public method.
     * Subclasses implement the task persistence and scheduling registration logic for the current framework.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param expression Valid cron or periodic expression that passed framework verification
     * @param runnable Method reflection wrapped task executable body
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     */
    protected abstract String registerInternal(String expression, CronMethodRunnable runnable) throws Exception;

    /**
     * Underlying internal registration method for {@link RunnableTaskBody} scheduled task.
     * <p>
     * All input parameters have passed legality verification and framework expression rule verification
     * in the outer public method.
     * Subclasses implement the task persistence and scheduling registration logic for the current framework.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param expression Valid cron or periodic expression that passed framework verification
     * @param body Runnable encapsulated task metadata and execution body
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     */
    protected abstract String registerInternal(String expression, RunnableTaskBody body) throws Exception;

    /**
     * Underlying internal registration method for generic {@link TaskBody} custom task body.
     * <p>
     * All input parameters have passed legality verification and framework expression rule verification
     * in the outer public method. Subclasses implement the task persistence and scheduling registration
     * logic for the current framework.Any runtime or checked exception thrown during implementation will
     * be uniformly wrapped into {@link CronInternalException}.
     *
     * @param expression Valid cron or periodic expression that passed framework verification
     * @param body Generic custom encapsulated task body
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     * @throws UnsupportedTaskBodyException Thrown when the current scheduling framework does not support
     * the incoming {@link TaskBody} type
     */
    protected abstract String registerInternal(String expression, TaskBody body) throws Exception;

    /**
     * Underlying internal registration method for complete {@link CronTask} metadata object.
     * <p>
     * The complete task metadata has completed parameter verification in the outer public method.
     * Subclasses store the full task metadata and complete framework scheduling registration.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param task Complete encapsulated cron task metadata
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     */
    protected abstract String registerInternal(CronTask task) throws Exception;

    /**
     * Underlying internal update method for modifying the cron expression of a specified task.
     * <p>
     * The task ID and new expression have passed parameter and framework rule verification in the
     * outer public method.
     * Subclasses implement the logic of updating the scheduled expression bound to the task.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param id Unique identifier of the target task to be updated
     * @param newExpression New valid cron expression that passed framework verification
     * @throws Exception Any exception thrown by the underlying scheduling framework during update
     */
    protected abstract void updateInternal(String id, String newExpression) throws Exception;

    /**
     * Underlying internal deletion method for releasing a scheduled task and its runtime resources.
     * <p>
     * The task ID has passed non-null parameter verification in the outer public method.
     * Subclasses implement task metadata deletion and framework resource release logic.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param id Unique identifier of the target task to be removed
     * @throws Exception Any exception thrown by the underlying scheduling framework during deletion
     */
    protected abstract void removeInternal(String id) throws Exception;

    /**
     * Underlying internal batch deletion method to clear all registered scheduled tasks and release
     * all runtime resources held by the current repository.
     * <p>
     * This method is invoked after the outer public {@link #removeAll()} method, no parameter verification
     * is required in subclasses.Any runtime or checked exception thrown during implementation will be
     * uniformly caught and wrapped into {@link CronInternalException}.
     *
     * @throws Exception Any exception thrown by the underlying scheduling framework during batch task
     * removal and resource recycling
     */
    protected abstract void removeAllInternal() throws Exception;

    /**
     * Underlying internal query method to obtain the detailed information of the specified registered
     * cron task.
     * <p>
     * After the underlying implementation obtains {@link CronTaskInfo}, there is no need to manually
     * call {@link #customizeCronTaskInfo(CronTaskInfo)} to assign specific dynamic information, as it
     * will be uniformly processed within {@link #getCronTaskInfo(String)}.
     *
     * @param id unique identifier of the target registered cron task
     * @return detailed information of the specified cron task; returns {@code null} if the task does
     * not exist
     */
    @Nullable
    protected abstract CronTaskInfo getCronTaskInfoInternal(String id);

    /**
     * Underlying internal method to terminate the currently executing instance of the specified cron task.
     * <p>
     * This method is invoked after parameter verification in the outer public method,
     * no parameter check is required in subclasses. Only the running task thread will be interrupted,
     * the task registration information and cron scheduling configuration will be retained,
     * and the task can still be triggered normally at the next scheduled time.
     * Any runtime or checked exception thrown during implementation will be uniformly caught and wrapped
     * into {@link CronInternalException}.
     *
     * @param id unique identifier of the target registered cron task
     * @throws Exception Any exception thrown by the underlying scheduling framework during task thread
     * termination
     */
    protected abstract void terminateInternal(String id) throws Exception;

    /**
     * Underlying internal batch method to terminate all currently executing cron task instances.
     * <p>
     * This method is invoked after parameter verification in the outer public method,
     * no parameter check is required in subclasses. All ongoing task threads will be interrupted,
     * while all task metadata and cron scheduling rules remain unchanged without deletion.
     * Any runtime or checked exception thrown during implementation will be uniformly caught and wrapped
     * into {@link CronInternalException}.
     *
     * @throws Exception Any exception thrown by the underlying scheduling framework during batch task
     * thread termination
     */
    protected abstract void terminateAllInternal() throws Exception;
}
