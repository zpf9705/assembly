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

import java.lang.reflect.Method;

/**
 * This class is responsible for registering methods annotated with {@code CronTask}
 * and processing associated annotations such as {@link RunTimes} (for execution count
 * limits) and {@link RunTimeout} (for execution timeout control).
 *
 * <p> During registration, it checks whether the target method is annotated with these
 * annotations and registers the corresponding configuration into the {@link CronTaskRepository},
 * enabling fine-grained task scheduling management.
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
     * Construct a {@link CronTaskRegistrar} with given arguments.
     * @param cronTask       the pending registration {@link CronTask}.
     */
    public CronTaskRegistrar(CronTask cronTask) {

        Assert.notNull(cronTask, "CronTask not be null");

        this.cronTask = cronTask;
        this.targetMethod = cronTask.getRunnable().getMethod();
    }

    /**
     * Register {@link #cronTask} into the given {@link CronTaskRepository}, while
     * simultaneously configuring annotations {@link RunTimes} and {@link RunTimeout}
     * and {@link DisallowConcurrentExecution}.
     * @param cronTaskRepository the {@link CronTaskRepository}.
     * @return The unique ID for scheduling task registration, when running times
     * related to API registration, returns {@literal null}.
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
