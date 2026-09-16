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
import top.osjf.cron.core.exception.CannotCancelConcurrentException;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.NotSupportConcurrentExecutionException;
import top.osjf.cron.core.listener.CronListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Abstract delegating decorator implementation of {@link CronTaskRepository}.
 *
 * <p>This abstract class follows the decorator (delegation) pattern.
 * It holds a target {@link CronTaskRepository} delegate instance and forwards
 * all interface method invocations to {@link #delegate} by default.
 * Subclasses may extend this class and selectively override target methods
 * to add custom enhancements before/after the original logic, such as logging,
 * monitoring, parameter validation, exception wrapping, metrics, etc.
 * There is no need to implement all interface methods manually.
 *
 * <p>Usage: Subclass constructors supply the raw {@link CronTaskRepository} instance
 * to this parent class. Only methods requiring enhancement need overriding;
 * non-overridden methods will delegate directly to the underlying repository.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class DelegatingCronTaskRepository implements CronTaskRepository {

    protected final CronTaskRepository delegate;

    /**
     * Create a delegating wrapper holding the raw cron task repository.
     * @param delegate the underlying {@link CronTaskRepository} implementation.
     */
    public DelegatingCronTaskRepository(CronTaskRepository delegate) {
        Assert.notNull(delegate, "Delegate CronTaskRepository must not be null");
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void initialize() throws Exception {
        delegate.initialize();
    }

    @Override
    public void start() {
        delegate.start();
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public boolean isStarted() {
        return delegate.isStarted();
    }

    @Override
    public void addListener(CronListener listener) {
        delegate.addListener(listener);
    }

    @Override
    public void addFirstListener(CronListener listener) {
        delegate.addFirstListener(listener);
    }

    @Override
    public void addLastListener(CronListener listener) {
        delegate.addLastListener(listener);
    }

    @Override
    public boolean hasListener(CronListener listener) {
        return delegate.hasListener(listener);
    }

    @Override
    public boolean removeListener(CronListener listener) {
        return delegate.removeListener(listener);
    }

    @Override
    public boolean removeListener(String listenerName) {
        return delegate.removeListener(listenerName);
    }

    @Override
    public CronListener getListener(String listenerName) {
        return delegate.getListener(listenerName);
    }

    @Override
    public long getListenerSize() {
        return delegate.getListenerSize();
    }

    @Override
    public List<CronListener> getAllListeners() {
        return delegate.getAllListeners();
    }

    @Override
    public boolean isSupportedExpression(String expression) {
        return delegate.isSupportedExpression(expression);
    }

    @Override
    public void checkSupportedExpression(String expression) throws CronExpressionInvalidException {
        delegate.checkSupportedExpression(expression);
    }

    @Override
    public long getTaskRemainingNumberOfRuns(String id) {
        return delegate.getTaskRemainingNumberOfRuns(id);
    }

    @Override
    public long getRemainingLimitedRunTimesTaskCount() {
        return delegate.getRemainingLimitedRunTimesTaskCount();
    }

    @Override
    public RunningTimeout getTimeoutConfig(String id) {
        return delegate.getTimeoutConfig(id);
    }

    @Override
    public CronTaskInfo customizeCronTaskInfo(CronTaskInfo cronTaskInfo) {
        return delegate.customizeCronTaskInfo(cronTaskInfo);
    }

    @Override
    public Runnable unwrapRunnable(Runnable given) {
        return delegate.unwrapRunnable(given);
    }

    @Override
    public boolean isSupportConcurrentExecution() {
        return delegate.isSupportConcurrentExecution();
    }

    @Override
    public boolean hasDisallowConcurrentExecution(String id) {
        return delegate.hasDisallowConcurrentExecution(id);
    }

    @Override
    public void disallowConcurrentExecution(String id) throws NotSupportConcurrentExecutionException {
        delegate.disallowConcurrentExecution(id);
    }

    @Override
    public void cancelDisallowConcurrentExecution(String id) throws CannotCancelConcurrentException {
        delegate.cancelDisallowConcurrentExecution(id);
    }

    @Override
    public void setIDGenerator(@Nullable IDGenerator idGenerator) {
        delegate.setIDGenerator(idGenerator);
    }

    @Override
    public IDGenerator getIDGenerator() {
        return delegate.getIDGenerator();
    }

    @Override
    public LongTimedExecutor longTimed(String... tags) {
        return delegate.longTimed(tags);
    }

    @Override
    public Builder newBuilder() {
        return delegate.newBuilder();
    }

    @Override
    public Class<? extends TaskBody>[] getSupportTaskBodyClasses() {
        return delegate.getSupportTaskBodyClasses();
    }

    @Override
    public CronTaskExtendInfo getExtendInfo(String id) {
        return delegate.getExtendInfo(id);
    }

    @Override
    public String register(String expression, Runnable runnable) throws CronInternalException {
        return delegate.register(expression, runnable);
    }

    @Override
    public String register(String expression, CronMethodRunnable runnable) throws CronInternalException {
        return delegate.register(expression, runnable);
    }

    @Override
    public String register(String expression, RunnableTaskBody body) throws CronInternalException {
        return delegate.register(expression, body);
    }

    @Override
    public String register(String expression, TaskBody body) throws CronInternalException {
        return delegate.register(expression, body);
    }

    @Override
    public String register(CronTask task) throws CronInternalException {
        return delegate.register(task);
    }

    @Override
    public void reStart() {
        delegate.reStart();
    }

    @Override
    public boolean hasCronTaskInfo(String id) {
        return delegate.hasCronTaskInfo(id);
    }

    @Override
    public CronTaskInfo getCronTaskInfo(String id) {
        return delegate.getCronTaskInfo(id);
    }

    @Override
    public List<CronTaskInfo> getAllCronTaskInfos() {
        return delegate.getAllCronTaskInfos();
    }

    @Override
    public List<String> getAllRegisteredTaskIds() {
        return delegate.getAllRegisteredTaskIds();
    }

    @Override
    public boolean isTaskRunning(String id) {
        return delegate.isTaskRunning(id);
    }

    @Override
    public List<String> getAllRunningTaskIds() {
        return delegate.getAllRunningTaskIds();
    }

    @Override
    public Long getNextExecuteTime(String id) {
        return delegate.getNextExecuteTime(id);
    }

    @Override
    public Map<String, Long> getNextExecuteTimes(Collection<String> ids) {
        return delegate.getNextExecuteTimes(ids);
    }

    @Override
    public void update(String id, String newExpression) throws CronInternalException {
        delegate.update(id, newExpression);
    }

    @Override
    public void remove(String id) throws CronInternalException {
        delegate.remove(id);
    }

    @Override
    public void removeAll() throws CronInternalException {
        delegate.removeAll();
    }

    @Override
    public void terminate(String id) throws CronInternalException {
        delegate.terminate(id);
    }

    @Override
    public void terminateAll() throws CronInternalException {
        delegate.terminateAll();
    }

    @Override
    public String register(String expression, Runnable runnable, RunningTimeout timeout) throws CronInternalException {
        return delegate.register(expression, runnable, timeout);
    }

    @Override
    public String register(String expression, CronMethodRunnable runnable, RunningTimeout timeout) throws CronInternalException {
        return delegate.register(expression, runnable, timeout);
    }

    @Override
    public String register(String expression, RunnableTaskBody body, RunningTimeout timeout) throws CronInternalException {
        return delegate.register(expression, body, timeout);
    }

    @Override
    public String register(String expression, TaskBody body, RunningTimeout timeout) throws CronInternalException {
        return delegate.register(expression, body, timeout);
    }

    @Override
    public String register(CronTask task, RunningTimeout timeout) throws CronInternalException {
        return delegate.register(task, timeout);
    }

    @Override
    public String registerRunTimes(String expression, Runnable runnable, int times, RunningTimeout timeout) throws CronInternalException {
        return delegate.registerRunTimes(expression, runnable, times, timeout);
    }

    @Override
    public String registerRunTimes(String expression, CronMethodRunnable runnable, int times, RunningTimeout timeout) throws CronInternalException {
        return delegate.registerRunTimes(expression, runnable, times, timeout);
    }

    @Override
    public String registerRunTimes(String expression, RunnableTaskBody body, int times, RunningTimeout timeout) throws CronInternalException {
        return delegate.registerRunTimes(expression, body, times, timeout);
    }

    @Override
    public String registerRunTimes(String expression, TaskBody body, int times, RunningTimeout timeout) throws CronInternalException {
        return delegate.registerRunTimes(expression, body, times, timeout);
    }

    @Override
    public String registerRunTimes(CronTask task, int times, RunningTimeout timeout) throws CronInternalException {
        return delegate.registerRunTimes(task, times, timeout);
    }

    @Override
    public String registerRunTimes(String expression, Runnable runnable, int times) throws CronInternalException {
        return delegate.registerRunTimes(expression, runnable, times);
    }

    @Override
    public String registerRunTimes(String expression, CronMethodRunnable runnable, int times) throws CronInternalException {
        return delegate.registerRunTimes(expression, runnable, times);
    }

    @Override
    public String registerRunTimes(String expression, RunnableTaskBody body, int times) throws CronInternalException {
        return delegate.registerRunTimes(expression, body, times);
    }

    @Override
    public String registerRunTimes(String expression, TaskBody body, int times) throws CronInternalException {
        return delegate.registerRunTimes(expression, body, times);
    }

    @Override
    public String registerRunTimes(CronTask task, int times) throws CronInternalException {
        return delegate.registerRunTimes(task, times);
    }
}
