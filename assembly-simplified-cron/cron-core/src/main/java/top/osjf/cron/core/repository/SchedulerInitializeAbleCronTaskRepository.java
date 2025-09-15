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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * An abstract repository class that provides initialization capabilities for cron task schedulers.
 * This class extends {@link AbstractCronTaskRepository} and adds functionality to manage a
 * named scheduler instance ({@link #SCHEDULER_NAME}) with thread-safe initialization guarantees.
 *
 * <p>This class implements the double-checked locking pattern to ensure thread-safe lazy
 * initialization of the scheduler field while maintaining performance. Subclasses should
 * implement the actual initialization logic through the required methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class SchedulerInitializeAbleCronTaskRepository<T> extends AbstractCronTaskRepository {

    public static final String SCHEDULER_NAME = "scheduler";

    private volatile T scheduler;

    private final Object monitor = new Object();

    /**
     * Return the scheduler named {@link #SCHEDULER_NAME} after an initialization operation,
     * indicating that it must undergo an initialization operation before it can be used.
     * @return A {@code scheduler} object named {@link #SCHEDULER_NAME} after initializing.
     * @throws IllegalStateException If no scheduler instance named {@link #SCHEDULER_NAME} is found
     *                               or access failed.
     */
    @SuppressWarnings("unchecked")
    protected T getInitializedScheduler() {

        ensureInitialized();

        if (scheduler == null) {
            synchronized (monitor) {
                if (scheduler == null) {
                    for (Field df : this.getClass().getDeclaredFields()) {
                        if (SCHEDULER_NAME.equals(df.getName())) {
                            try {
                                if ((!Modifier.isPublic(df.getModifiers()) ||
                                        !Modifier.isPublic(df.getDeclaringClass().getModifiers()))
                                        && !df.isAccessible()) {
                                    df.setAccessible(true);
                                }
                                scheduler = (T) df.get(this);
                            }
                            catch (Exception ex) {
                                throw new IllegalStateException(ex);
                            }
                        }
                    }
                }
            }
        }

        return scheduler;
    }
}
