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

import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.listener.DefaultCronListenerCollector;

/**
 * The abstract implementation class of {@link CronTaskRepository} utilizes
 * {@link CronListenerCollector} to centrally manage {@link CronListener}
 * and ensure thread safety, while opening subclasses for customizing
 * {@link CronListenerCollector} to further ensure business functionality.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class AbstractCronListenerRepository
        extends AbstractLifecycleRepository implements CronListenerRepository {

    /** Scheduling listener manager.*/
    private final CronListenerCollector listenerCollector = new DefaultCronListenerCollector();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(@NotNull CronListener listener) {
        getCronListenerCollector().addCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFirstListener(@NotNull CronListener listener) {
        getCronListenerCollector().addFirstCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLastListener(@NotNull CronListener listener) {
        getCronListenerCollector().addLastCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasListener(@NotNull CronListener cronListener) {
        return getCronListenerCollector().hasCronListener(cronListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(@NotNull CronListener listener) {
        getCronListenerCollector().removeCronListener(listener);
    }

    /**
     * @return A {@link CronListenerCollector} manager, default to {@link #listenerCollector},
     * supports subclass customization.
     */
    protected CronListenerCollector getCronListenerCollector() {
        return listenerCollector;
    }
}
