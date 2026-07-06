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

import com.google.gson.annotations.Expose;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.Assert;
import top.osjf.cron.core.util.GsonUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class encapsulates the complete runtime metadata of a cron scheduled task,
 * which is used for task runtime management, governance control, execution monitoring
 * and metadata persistence.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class CronTaskInfo implements Serializable {

    private static final long serialVersionUID = 3944766838390077158L;

    /**  The unique ID of this task within its lifetime. */
    private final String id;

    /**
     * The custom name for this task.
     * @since 3.0.2
     */
    @Nullable private String name;

    /** The cron expression for executing this task.*/
    private final String expression;

    /** The function runtime for this task.*/
    @Expose(serialize = false, deserialize = false)
    private final Runnable runnable;

    /** The target object for this task execution.*/
    @Nullable private final Object target;

    /** The target method for executing this task.*/
    @Nullable
    private final Method method;

    /** The accompanying parameters for executing this task.*/
    @Nullable private Object[] args;

    /**
     * The remaining number of runs for this task.
     * <p> The unlimited number of times is {@code -1}, and there are no tasks with {@code 0}.
     * Otherwise, it is the remaining number of runs.
     * @since 3.0.1
     */
    private long remainingNumberOfRuns;

    /**
     * An instance of timeout configuration for a single task run.
     * @since 3.0.2
     */
    @Nullable
    private RunningTimeout timeoutConfig;

    /**
     * The {@code boolean} flag to indicate whether the task is currently executing.
     * @since 3.0.2
     */
    private boolean isRunning;

    /**
     * Next scheduled execution timestamp of the task, unit: milliseconds.
     * Returns {@code null} if there is no subsequent trigger.
     * @since 3.0.2
     */
    @Nullable private Long nextExecuteTimestamp;

    /**
     * The {@code boolean} to indicate whether concurrent execution is prohibited.
     * @since 3.0.2
     */
    private boolean disallowConcurrentExecution;

    /**
     * The description of the role of this task
     * @since 3.0.2
     */
    @Nullable private String description;

    /**
     * Construct a scheduled task metadata instance for common {@link Runnable} type tasks.
     * <p>This constructor is applicable to anonymous task scenarios without binding target
     * objects and execution methods.
     *
     * @param id         Unique task identifier
     * @param expression Cron trigger expression
     * @param runnable   Task execution logic carrier
     */
    public CronTaskInfo(String id, String expression, Runnable runnable) {
        this(id, expression, runnable, null, null);
    }

    /**
     * Construct a scheduled task metadata instance for method-type scheduled tasks.
     * <p>Applicable to task scenarios parsed from annotation methods, which can record target
     * beans and execution method metadata.
     *
     * @param id         Unique task identifier
     * @param expression Cron trigger expression
     * @param runnable   Task execution logic carrier
     * @param target     Target bean instance of the execution method
     * @param method     Reflected target execution method
     */
    public CronTaskInfo(String id, String expression, Runnable runnable,
                        @Nullable Object target, @Nullable Method method) {

        Assert.hasText(id, "id must not be null or blank");
        Assert.hasText(expression, "expression must not be null or blank");
        Assert.notNull(runnable, "source Runnable not be null");

        this.id = id;
        this.expression = expression;
        this.runnable = runnable;
        this.target = target;
        this.method = method;
    }

    /**
     * Set the custom task name.
     * @param name the custom task name.
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * Set the remaining number of runs  for this {@code CronTaskInfo}.
     *
     * @param remainingNumberOfRuns the remaining number of runs for this task.
     * @since 3.0.1
     */
    public void setRemainingNumberOfRuns(long remainingNumberOfRuns) {
        this.remainingNumberOfRuns = remainingNumberOfRuns;
    }

    /**
     * Set a timeout configuration instance for the {@code CronTaskInfo} task to run once.
     * @param timeoutConfig the timeout configuration instance.
     * @since 3.0.2
     */
    public void setTimeoutConfig(@Nullable RunningTimeout timeoutConfig) {
        this.timeoutConfig = timeoutConfig;
    }

    /**
     * Set the remaining extension parameter array for this {@code CronTaskInfo}.
     * @param args the remaining extension parameter array.
     */
    public void setArgs(@Nullable Object[] args) {
        this.args = args;
    }

    /**
     * Set whether the task is running.
     * @param running task running status
     * @since 3.0.2
     */
    public void setRunning(boolean running) {
        isRunning = running;
    }

    /**
     * Set the next task execution timestamp (milliseconds).
     * @param nextExecuteTimestamp next execution time in milliseconds, may be {@code null}
     * @since 3.0.2
     */
    public void setNextExecuteTimestamp(@Nullable Long nextExecuteTimestamp) {
        this.nextExecuteTimestamp = nextExecuteTimestamp;
    }

    /**
     * Set a flag to indicate whether concurrent execution is prohibited.
     * @param disallowConcurrentExecution flag to indicate whether concurrent execution is prohibited.
     * @since 3.0.2
     */
    public void setDisallowConcurrentExecution(boolean disallowConcurrentExecution) {
        this.disallowConcurrentExecution = disallowConcurrentExecution;
    }

    /**
     * Set the description of the role of this task
     * @param description the description of the role of this task
     * @since 3.0.2
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * @return {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * @return {@link #id}
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * @return {@link #expression}
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @return {@link #runnable}
     */
    @Nullable
    public Runnable getRunnable() {
        return runnable;
    }

    /**
     * @return {@link #target}
     */
    @Nullable
    public Object getTarget() {
        return target;
    }

    /**
     * @return {@link #method}
     */
    @Nullable
    public Method getMethod() {
        return method;
    }

    /**
     * @return {@link #args}
     */
    @Nullable
    public Object[] getArgs() {
        return args;
    }

    /**
     * @return {@link #remainingNumberOfRuns}
     */
    public long getRemainingNumberOfRuns() {
        return remainingNumberOfRuns;
    }

    /**
     * @return {@link #timeoutConfig}
     */
    @Nullable
    public RunningTimeout getTimeoutConfig() {
        return timeoutConfig;
    }

    /**
     * @return {@link #isRunning}
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * @return {@link #nextExecuteTimestamp}
     */
    @Nullable
    public Long getNextExecuteTimestamp() {
        return nextExecuteTimestamp;
    }

    /**
     * @return {@link #disallowConcurrentExecution}
     */
    public boolean isDisallowConcurrentExecution() {
        return disallowConcurrentExecution;
    }

    /**
     * @return {@link #description}
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    public String toJsonView() {
        return GsonUtils.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CronTaskInfo that = (CronTaskInfo) o;
        return Objects.equals(expression, that.expression)
                && Objects.equals(target, that.target)
                && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(expression, target, method);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    public String toString() {
        return "CronTaskInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", expression='" + expression + '\'' +
                ", runnable=" + runnable +
                ", target=" + target +
                ", method=" + method +
                ", args=" + Arrays.toString(args) +
                ", remainingNumberOfRuns=" + remainingNumberOfRuns +
                ", timeoutConfig=" + timeoutConfig +
                ", isRunning=" + isRunning +
                ", nextExecuteTimestamp=" + nextExecuteTimestamp +
                ", disallowConcurrentExecution=" + disallowConcurrentExecution +
                ", description='" + description + '\'' +
                '}';
    }
}
