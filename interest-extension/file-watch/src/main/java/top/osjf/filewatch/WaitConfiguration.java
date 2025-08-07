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


package top.osjf.filewatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * File creation/modification/deletion completion detection configuration.
 *
 * <p>Provides monitoring capability for file creation/modification process, determines upload
 * completion by checking file size and modification time stability
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see FileWatchService#registerWaitConfiguration(String, String, WaitConfiguration)
 * @see WaitConfigurations#registerWaitConfiguration(Path, Path, WaitConfiguration)
 */
public class WaitConfiguration implements BiFunction<Path, WatchEvent.Kind<Path>, Boolean> {

    /** Internal check create interval value */
    private Long waitCreateInternalTimeout = 1L;

    /** Internal check create interval unit */
    private TimeUnit waitCreateInternalTimeUnit = TimeUnit.SECONDS;

    /** Total wait create timeout value */
    private Long waitCreateTimeout = 60L;

    /** Total wait create timeout unit */
    private TimeUnit waitCreateTimeUnit = TimeUnit.SECONDS;

    /** Total wait create inspection frequency */
    private Integer waitCreateInspectionFrequency = 3;

    /** Internal check modify interval value */
    private Long waitModifyInternalTimeout = 1L;

    /** Internal check modify interval unit */
    private TimeUnit waitModifyInternalTimeUnit = TimeUnit.SECONDS;

    /** Total wait modify timeout value */
    private Long waitModifyTimeout = 60L;

    /** Total wait modify timeout unit */
    private TimeUnit waitModifyTimeUnit = TimeUnit.SECONDS;

    /** Total wait modify inspection frequency */
    private Integer waitModifyInspectionFrequency = 3;

    /** Internal check delete interval value */
    private Long waitDeleteInternalTimeout = 1L;

    /** Internal check delete interval unit */
    private TimeUnit waitDeleteInternalTimeUnit = TimeUnit.SECONDS;

    /** Total wait delete timeout value */
    private Long waitDeleteTimeout = 60L;

    /** Total wait delete timeout unit */
    private TimeUnit waitDeleteTimeUnit = TimeUnit.SECONDS;

    /** Total wait delete inspection frequency */
    private Integer waitDeleteInspectionFrequency = 3;

    /** Universal waiting to complete work function instance. */
    private final GenericWaitAction genericWaitAction = new GenericWaitAction(this);

    /** Delete the waiting to complete work function instance. */
    private final DeletionWaitAction deletionWaitAction = new DeletionWaitAction(this);

