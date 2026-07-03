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

import top.osjf.commons.util.Assert;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Annotation-based cron task registrar, used to parse annotations on target methods,
 * assemble task governance configuration and complete scheduled task registration.
 * <p>
 * Two construction modes are supported:
 * <ol>
 * <li>Bean + Method mode: fully rely on {@link Expression} and other method annotations to build
 * cron task.</li>
 * <li>{@link CronTask} mode: use custom task instance as high-priority configuration,
 * only supplement governance attributes from method annotations.</li>
 * </ol>
 * Each registrar instance can only perform one registration operation to avoid duplicate tasks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class AnnotationMethodRegistrar {

    /** The target method parsed from cron task runnable, used to resolve governance annotations. */
    private final Method targetMethod;

    /** User-defined cron task with high priority configuration. */
    private final CronTask cronTask;

    /** Atomic flag to guarantee each registrar only executes registration once. */
    private final AtomicBoolean registered = new AtomicBoolean(false);

    /**
     * Create a {@code AnnotationMethodRegistrar} using the given target object and target method.
     * <p>
     * <strong>NOTE:</strong>
     * This construction method requires that the passed method must be annotated with a cron expression
     * to obtain annotations.
     * @param target target bean instance
     * @param method scheduled task execution method marked with annotations
     * @throws IllegalArgumentException If the method is annotated, provide an annotation {@link Expression}
     * for the expression.
     */
    public AnnotationMethodRegistrar(Object target, Method method) {
        this.targetMethod = method;
        CronMethodRunnable runnable = new CronMethodRunnable(target, method);
        Expression expression = method.getAnnotation(Expression.class);
        Assert.notNull(expression, "Missing annotation on method " + method.getName());
        this.cronTask = new CronTask(expression.value(), runnable);
    }

    /**
     * Create a {@code AnnotationMethodRegistrar} using the given user-defined  {@code CronTask}.
     *
     * @param cronTask predefined core cron task definition
     */
    public AnnotationMethodRegistrar(CronTask cronTask) {
        this.cronTask = cronTask;
        this.targetMethod = cronTask.getRunnable().getMethod();
    }

    /**
     * Parse governance annotations on target method, assemble task configuration via builder,
     * register cron task to repository and return the unique task ID.
     * This method can only be called once per registrar instance.
     *
     * @param cronTaskRepository task registration repository
     * @return globally unique registered cron task id
     * @throws IllegalStateException if this registrar has already finished registration
     */
    public String registerFor(CronTaskRepository cronTaskRepository) {

        Assert.state(registered.compareAndSet(false, true),
                "AnnotationMethodRegistrar method registerFor() only invoke once.");

        CronTaskRepository.Builder builder = cronTaskRepository.newBuilder();

        builder.withTask(cronTask);

        Name name;
        builder.withName((name = targetMethod.getAnnotation(Name.class)) != null ? name.value() : null);

        Description description;
        builder.withDescription((description = targetMethod.getAnnotation(Description.class)) != null
                ? description.value() : null);

        if (targetMethod.isAnnotationPresent(DisallowConcurrentExecution.class))
            builder.disallowConcurrentExecution();

        RunTimes runTimes;
        builder.limitRunTimes((runTimes = targetMethod.getAnnotation(RunTimes.class)) != null
                ? runTimes.value() : 1);

        RunTimeout runTimeout;
        builder.timeout((runTimeout = targetMethod.getAnnotation(RunTimeout.class)) != null
                ? new RunningTimeout(runTimeout.timeout(), runTimeout.timeUnit(), runTimeout.policy()) : null);

        return builder.build();
    }
}
