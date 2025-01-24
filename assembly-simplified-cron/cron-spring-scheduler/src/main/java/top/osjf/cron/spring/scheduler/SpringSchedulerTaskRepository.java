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

package top.osjf.cron.spring.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.IdGenerator;
import org.springframework.util.SimpleIdGenerator;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.repository.*;

import java.util.List;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class SpringSchedulerTaskRepository extends ListenableTaskScheduler implements CronTaskRepository {

    private final CronListenerCollector cronListenerCollector = new CronListenerCollectorImpl();

    private final IdGenerator idGenerator = new SimpleIdGenerator();

    public SpringSchedulerTaskRepository(@NotNull TaskScheduler taskScheduler) {
        super(taskScheduler);
    }

    /**
     * Set all {@link CronListener} beans in the container to the current bean.
     *
     * <p><strong>Note:</strong></p>
     * If a bean depends on this bean {@code SchedulingRepository} and implements
     * {@code CronListener}, it is also a {@code CronListener} bean, which
     * will result in circular dependencies and program errors. It is recommended to
     * separate the logic extraction and processing.
     *
     * @param cronListeners all {@link CronListener} beans in the container.
     */
    @Autowired(required = false)
    public void setSchedulingListeners(List<CronListener> cronListeners) {
        for (CronListener cronListener : cronListeners) {
            cronListenerCollector.addCronListener(cronListener);
        }
    }

    @Override
    protected ListenableRunnable wrapperRunnableToListenable(Runnable runnable, Trigger trigger) {
        String id = idGenerator.generateId().toString();
        return new DefaultListenableRunnable(id, runnable, trigger, cronListenerCollector.getCronListeners());
    }

    @Override
    public String register(@NotNull String expression, @NotNull Runnable runnable) throws CronInternalException {
        return RepositoryUtils.doRegister(() ->
                        schedule(runnable, new CronTrigger(expression)).getListenableRunnable().getId(),
                IllegalArgumentException.class);
    }

    @Override
    public String register(@NotNull String expression, @NotNull CronMethodRunnable runnable) throws CronInternalException {
        return register(expression, (Runnable) runnable);
    }

    @Override
    public String register(@NotNull String expression, @NotNull RunnableTaskBody body) throws CronInternalException {
        return register(expression, body.getRunnable());
    }

    @Override
    @NotNull
    public String register(@NotNull String expression, @NotNull TaskBody body) {
        if (body.isWrapperFor(RunnableTaskBody.class)) {
            return register(expression, body.unwrap(RunnableTaskBody.class));
        }
        throw new UnsupportedTaskBodyException(body.getClass());
    }

    @Override
    @NotNull
    public String register(@NotNull top.osjf.cron.core.repository.CronTask task) {
        return register(task.getExpression(), task.getRunnable());
    }

    @Nullable
    @Override
    public String getExpression(String id) {
        ListenableScheduledFuture listenableScheduledFuture = getListenableScheduledFutures().get(id);
        if (listenableScheduledFuture != null) {
            Trigger trigger = listenableScheduledFuture.getListenableRunnable().getTrigger();
            if (trigger instanceof CronTrigger) {
                return ((CronTrigger) trigger).getExpression();
            }
        }
        return null;
    }

    @Override
    public void update(@NotNull String id, @NotNull String newExpression) {
        ListenableScheduledFuture listenableScheduledFuture = getListenableScheduledFutures().remove(id);
        if (listenableScheduledFuture == null) {
            throw new CronInternalException("ID " + id + " did not find the corresponding task information.");
        }
        listenableScheduledFuture.cancel(true);
        register(newExpression, listenableScheduledFuture.getListenableRunnable().getRunnable());
    }

    @Override
    public void remove(@NotNull String id) {
        ListenableScheduledFuture listenableScheduledFuture = getListenableScheduledFutures().remove(id);
        if (listenableScheduledFuture != null) {
            listenableScheduledFuture.cancel(true);
        }
    }

    @Override
    public void addListener(@NotNull CronListener listener) {
        cronListenerCollector.addCronListener(listener);
    }

    @Override
    public void removeListener(@NotNull CronListener listener) {
        cronListenerCollector.removeCronListener(listener);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
