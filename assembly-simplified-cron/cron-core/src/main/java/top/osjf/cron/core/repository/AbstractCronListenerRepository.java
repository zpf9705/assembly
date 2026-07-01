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

import io.micrometer.core.annotation.Counted;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.listener.DefaultCronListenerCollector;
import top.osjf.cron.core.micrometer.SystemPropertiesTags;

import java.util.List;

import static top.osjf.cron.core.micrometer.RepositoryMicrometerConstants.*;

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
    private final CronListenerCollector listenerCollector = new DefaultCronListenerCollector(this);

    /**
     * {@inheritDoc}
     */
    @Counted(value = ADD_LISTENER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "addListener(CronListener listener)"},
            description = "Counts invocation times of adding a cron listener")
    @SystemPropertiesTags
    @Override
    public void addListener(CronListener listener) {
        getCronListenerCollector().addCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Counted(value = ADD_LISTENER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "addFirstListener(CronListener listener)"},
            description = "Counts invocation times of adding a cron listener")
    @SystemPropertiesTags
    @Override
    public void addFirstListener(CronListener listener) {
        getCronListenerCollector().addFirstCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Counted(value = ADD_LISTENER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "addLastListener(CronListener listener)"},
            description = "Counts invocation times of adding a cron listener")
    @SystemPropertiesTags
    @Override
    public void addLastListener(CronListener listener) {
        getCronListenerCollector().addLastCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasListener(CronListener cronListener) {
        return getCronListenerCollector().hasCronListener(cronListener);
    }

    /**
     * {@inheritDoc}
     */
    @Counted(value = REMOVE_LISTENER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "removeListener(CronListener listener)"},
            description = "Counts invocation times of removing a cron listener")
    @SystemPropertiesTags
    @Override
    public boolean removeListener(CronListener listener) {
        return getCronListenerCollector().removeCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Counted(value = REMOVE_LISTENER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "removeListener(String listenerName)"},
            description = "Counts invocation times of removing a cron listener")
    @SystemPropertiesTags
    @Override
    public boolean removeListener(String listenerName) {
        return getCronListenerCollector().removeCronListener(listenerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronListener getListener(String listenerName) {
        return getCronListenerCollector().getListener(listenerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getListenerSize() {
        return getCronListenerCollector().getListenerSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CronListener> getAllListeners() {
        return getCronListenerCollector().getCronListeners();
    }

    /**
     * @return A {@link CronListenerCollector} manager, default to {@link #listenerCollector},
     * supports subclass customization.
     */
    protected CronListenerCollector getCronListenerCollector() {
        return listenerCollector;
    }
}
