/*
 * Copyright 2026-? the original author or authors.
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

import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.Assert;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fluent builder implementation for {@link CronTaskRepository}, used to assemble task registration
 * parameters in a chained way and automatically match the overloaded registration method to complete
 * scheduled task registration.
 * <p>
 * Supports five types of task bodies: {@link CronTask}, {@link CronMethodRunnable}, {@link RunnableTaskBody},
 * {@link TaskBody}, native {@link Runnable}, and provides two governance capabilities: limited execution
 * times and task timeout control. Each builder instance can only call {@link #build()} once to avoid duplicate
 * task registration.
 * <p>
 * <h3>Code Usage Example</h3>
 * <pre>{@code
 * // 1. Register common Runnable scheduled task
 * String taskId1 = CronTaskBuilder.forRepository(cronTaskRepository)
 *         .withExpression("0/5 * * * * ?")
 *         .withTask(() -> System.out.println("Execute common runnable task"))
 *         .build();
 *
 * // 2. Register method-type scheduled task with execution limit and timeout governance
 * CronMethodRunnable methodRunnable
 *               = new CronMethodRunnable(targetBean, TargetClass.class.getDeclaredMethod("taskMethod"));
 * String taskId2 = CronTaskBuilder.forRepository(cronTaskRepository)
 *         .withExpression("0 0 8 * * ?")
 *         .withTask(methodRunnable)
 *         .limitRunTimes(100)
 *         .timeout(new RunningTimeout(30, TimeUnit.SECONDS))
 *         .build();
 *
 * // 3. Register task via integrated CronTask object
 * CronTask cronTask = new CronTask("task-001", "0 30 9 * * ?", () -> System.out.println("Integrated cron task"));
 * String taskId3 = CronTaskBuilder.forRepository(cronTaskRepository)
 *         .withExpression(cronTask.getExpression())
 *         .withTask(cronTask)
 *         .build();
 * }</pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class CronTaskBuilder implements CronTaskRepository.Builder {

    private final CronTaskRepository repository;

    // Required parameters
    private String expression;
    private Object task;

    // Optional governance parameters
    @Nullable private Integer maxRunTimes;

    @Nullable private RunningTimeout runningTimeout;

    /** Mark to ensure build() only execute once for current builder instance. */
    private final AtomicBoolean built = new AtomicBoolean(false);

    /**
     * Private constructor, use {@link #forRepository(CronTaskRepository)} to create builder instance.
     *
     * @param repository target cron task repository for task registration, must not be {@code null}
     */
    private CronTaskBuilder(CronTaskRepository repository) {
        Assert.notNull(repository, "CronTaskRepository must not be null");
        this.repository = repository;
    }

    /**
     * Create a new {@code CronTaskBuilder} bound to specified {@link CronTaskRepository}.
     *
     * @param repository task repository instance
     * @return fluent builder instance
     */
    public static CronTaskBuilder forRepository(CronTaskRepository repository) {
        return new CronTaskBuilder(repository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskBuilder withExpression(String expression) {
        this.expression = expression;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskBuilder withTask(Runnable runnable) {
        this.task = runnable;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskBuilder withTask(CronMethodRunnable methodRunnable) {
        this.task = methodRunnable;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskBuilder withTask(RunnableTaskBody runnableTaskBody) {
        this.task = runnableTaskBody;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskBuilder withTask(TaskBody taskBody) {
        this.task = taskBody;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskBuilder withTask(CronTask cronTask) {
        this.task = cronTask;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException max times non be greater than 0
     */
    @Override
    public CronTaskBuilder limitRunTimes(int maxTimes) {
        Assert.isTrue(maxTimes > 0, "Max run times must be greater than 0");
        this.maxRunTimes = maxTimes;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskBuilder timeout(RunningTimeout timeout) {
        this.runningTimeout = timeout;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException Current CronTaskBuilder instance can only call build() once.
     */
    public String build() {

        Assert.state(built.compareAndSet(false, true),
                "Current CronTaskBuilder instance can only call build() once");

        Assert.hasText(expression, "Cron expression must be specified");

        Assert.notNull(task, "Task execution body must be specified");

        return buildInternal();
    }

    /**
     * Distribute different registration logic according to the actual type of task execution body,
     * and call the corresponding task registration method after matching governance rules.
     *
     * @return unique identifier of registered cron task
     */
    private String buildInternal() {
        if (task instanceof CronTask) {
            CronTask cronTask = (CronTask) task;
            return doRegisterCronTask(cronTask);
        }
        else if (task instanceof CronMethodRunnable) {
            CronMethodRunnable methodRunnable = (CronMethodRunnable) task;
            return matchRegisterMethod(methodRunnable);
        }
        else if (task instanceof RunnableTaskBody) {
            RunnableTaskBody taskBody = (RunnableTaskBody) task;
            return matchRegisterMethod(taskBody);
        }
        else if (task instanceof TaskBody) {
            TaskBody taskBody = (TaskBody) task;
            return matchRegisterMethod(taskBody);
        }
        else if (task instanceof Runnable) {
            Runnable runnable = (Runnable) task;
            return matchRegisterMethod(runnable);
        }
        throw new IllegalArgumentException
                ("Coming here should be an extension of the task type without code compatibility!");
    }

    /**
     * Execute registration for integrated {@link CronTask} object,
     * automatically match the registration method according to timeout and limited execution rules.
     *
     * @param cronTask integrated full metadata of scheduled task
     * @return unique identifier of registered cron task
     */
    private String doRegisterCronTask(CronTask cronTask) {
        return doMatchRegisterMethod(
                (maxRunTimes, runningTimeout) -> repository.registerRunTimes(cronTask, maxRunTimes, runningTimeout),
                (maxRunTimes) ->  repository.registerRunTimes(cronTask, maxRunTimes),
                (runningTimeout) -> repository.register(cronTask, runningTimeout),
                () -> repository.register(cronTask));
    }

    /**
     * Register native {@link Runnable} type task with specified cron expression and governance rules.
     *
     * @param runnable native task execution logic
     * @return unique identifier of registered cron task
     */
    private String matchRegisterMethod(Runnable runnable) {
        return doMatchRegisterMethod(
                (maxRunTimes, runningTimeout) -> repository.registerRunTimes(expression, runnable, maxRunTimes,
                        runningTimeout),
                (maxRunTimes) ->  repository.registerRunTimes(expression, runnable, maxRunTimes),
                (runningTimeout) -> repository.register(expression, runnable, runningTimeout),
                () -> repository.register(expression, runnable));
    }

    /**
     * Register {@link CronMethodRunnable} method-type task with specified cron expression and governance rules.
     * This type can resolve the target execution bean and method metadata of the task.
     *
     * @param methodRunnable encapsulated method task instance
     * @return unique identifier of registered cron task
     */
    private String matchRegisterMethod(CronMethodRunnable methodRunnable) {
        return doMatchRegisterMethod(
                (maxRunTimes, runningTimeout) -> repository.registerRunTimes(expression, methodRunnable, maxRunTimes,
                        runningTimeout),
                (maxRunTimes) ->  repository.registerRunTimes(expression, methodRunnable, maxRunTimes),
                (runningTimeout) -> repository.register(expression, methodRunnable, runningTimeout),
                () -> repository.register(expression, methodRunnable));
    }

    /**
     * Register {@link RunnableTaskBody} wrapped runnable task with specified cron expression and governance rules.
     *
     * @param taskBody wrapped runnable task instance
     * @return unique identifier of registered cron task
     */
    private String matchRegisterMethod(RunnableTaskBody taskBody) {
        return doMatchRegisterMethod(
                (maxRunTimes, runningTimeout) -> repository.registerRunTimes(expression, taskBody, maxRunTimes,
                        runningTimeout),
                (maxRunTimes) ->  repository.registerRunTimes(expression, taskBody, maxRunTimes),
                (runningTimeout) -> repository.register(expression, taskBody, runningTimeout),
                () -> repository.register(expression, taskBody));
    }

    /**
     * Register general {@link TaskBody} task wrapper with specified cron expression and governance rules.
     *
     * @param taskBody general task wrapper instance
     * @return unique identifier of registered cron task
     */
    private String matchRegisterMethod(TaskBody taskBody) {
        return doMatchRegisterMethod(
                (maxRunTimes, runningTimeout) -> repository.registerRunTimes(expression, taskBody, maxRunTimes,
                        runningTimeout),
                (maxRunTimes) ->  repository.registerRunTimes(expression, taskBody, maxRunTimes),
                (runningTimeout) -> repository.register(expression, taskBody, runningTimeout),
                () -> repository.register(expression, taskBody));
    }

    /**
     * General governance rule matching template method, select the corresponding registration method
     * according to whether the two governance parameters of limited execution times and timeout are configured.
     *
     * @param bothFunc       execute when both maxRunTimes and runningTimeout are configured
     * @param onlyTimesFunc  execute when only maxRunTimes is configured
     * @param onlyTimeoutFunc execute when only runningTimeout is configured
     * @param defaultFunc    execute when no governance rules are configured
     * @return unique identifier of registered cron task
     */
    private String doMatchRegisterMethod(BiFunction<Integer, RunningTimeout, String> bothFunc,
                                         Function<Integer, String> onlyTimesFunc,
                                         Function<RunningTimeout, String> onlyTimeoutFunc,
                                         Supplier<String> defaultFunc) {
        if (maxRunTimes != null && runningTimeout != null) {
            return bothFunc.apply(maxRunTimes, runningTimeout);
        }
        else if (maxRunTimes != null) {
            return onlyTimesFunc.apply(maxRunTimes);
        }
        else if (runningTimeout != null) {
            return onlyTimeoutFunc.apply(runningTimeout);
        }
        else {
            return defaultFunc.get();
        }
    }
}