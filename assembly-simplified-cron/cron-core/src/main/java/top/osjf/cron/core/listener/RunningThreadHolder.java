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


package top.osjf.cron.core.listener;

import top.osjf.commons.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread-safe container used to cache and manage all active running threads of scheduled tasks.
 *
 * It records the executing thread corresponding to each task unique ID, and provides capabilities
 * to unregister single thread, terminate all threads under a specified task, and globally interrupt
 * all cached task threads during application shutdown or task cancellation.
 *
 * The inner structure adopts {@link ConcurrentHashMap} combined with {@link ConcurrentHashMap.KeySetView}
 * to guarantee data security under multi-thread concurrent registration and removal scenarios.
 *
 * Only the thread interrupt flag will be set when interrupting threads; the business task code
 * needs to actively detect the interrupt status to exit execution gracefully.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class RunningThreadHolder {

    /**
     * Save the temporary cache map of the running task threads under the specified task ID.
     */
    private final Map<String, Set<Thread>> runningThreadMap = new ConcurrentHashMap<>();

    /**
     * Add the currently executing thread under the specified task ID to the cache.
     * @param id the unique identifier of the registered cron task.
     */
    public void addCurrentRunningThread(String id) {
        Thread currentThread = Thread.currentThread();
        runningThreadMap.computeIfAbsent(id,  k -> ConcurrentHashMap.newKeySet()).add(currentThread);
    }

    /**
     * Remove the currently executing thread under the specified task ID.
     * @param id the unique identifier of the registered cron task.
     */
    public void removeCurrentRunningThread(String id) {
        Set<Thread> threads = runningThreadMap.get(id);
        if (CollectionUtils.isEmpty(threads)) return;
        Thread thread = Thread.currentThread();
        if (threads.remove(thread)) interruptThread(thread);
    }

    /**
     * Remove all executing task threads under the specified task ID.
     * @param id the unique identifier of the registered cron task.
     */
    public void removeRunningThreads(String id) {
        Set<Thread> threads = runningThreadMap.remove(id);
        if (CollectionUtils.isEmpty(threads)) return;
        interruptThreads(threads);
    }

    /**
     * Remove all task threads cached in {@link #runningThreadMap} and clear the cache.
     */
    public void removeAllRunningThreads() {
        if (CollectionUtils.isEmpty(runningThreadMap)) return;
        for (Set<Thread> threads : runningThreadMap.values()) {
            interruptThreads(threads);
        }
        runningThreadMap.clear();
    }

    /**
     * Interrupt the specified thread collection.
     * @param threads the specified thread collection.
     */
    private void interruptThreads(Set<Thread> threads) {
        for (Thread thread : threads) {
            interruptThread(thread);
        }
    }

    /**
     * Interrupt a single specified thread.
     * @param thread a single specified thread.
     */
    private void interruptThread(Thread thread) {
        if (!thread.isAlive() || thread.isInterrupted()) {
            return;
        }
        try {
            thread.interrupt();
        }
        catch (Exception ignored) { }
    }
}
