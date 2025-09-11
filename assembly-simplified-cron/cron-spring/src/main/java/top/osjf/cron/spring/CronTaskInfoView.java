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


package top.osjf.cron.spring;

import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.repository.CronTaskInfo;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * The return obj of the web access view for {@link CronTaskInfo}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public final class CronTaskInfoView implements Serializable {

    private static final long serialVersionUID = -1770339670512525063L;
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
     * The fully qualified name of the type of the scheduled task execution object.
     */
    @Nullable
    private String sourceClassName;
    /**
     * Description of the method for executing the scheduled task object.
     */
    @Nullable
    private String sourceMethod;

    /**
     * The remaining number of runs for this task.
     * <p> The unlimited number of times is {@code -1}, and there are no tasks with {@code 0}.
     * Otherwise, it is the remaining number of runs.
     * @since 3.0.1
     */
    private final long remainingNumberOfRuns;

    /**
     * Creates a new {@link CronTaskInfoView} by given {@link CronTaskInfo}.
     *
     * @param cronTaskInfo the given {@link CronTaskInfo} obj.
     */
    public CronTaskInfoView(CronTaskInfo cronTaskInfo) {
        this.id = cronTaskInfo.getId();
        this.expression = cronTaskInfo.getExpression();
        Method mod = cronTaskInfo.getMethod();
        if (mod != null) {
            this.sourceClassName = mod.getDeclaringClass().getName();
            this.sourceMethod = mod.toString();
        }
        this.remainingNumberOfRuns = cronTaskInfo.getRemainingNumberOfRuns();
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
     * @return {@link #sourceClassName}
     */
    @Nullable
    public String getSourceClassName() {
        return sourceClassName;
    }

    /**
     * @return {@link #sourceMethod}
     */
    @Nullable
    public String getSourceMethod() {
        return sourceMethod;
    }

    /**
     * @return {@link #remainingNumberOfRuns}
     */
    public long getRemainingNumberOfRuns() {
        return remainingNumberOfRuns;
    }
}
