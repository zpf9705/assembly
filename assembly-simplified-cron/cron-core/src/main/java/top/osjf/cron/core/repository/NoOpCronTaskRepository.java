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

import top.osjf.cron.core.exception.CronInternalException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public String register(@Nonnull String expression, @Nonnull Runnable runnable) throws CronInternalException {
        return UUID.randomUUID().toString();
    }

    @Override
    public String register(@Nonnull String expression, @Nonnull CronMethodRunnable runnable) throws CronInternalException {
        return UUID.randomUUID().toString();
    }

    @Override
    public String register(@Nonnull String expression, @Nonnull RunnableTaskBody body) throws CronInternalException {
        return UUID.randomUUID().toString();
    }

    @Override
    public String register(@Nonnull String expression, @Nonnull TaskBody body) throws CronInternalException {
        return UUID.randomUUID().toString();
    }

    @Override
    public String register(@Nonnull CronTask task) throws CronInternalException {
        return UUID.randomUUID().toString();
    }

    @Nullable
    @Override
    public CronTaskInfo getCronTaskInfo(@Nonnull String id) {
        return null;
    }

    @Override
    public List<CronTaskInfo> getAllCronTaskInfo() {
        return Collections.emptyList();
    }

    @Override
    public void update(@Nonnull String id, @Nonnull String newExpression) throws CronInternalException {
    }

    @Override
    public void remove(@Nonnull String id) throws CronInternalException {
    }

    @Override
    public String toString() {
        return " Non operational cronTaskRepository implementation class. ";
    }
}
