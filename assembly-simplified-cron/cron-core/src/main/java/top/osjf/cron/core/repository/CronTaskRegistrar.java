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
 * @since 3.0.2
 */
public class CronTaskRegistrar {

    /** the pending registration {@link CronTask} .*/
    private final CronTask cronTask;

    /** the target method .*/
    private final Method targetMethod;

    /**
     * Construct a {@link CronTaskRegistrar} with given arguments.
     * @param cronTask       the pending registration {@link CronTask}.
     * @param targetMethod   the target method .
     */
    public CronTaskRegistrar(CronTask cronTask, Method targetMethod) {
        this.cronTask = cronTask;
        this.targetMethod = targetMethod;
    }

    /**
     * Register {@link #cronTask} into the given {@link CronTaskRepository}, while
     * simultaneously configuring annotations {@link RunTimes} and {@link RunTimeout}.
     * @param cronTaskRepository the {@link CronTaskRepository}.
     */
    public void doRegister(CronTaskRepository cronTaskRepository) {

        boolean needSpecifyRuntimes = targetMethod.isAnnotationPresent(RunTimes.class);
        boolean needSpecifyRunTimeout = targetMethod.isAnnotationPresent(RunTimeout.class);

        if (needSpecifyRuntimes) {
            RunTimes runTimes = targetMethod.getAnnotation(RunTimes.class);
            if (needSpecifyRunTimeout) {
                RunTimeout runTimeout = targetMethod.getAnnotation(RunTimeout.class);
                cronTaskRepository.registerRunTimes(cronTask, runTimes.value(),
                        new RunningTimeout(runTimeout.timeout(), runTimeout.timeUnit(),
                                runTimeout.policy()));
            }
            else {
                cronTaskRepository.registerRunTimes(cronTask, runTimes.value());
            }
        }
        else {
            cronTaskRepository.register(cronTask);
        }
    }
}
