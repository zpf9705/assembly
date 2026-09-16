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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.lifecycle.InitializeProperties;
import top.osjf.cron.core.lifecycle.PropertiesInitializeAble;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.*;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link CronTaskRepository} decorator that exposes cron‑task metrics via JMX MBean.
 *
 * <p>This class extends {@link DelegatingCronTaskRepository}, it wraps an underlying
 * {@link CronTaskRepository} and collects operation counters for task registration,
 * update, removal, termination and listener‑related events. All raw business logic
 * is delegated to the target repository.
 *
 * <p>On {@link #initialize()}, this instance will self‑register as an MBean into the
 * platform {@link MBeanServer}, making all collected metrics observable through JMX tools.
 * MBean will be unregistered on {@link #stop()}.
 *
 * <p>Metrics include cumulative counters (total registered/updated/removed tasks etc.)
 * and instant snapshot values (current running tasks, current registered tasks, etc.),
 * which are exposed by implementing {@link CronTaskRepositoryMBean}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class JMXMetricsCronTaskRepository extends DelegatingCronTaskRepository implements CronTaskRepositoryMBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMXMetricsCronTaskRepository.class);

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

    /** Registered MBean object name, {@code null} if not registered. */
    @Nullable private ObjectName mbeanObjectName;

    public JMXMetricsCronTaskRepository(CronTaskRepository delegate) {
        super(delegate);
    }

    /**
     * Initialize repository and register JMX MBean.
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws Exception {
        super.initialize();
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        String name = "top.osjf.cron:type=JMXMetricsCronTaskRepository";
        mbeanObjectName = new ObjectName(name);
        StandardMBean standardMBean = new CronTaskRepositoryStandardMBean(this);
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
            LOGGER.error("Unregister JMXMetricsCronTaskRepository MBean failed, module={}", getName(), ex);
        }
        mbeanObjectName = null;
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
        long registeredTaskCurrent = getRegisteredTaskCurrent();
        super.removeAll();
        removeTaskTotal.addAndGet(registeredTaskCurrent);
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
        long runningTaskCurrent = getRunningTaskCurrent();
        super.terminateAll();
        removeTaskTotal.addAndGet(runningTaskCurrent);
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
    public String getType() {
        return getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return delegate.getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceType() {
        return delegate.getSourceType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceVersion() {
        return delegate.getSourceVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIDGeneratorType() {
        IDGenerator idGenerator = getIDGenerator();
        return idGenerator != null ? idGenerator.getClass().getName() : "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProperties() {
        InitializeProperties properties = null;
        if (delegate instanceof PropertiesInitializeAble) {
            properties = ((PropertiesInitializeAble) delegate).getInitializeProperties();
        }
        return properties != null ? properties.toString() : "{}";
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
