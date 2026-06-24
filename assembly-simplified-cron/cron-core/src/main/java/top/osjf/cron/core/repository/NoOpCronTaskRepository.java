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

import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A basic, non operational {@link CronTaskRepository} implementation is used to disable
 * scheduled scheduling, typically used to support scheduled scheduling declarations
 * without an actual backup scheduler.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class NoOpCronTaskRepository extends AbstractCronTaskRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getName() {
        return "NOOP_SCHEDULER@" + super.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkSupportedExpression(String expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull String expression, @NotNull Runnable runnable)  {
        return getWithoutOperationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull String expression, @NotNull CronMethodRunnable runnable)  {
        return getWithoutOperationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull String expression, @NotNull RunnableTaskBody body)  {
        return getWithoutOperationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull String expression, @NotNull TaskBody body)  {
        return getWithoutOperationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull CronTask task) {
        return getWithoutOperationId();
    }

    @Override
    public boolean hasCronTaskInfoInternal(@NotNull String id) {
        return false;
    }

    static String getWithoutOperationId() { return UUID.randomUUID().toString(); }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public CronTaskInfo getCronTaskInfoInternal(@NotNull String id) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CronTaskInfo> getAllCronTaskInfo() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateInternal(@NotNull String id, @NotNull String newExpression)  {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeInternal(@NotNull String id)  {
    }

    @Override
    public String toString() {
        return " Non operational cronTaskRepository implementation class. ";
    }
}
