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


package top.osjf.cron.quartz.repository;

import com.google.common.collect.HashBiMap;
import org.quartz.JobKey;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.util.Assert;
import top.osjf.commons.util.StringUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Bidirectional in-memory mapping manager for task ID and Quartz {@code JobKey}.
 * Implemented based on Guava {@code HashBiMap} with bidirectional uniqueness constraint:
 * both task ID and {@code JobKey} must be globally unique.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class IdentityMemory {

    private final HashBiMap<String, JobKey> memory = HashBiMap.create();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * Store bidirectional mapping between task ID and {@code JobKey}
     * Both task ID and JobKey must be globally unique, duplicate insertion will trigger exception
     * @param id Global unique task ID, must not be {@code null}
     * @param jobKey Quartz unique job identifier, must not be {@code null}
     * @throws IdentityMemoryException Thrown if task ID is duplicated, {@code JobKey} is already
     * bound to another ID, or storage fails
     */
    public void put(@NotNull String id, @NotNull JobKey jobKey) throws IdentityMemoryException {
        Assert.hasText(id, "id must not null or blank");
        Assert.notNull(jobKey, "jobKey must not null");

        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            memory.put(id, jobKey);
        }
        catch (IllegalArgumentException ex) {
            throw new IdentityMemoryException(ex.getMessage(), ex);
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Look up corresponding JobKey via task ID
     * @param id Global unique task ID for lookup, must not be {@code null}
     * @return Bound Quartz {@code JobKey} of target task
     * @throws IdentityMemoryException Thrown if no mapping record matches the input task ID
     */
    public JobKey getJobKeyById(@NotNull String id) throws IdentityMemoryException {
        Assert.hasText(id, "id must not null or blank");

        Lock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            JobKey jobKey = memory.get(id);
            if (jobKey == null) {
                throw new IdentityMemoryException("According to id <"+ id + ">, the corresponding" +
                        " org.quartz.JobKey was not found.");
            }
            return jobKey;
        }
        finally {
            readLock.unlock();
        }

    }

    /**
     * Reverse lookup global task ID via Quartz {@code JobKey}
     * @param jobKey Quartz unique job identifier, must not be {@code null}
     * @return Global unique task ID bound to target {@code JobKey}
     * @throws IdentityMemoryException Thrown if no task ID is bound to the input {@code JobKey}
     */
    public String getIdByJobKey(@NotNull JobKey jobKey) throws IdentityMemoryException {
        Assert.notNull(jobKey, "jobKey must not null");

        Lock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            String id = memory.inverse().get(jobKey);
            if (StringUtils.isBlank(id)) {
                throw new IdentityMemoryException("According to  org.quartz.JobKey <"+ jobKey + ">, the corresponding" +
                        " id was not found.");
            }
            return id;
        }
        finally {
            readLock.unlock();
        }
    }

    /**
     * Delete bidirectional mapping record by task ID
     * @param id Unique task ID whose mapping will be removed, must not be {@code null}
     * @throws IdentityMemoryException Thrown if no mapping exists for the input task ID
     */
    public void removeById(@NotNull String id) throws IdentityMemoryException {
        Assert.hasText(id, "id must not null or blank");

        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            JobKey jobKey = memory.remove(id);
            if (jobKey == null) {
                throw new IdentityMemoryException("According to id <"+ id + ">, the corresponding" +
                        " org.quartz.JobKey was not found.");
            }
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Clear all bidirectional cached mappings between task ID and JobKey in memory.
     */
    public void clear() {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            memory.clear();
        }
        finally {
            writeLock.unlock();
        }
    }
}
