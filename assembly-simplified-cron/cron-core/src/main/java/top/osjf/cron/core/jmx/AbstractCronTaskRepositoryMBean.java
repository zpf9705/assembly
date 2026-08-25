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


package top.osjf.cron.core.jmx;

import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.*;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract cron task repository with JMX metrics support.
 *
 * <p>Auto register/unregister JMX MBean on {@link #initialize()} and {@link #stop()}.
 * Maintain counter metrics via {@link AtomicLong}, gauge metrics query real‑time repository state.
 * MBean ObjectName: {@code top.osjf.cron:type=CronTaskRepositoryMetrics,name={repositoryName}}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class AbstractCronTaskRepositoryMBean
        extends AbstractCronTaskRepository implements CronTaskRepositoryMBean {

    /** Registered MBean object name, {@code null} if not registered. */
    @Nullable private ObjectName mbeanObjectName;

    /** Total count of task registration operations. */
    protected final AtomicLong registerTaskTotal = new AtomicLong();
    /** Total count of tasks registered with running‑timeout config. */
    protected final AtomicLong registerTimeoutTotal = new AtomicLong();
    /** Total count of limited‑run‑times task registrations. */
    protected final AtomicLong registerRuntimesTotal = new AtomicLong();
    /** Total count of limited‑run‑times tasks with running‑timeout config. */
    protected final AtomicLong registerTimeoutRuntimesTotal = new AtomicLong();
    /** Total count of task update operations. */
    protected final AtomicLong updateTaskTotal = new AtomicLong();
    /** Total count of task remove operations. */
    protected final AtomicLong removeTaskTotal = new AtomicLong();
    /** Total count of task terminate operations. */
    protected final AtomicLong terminateTaskTotal = new AtomicLong();
    /** Total count of add listener operations. */
    protected final AtomicLong addListenerTotal = new AtomicLong();
    /** Total count of remove listener operations. */
    protected final AtomicLong removeListenerTotal = new AtomicLong();

    /**
     * Initialize repository and register JMX MBean.
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws Exception {
        super.initialize();
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        String name = "top.osjf.cron:type=CronTaskRepositoryMetrics,name=" + ObjectName.quote(getName());
        mbeanObjectName = new ObjectName(name);
        StandardMBean standardMBean = new StandardMBean(this,  CronTaskRepositoryMBean.class);
        if (!mBeanServer.isRegistered(mbeanObjectName)) {
            mBeanServer.registerMBean(standardMBean, mbeanObjectName);
        }
    }

    /**
     * Stop repository and unregister JMX MBean.
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        super.stop();
        if (mbeanObjectName == null) {
            return;
        }
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            if (mBeanServer.isRegistered(mbeanObjectName)) {
                mBeanServer.unregisterMBean(mbeanObjectName);
            }
        }
        catch (Exception ex) {
             logger.error("Unregister CronTaskRepository MBean failed, module={}", getName(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, Runnable runnable) throws CronInternalException {
        String id = super.register(expression, runnable);
        registerTaskTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, CronMethodRunnable runnable) throws CronInternalException {
        String id = super.register(expression, runnable);
        registerTaskTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, RunnableTaskBody body) throws CronInternalException {
        String id = super.register(expression, body);
        registerTaskTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, TaskBody body) throws CronInternalException {
        String id = super.register(expression, body);
        registerTaskTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(CronTask task) throws CronInternalException {
        String id = super.register(task);
        registerTaskTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(String id, String newExpression) throws CronInternalException {
        super.update(id, newExpression);
        updateTaskTotal.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String id) throws CronInternalException {
        super.remove(id);
        removeTaskTotal.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAll() throws CronInternalException {
        super.removeAll();
        removeTaskTotal.addAndGet(getRegisteredTaskCurrent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate(String id) throws CronInternalException {
        super.terminate(id);
        terminateTaskTotal.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminateAll() throws CronInternalException {
        super.terminateAll();
        removeTaskTotal.addAndGet(getRunningTaskCurrent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, Runnable runnable, RunningTimeout timeout)
            throws CronInternalException {
        String id = super.register(expression, runnable, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, CronMethodRunnable runnable, RunningTimeout timeout)
            throws CronInternalException {
        String id = super.register(expression, runnable, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, RunnableTaskBody body, RunningTimeout timeout)
            throws CronInternalException {
        String id = super.register(expression, body, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, TaskBody body, RunningTimeout timeout) throws CronInternalException {
        String id = super.register(expression, body, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(CronTask task, RunningTimeout timeout) throws CronInternalException {
        String id = super.register(task, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(String expression, Runnable runnable, int times,
                                   RunningTimeout timeout) throws CronInternalException {
        String id = super.registerRunTimes(expression, runnable, times, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        registerTimeoutRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(String expression, CronMethodRunnable runnable,
                                   int times, RunningTimeout timeout) throws CronInternalException {
        String id = super.registerRunTimes(expression, runnable, times, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        registerTimeoutRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(String expression, RunnableTaskBody body, int times, RunningTimeout timeout)
            throws CronInternalException {
        String id = super.registerRunTimes(expression, body, times, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        registerTimeoutRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(String expression, TaskBody body, int times, RunningTimeout timeout)
            throws CronInternalException {
        String id = super.registerRunTimes(expression, body, times, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        registerTimeoutRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(CronTask task, int times, RunningTimeout timeout) throws CronInternalException {
        String id = super.registerRunTimes(task, times, timeout);
        registerTaskTotal.incrementAndGet();
        registerTimeoutTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        registerTimeoutRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(String expression, Runnable runnable, int times)
            throws CronInternalException {
        String id = super.registerRunTimes(expression, runnable, times);
        registerTaskTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(String expression, CronMethodRunnable runnable, int times)
            throws CronInternalException {
        String id = super.registerRunTimes(expression, runnable, times);
        registerTaskTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(String expression, RunnableTaskBody body, int times)
            throws CronInternalException {
        String id = super.registerRunTimes(expression, body, times);
        registerTaskTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(String expression, TaskBody body, int times) throws CronInternalException {
        String id = super.registerRunTimes(expression, body, times);
        registerTaskTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerRunTimes(CronTask task, int times) throws CronInternalException {
        String id = super.registerRunTimes(task, times);
        registerTaskTotal.incrementAndGet();
        registerRuntimesTotal.incrementAndGet();
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(CronListener listener) {
        super.addLastListener(listener);
        addListenerTotal.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFirstListener(CronListener listener) {
        super.addFirstListener(listener);
        addListenerTotal.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLastListener(CronListener listener) {
        super.addLastListener(listener);
        addListenerTotal.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeListener(CronListener listener) {
        boolean result = super.removeListener(listener);
        removeListenerTotal.incrementAndGet();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeListener(String listenerName) {
        boolean result = super.removeListener(listenerName);
        removeListenerTotal.incrementAndGet();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRegisterTaskTotal() {
        return registerTaskTotal.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRegisterTimeoutTotal() {
        return registerTimeoutTotal.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTaskTimeoutTotal() {
        return TimeoutMonitoringRunnable.getTaskTimeoutTotal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRegisterRuntimesTotal() {
        return registerRuntimesTotal.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRegisterTimeoutRuntimesTotal() {
        return registerTimeoutRuntimesTotal.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getUpdateTaskTotal() {
        return updateTaskTotal.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRemoveTaskTotal() {
        return removeTaskTotal.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTerminateTaskTotal() {
        return terminateTaskTotal.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getAddListenerTotal() {
        return addListenerTotal.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRemoveListenerTotal() {
        return removeListenerTotal.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRegisteredTaskCurrent() {
        return getAllRegisteredTaskIds().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRegisteredRunTimesTaskCurrent() {
        return getRemainingLimitedRunTimesTaskCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRunningTaskCurrent() {
        return getAllRunningTaskIds().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRegisteredTaskListenerCurrent() {
        return getListenerSize();
    }
}