    /**
     * Core method to detect file creation/modification completion.
     * @param path the specific path to monitor.
     * @param kind the specific file event type
     * @return {@code true} indicates stable file creation/modification/deletion, {@code false}
     *          means timeout or exception
     */
    @Override
    public Boolean apply(Path path, WatchEvent.Kind<Path> kind) {
        if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            return genericWaitAction.apply(path, kind);
        }
        else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            return deletionWaitAction.apply(path, kind);
        }
        return false;
    }

    /**
     * Gets the WaitInternalTimeout by {@link TriggerKind}.
     * @param kind the referenced {@link TriggerKind}.
     * @return value of WaitInternalTimeout.
     */
    public Long getWaitInternalTimeout(TriggerKind kind) {
        switch (kind) {
            case ENTRY_CREATE: return waitCreateInternalTimeout;
            case ENTRY_MODIFY: return waitModifyInternalTimeout;
            case ENTRY_DELETE: return waitDeleteInternalTimeout;
        }
        return null;
    }

    /**
     * Gets the WaitInternalTimeUnit by {@link TriggerKind}.
     * @param kind the referenced {@link TriggerKind}.
     * @return value of WaitInternalTimeUnit.
     */
    public TimeUnit getWaitInternalTimeUnit(TriggerKind kind) {
        switch (kind) {
            case ENTRY_CREATE: return waitCreateInternalTimeUnit;
            case ENTRY_MODIFY: return waitModifyInternalTimeUnit;
            case ENTRY_DELETE: return waitDeleteInternalTimeUnit;
        }
        return null;
    }

    /**
     * Gets the WaitTimeout by {@link TriggerKind}.
     * @param kind the referenced {@link TriggerKind}.
     * @return value of WaitTimeout.
     */
    public Long getWaitTimeout(TriggerKind kind) {
        switch (kind) {
            case ENTRY_CREATE: return waitCreateTimeout;
            case ENTRY_MODIFY: return waitModifyTimeout;
            case ENTRY_DELETE: return waitDeleteTimeout;
        }
        return null;
    }

    /**
     * Gets the WaitTimeUnit by {@link TriggerKind}.
     * @param kind the referenced {@link TriggerKind}.
     * @return value of WaitTimeUnit.
     */
    public TimeUnit getWaitTimeUnit(TriggerKind kind) {
        switch (kind) {
            case ENTRY_CREATE: return waitCreateTimeUnit;
            case ENTRY_MODIFY: return waitModifyTimeUnit;
            case ENTRY_DELETE: return waitDeleteTimeUnit;
        }
        return null;
    }

    /**
     * Gets the InspectionFrequency by {@link TriggerKind}.
     * @param kind the referenced {@link TriggerKind}.
     * @return value of InspectionFrequency.
     */
    public Integer getWaitInspectionFrequency(TriggerKind kind) {
        switch (kind) {
            case ENTRY_CREATE: return waitCreateInspectionFrequency;
            case ENTRY_MODIFY: return waitModifyInspectionFrequency;
            case ENTRY_DELETE: return waitDeleteInspectionFrequency;
        }
        return null;
    }

    /* Set and Get */

    public Long getWaitCreateInternalTimeout() {
        return waitCreateInternalTimeout;
    }

    public void setWaitCreateInternalTimeout(Long waitCreateInternalTimeout) {
        this.waitCreateInternalTimeout = waitCreateInternalTimeout;
    }

    public TimeUnit getWaitCreateInternalTimeUnit() {
        return waitCreateInternalTimeUnit;
    }

    public void setWaitCreateInternalTimeUnit(TimeUnit waitCreateInternalTimeUnit) {
        this.waitCreateInternalTimeUnit = waitCreateInternalTimeUnit;
    }

    public Long getWaitCreateTimeout() {
        return waitCreateTimeout;
    }

    public void setWaitCreateTimeout(Long waitCreateTimeout) {
        this.waitCreateTimeout = waitCreateTimeout;
    }

    public TimeUnit getWaitCreateTimeUnit() {
        return waitCreateTimeUnit;
    }

    public void setWaitCreateTimeUnit(TimeUnit waitCreateTimeUnit) {
        this.waitCreateTimeUnit = waitCreateTimeUnit;
    }

    public Integer getWaitCreateInspectionFrequency() {
        return waitCreateInspectionFrequency;
    }

    public void setWaitCreateInspectionFrequency(Integer waitCreateInspectionFrequency) {
        this.waitCreateInspectionFrequency = waitCreateInspectionFrequency;
    }

    public Long getWaitModifyInternalTimeout() {
        return waitModifyInternalTimeout;
    }

    public void setWaitModifyInternalTimeout(Long waitModifyInternalTimeout) {
        this.waitModifyInternalTimeout = waitModifyInternalTimeout;
    }

    public TimeUnit getWaitModifyInternalTimeUnit() {
        return waitModifyInternalTimeUnit;
    }

    public void setWaitModifyInternalTimeUnit(TimeUnit waitModifyInternalTimeUnit) {
        this.waitModifyInternalTimeUnit = waitModifyInternalTimeUnit;
    }

    public Long getWaitModifyTimeout() {
        return waitModifyTimeout;
    }

    public void setWaitModifyTimeout(Long waitModifyTimeout) {
        this.waitModifyTimeout = waitModifyTimeout;
    }

    public TimeUnit getWaitModifyTimeUnit() {
        return waitModifyTimeUnit;
    }

    public void setWaitModifyTimeUnit(TimeUnit waitModifyTimeUnit) {
        this.waitModifyTimeUnit = waitModifyTimeUnit;
    }

    public Integer getWaitModifyInspectionFrequency() {
        return waitModifyInspectionFrequency;
    }

    public void setWaitModifyInspectionFrequency(Integer waitModifyInspectionFrequency) {
        this.waitModifyInspectionFrequency = waitModifyInspectionFrequency;
    }

    public Long getWaitDeleteInternalTimeout() {
        return waitDeleteInternalTimeout;
    }

    public void setWaitDeleteInternalTimeout(Long waitDeleteInternalTimeout) {
        this.waitDeleteInternalTimeout = waitDeleteInternalTimeout;
    }

    public TimeUnit getWaitDeleteInternalTimeUnit() {
        return waitDeleteInternalTimeUnit;
    }

    public void setWaitDeleteInternalTimeUnit(TimeUnit waitDeleteInternalTimeUnit) {
        this.waitDeleteInternalTimeUnit = waitDeleteInternalTimeUnit;
    }

    public Long getWaitDeleteTimeout() {
        return waitDeleteTimeout;
    }

    public void setWaitDeleteTimeout(Long waitDeleteTimeout) {
        this.waitDeleteTimeout = waitDeleteTimeout;
    }

    public TimeUnit getWaitDeleteTimeUnit() {
        return waitDeleteTimeUnit;
    }

    public void setWaitDeleteTimeUnit(TimeUnit waitDeleteTimeUnit) {
        this.waitDeleteTimeUnit = waitDeleteTimeUnit;
    }

    public Integer getWaitDeleteInspectionFrequency() {
        return waitDeleteInspectionFrequency;
    }

    public void setWaitDeleteInspectionFrequency(Integer waitDeleteInspectionFrequency) {
        this.waitDeleteInspectionFrequency = waitDeleteInspectionFrequency;
    }

    /* help instance */

    private static class GenericWaitAction implements BiFunction<Path, WatchEvent.Kind<Path>, Boolean> {
        private final WaitConfiguration configuration;

        public GenericWaitAction(WaitConfiguration configuration) {
            this.configuration = configuration;
        }

        @Override
        public Boolean apply(Path path, WatchEvent.Kind<Path> kind) {
            TriggerKind triggerKind = TriggerKind.valueOf(kind.name());
            long timeoutMillis = getWaitTimeUnit(triggerKind).toMillis(getWaitTimeout(triggerKind));
            TimeUnit waitInternalTimeUnit = getWaitInternalTimeUnit(triggerKind);
            long waitInternalTimeout = getWaitInternalTimeout(triggerKind);
            int waitInspectionFrequency = getWaitInspectionFrequency(triggerKind);
            long startMillis = System.currentTimeMillis();
            return applyInternal(path, timeoutMillis, waitInternalTimeUnit,
                    waitInternalTimeout, waitInspectionFrequency, startMillis);
        }
        protected Boolean applyInternal(Path path,
                                        long timeoutMillis,
                                        TimeUnit waitInternalTimeUnit,
                                        long waitInternalTimeout,
                                        int waitInspectionFrequency, long startMillis) {
            int stability = 0;
            do {
                try {
                    if (!Files.exists(path)) return false;
                    // Get size before.
                    long sizeBefore = Files.size(path);
                    // Get last modified time before.
                    FileTime fileTimeBefore = Files.getLastModifiedTime(path);
                    // Wait.
                    waitInternalTimeUnit.sleep(waitInternalTimeout);
                    // Get size after.
                    long sizeAfter = Files.size(path);
                    // Get last modified time after.
                    FileTime fileTimeAfter = Files.getLastModifiedTime(path);
                    // Meet at times.
                    if (sizeBefore == sizeAfter && sizeBefore > 0 && fileTimeBefore.equals(fileTimeAfter)) {
                        stability++;
                        if (stability >= waitInspectionFrequency) {
                            return true;
                        }
                    }
                }
                catch (IOException | SecurityException | InterruptedException ex) {
                    if (ex instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    return false;
                }
            } while ((System.currentTimeMillis() - startMillis) < timeoutMillis);
            return false;
        }

        protected Long getWaitInternalTimeout(TriggerKind kind) {
            return configuration.getWaitInternalTimeout(kind);
        }

        protected TimeUnit getWaitInternalTimeUnit(TriggerKind kind) {
            return configuration.getWaitInternalTimeUnit(kind);
        }

        protected Long getWaitTimeout(TriggerKind kind) {
            return configuration.getWaitTimeout(kind);
        }

        protected TimeUnit getWaitTimeUnit(TriggerKind kind) {
            return configuration.getWaitTimeUnit(kind);
        }

        protected Integer getWaitInspectionFrequency(TriggerKind kind) {
            return configuration.getWaitInspectionFrequency(kind);
        }
    }

    private static class DeletionWaitAction extends GenericWaitAction {

        public DeletionWaitAction(WaitConfiguration configuration) {
            super(configuration);
        }

        @Override
        protected Boolean applyInternal(Path path,
                                        long timeoutMillis,
                                        TimeUnit waitInternalTimeUnit,
                                        long waitInternalTimeout,
                                        int waitInspectionFrequency, long startMillis) {
            int stability = 0;
            do {
                try {
                    if (Files.exists(path)) {
                        // Wait.
                        waitInternalTimeUnit.sleep(waitInternalTimeout);
                    }
                    else {
                        // Meet at times.
                        if (++stability >= waitInspectionFrequency) {
                            return true;
                        }
                    }
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return false;
                }
                catch (SecurityException ex) {
                    return false;
                }
            } while ((System.currentTimeMillis() - startMillis) < timeoutMillis);
            return false;
        }
    }
}
