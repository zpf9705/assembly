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

import top.osjf.cron.core.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * The {@code CronTaskInfo} class encapsulates the relevant information of
 * a scheduled task.
 *
 * <p>This class is used to store and manage the basic information of a
 * scheduled task, including the task ID, execution expression, runnable,
 * target object, method instance, and extended parameters.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class CronTaskInfo implements Serializable {

    private static final long serialVersionUID = 3944766838390077158L;
    /**
     * The unique registration ID for this scheduled task.
     */
    private final String id;
    /**
     * The determination expression for the registration execution frequency of
     * this scheduled task.
     * <p>In general, it is a valid cron expression, but in extended scenarios,
     * it is an exclusive expression used for it.
     */
    private final String expression;
    /**
     * The {@link Runnable} runtime encapsulated by this scheduled task.
     */
    private final Runnable runnable;
    /**
     * The target audience for this scheduled task execution.
     * <p>
     * Static call or anonymous inner class of {@link Runnable}, this value may
     * be null.
     */
    @Nullable
    private final Object target;
    /**
     * The method instance for executing the target object of this scheduled task.
     * <p>
     * If it is an anonymous inner class {@link Runnable}, then this value is null.
     */
    @Nullable
    private final Method method;
    /**
     * The remaining extension parameters can be customized by the user.
     */
    @Nullable
    private Object[] args;

    /**
     * The remaining number of runs for this task.
     * @since 3.0.1
     */
    private final long remainingNumberOfRuns;

    /**
     * Constructs a {@code CronTaskInfo} with any task info.
     * @param id                        {@link #id}
     * @param expression                {@link #expression}
     * @param runnable                  {@link #runnable}
     */
    public CronTaskInfo(String id, String expression, Runnable runnable) {
        this(id, expression, runnable, null, null, -1);
    }

    /**
     * Constructs a {@code CronTaskInfo} with any task info.
     * @param id                        {@link #id}
     * @param expression                {@link #expression}
     * @param runnable                  {@link #runnable}
     * @param target                    {@link #target}
     * @param method                    {@link #method}
     * @param remainingNumberOfRuns     {@link #remainingNumberOfRuns}
     */
    public CronTaskInfo(String id, String expression, Runnable runnable, @Nullable Object target,
                        @Nullable Method method, long remainingNumberOfRuns) {
        this.id = id;
        this.expression = expression;
        this.runnable = runnable;
        this.target = target;
        this.method = method;
        this.remainingNumberOfRuns = remainingNumberOfRuns;
    }

    /**
     * Set the remaining extension parameter array for this {@code CronTaskInfo}.
     *
     * @param args the remaining extension parameter array.
     */
    public void setArgs(@Nullable Object[] args) {
        this.args = args;
    }

    /**
     * Get the corresponding parameter in the extension parameter array based on
     * the specified class type.
     *
     * @param clazz the type of the parameter to retrieve.
     * @param <T>   the generic of the parameter to retrieve.
     * @return The found parameter of the specified type, or null if not found.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getArg(Class<T> clazz) {
        if (args == null || args.length < 1) {
            return null;
        }
        for (Object arg : args) {
            if (clazz.isInstance(arg)) {
                return (T) arg;
            }
        }
        return null;
    }

    /**
     * @return {@link #id}
     */
    public String getId() {
        return id;
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
     * @return {@link #remainingNumberOfRuns}
     */
    public long getRemainingNumberOfRuns() {
        return remainingNumberOfRuns;
    }
}
