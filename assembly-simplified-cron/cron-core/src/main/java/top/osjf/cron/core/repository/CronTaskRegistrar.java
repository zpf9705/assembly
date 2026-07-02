/*
 * Copyright 2025-? the original author or authors.
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

import top.osjf.commons.util.Assert;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.NotSupportConcurrentExecutionException;

import java.lang.reflect.Method;

/**
 * Registrar component for registering annotated cron scheduled tasks.
 *
 * <p>This class resolves task governance annotations marked on target methods,
 * including {@link RunTimes} (limited execution count), {@link RunTimeout}
 * (single task execution timeout), and {@link DisallowConcurrentExecution}
 * (concurrent execution prohibition). It encapsulates various annotation combination
 * registration branches and completes unified task registration to
 * {@link CronTaskRepository}.
 *
 * <p><strong>Usage Constraint:</strong> The wrapped {@link CronTask} must be constructed
 * from {@link CronMethodRunnable} bound to a Java method.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class CronTaskRegistrar {

    /** the pending registration {@link CronTask} .*/
    private final CronTask cronTask;

    /** the target method .*/
    private final Method targetMethod;

    /**
     * Creates a {@code CronTaskRegistrar} bound to the specified cron task metadata.
     *
     * @param cronTask the cron task metadata to be registered, must not be {@code null};
     *                 must be created via method-bound {@link CronMethodRunnable}
     */
    public CronTaskRegistrar(CronTask cronTask) {

        Assert.notNull(cronTask, "CronTask not be null");

        this.cronTask = cronTask;
        this.targetMethod = cronTask.getRunnable().getMethod();
    }

    /**
     * Resolves annotations on the bound target method and registers the current cron task
     * into the specified {@link CronTaskRepository}.
     *
     * <p>Registration process:
     * <ol>
     * <li>Parses {@link RunTimes} to confirm whether to enable limited execution scheduling;</li>
     * <li>Parses {@link RunTimeout} to encapsulate single-task execution timeout configuration;</li>
     * <li>Selects the matching overloaded register method according to the above two annotations;</li>
     * <li>If {@link DisallowConcurrentExecution} is marked, dynamically binds the concurrency prohibition
     * constraint to the registered task after successful registration.</li>
     * </ol>
     *
     * @param cronTaskRepository the task repository for registering cron tasks, must not be {@code null}
     * @return the globally unique task registration ID generated after successful task registration
     * @throws CronInternalException               thrown when cron parsing fails, registration conflict
     *                                              or internal scheduling error occurs.
     * @throws NotSupportConcurrentExecutionException if the underlying scheduler does not support concurrent
     *                                               execution when enabling the concurrency prohibition constraint.
     */
    public String registerFor(CronTaskRepository cronTaskRepository) {

        Assert.notNull(cronTaskRepository, "CronTaskRepository not be null");

        RunTimes runTimes = targetMethod.getAnnotation(RunTimes.class);
        RunTimeout runTimeout = targetMethod.getAnnotation(RunTimeout.class);

        RunningTimeout runningTimeout = null;
        if (runTimeout != null) {
            runningTimeout = new RunningTimeout(runTimeout.timeout(), runTimeout.timeUnit(), runTimeout.policy());
        }

        String id;
        if (runTimes != null) {
            if (runningTimeout != null) {
                id = cronTaskRepository.registerRunTimes(cronTask, runTimes.value(), runningTimeout);
            }
            else {
                id = cronTaskRepository.registerRunTimes(cronTask, runTimes.value());
            }
        }
        else {
            if (runningTimeout != null) {
                id = cronTaskRepository.register(cronTask, runningTimeout);
            }
            else {
                id = cronTaskRepository.register(cronTask);
            }
        }

        if (targetMethod.isAnnotationPresent(DisallowConcurrentExecution.class)) {
            cronTaskRepository.disallowConcurrentExecution(id);
        }

        return id;
    }
}
