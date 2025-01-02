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


package top.osjf.cron.quartz.repository;

import org.quartz.SchedulerConfigException;
import org.quartz.spi.ThreadPool;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * The {@code TaskExecutorDelegateThreadPool} class implements Quartz's ThreadPool
 * interface, and it allows Quartz to use an external Executor to execute tasks.
 *
 * <p>This class is primarily used to provide a bridge between the Quartz scheduler
 * and user-defined thread pools.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class TaskExecutorDelegateThreadPool implements ThreadPool {

    private static final ThreadLocal<Executor> EXECUTOR_LOCAL_STORE = new ThreadLocal<>();

    private Executor taskExecutor;

    protected static void setTaskExecutor(Executor taskExecutor) {
        if (taskExecutor != null) {
            EXECUTOR_LOCAL_STORE.set(taskExecutor);
        }
    }

    @Override
    public boolean runInThread(Runnable runnable) {
        try {
            this.taskExecutor.execute(runnable);
        }
        catch (RejectedExecutionException e) {
            return false;
        }
        return true;
    }

    /**
     * When customizing the thread pool for execution, it is always ensured that
     * the number of available threads is greater than 0, and all execution issues
     * are handled by the {@link #runInThread} method.
     *
     * @return {@inheritDoc}
     */
    @Override
    public int blockForAvailableThreads() {
        return 1;
    }

    @Override
    public void initialize() throws SchedulerConfigException {
        Executor executor = EXECUTOR_LOCAL_STORE.get();
        if (executor != null) {
            this.taskExecutor = executor;
            EXECUTOR_LOCAL_STORE.remove();
        } else {
            throw new SchedulerConfigException
                    ("No task thread pool instance provided for proxy execution.");
        }
    }

    @Override
    public void shutdown(boolean waitForJobsToComplete) {
        if (taskExecutor instanceof ExecutorService) {
            if (waitForJobsToComplete) {
                ((ExecutorService) taskExecutor).shutdown();
            } else {
                ((ExecutorService) taskExecutor).shutdownNow();
            }
        }
    }

    @Override
    public int getPoolSize() {
        return -1;
    }

    @Override
    public void setInstanceId(String schedInstId) {

    }

    @Override
    public void setInstanceName(String schedName) {

    }
}
