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

/**
 * File creation/modification completion detection configuration.
 *
 * <p>Provides monitoring capability for file creation/modification process, determines upload
 * completion by checking file size and modification time stability
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see FileWatchService#registerWaitConfiguration(String, String, WaitConfiguration)
 * @see WaitConfigurations#registerWaitConfiguration(Path, Path, WaitConfiguration)
 */
public class WaitConfiguration {

    /**
     * Default configuration instance (empty implementation, directly returns {@code true})
     */
    public static final WaitConfiguration INSTANCE = new WaitConfiguration() {
        @Override
        public boolean waitComplete(Path filePath, WatchEvent.Kind<Path> kind) {
            return true;
        }
    };

    /** Internal check interval value */
    private Long waitCreateInternalTimeout = 1L;

    /** Internal check interval unit */
    private TimeUnit waitCreateInternalTimeUnit = TimeUnit.SECONDS;

    /** Total wait timeout value */
    private Long waitCreateTimeout = 60L;

    /** Total wait timeout unit */
    private TimeUnit waitCreateTimeUnit = TimeUnit.SECONDS;

    /**
     * Core method to detect file creation/modification completion.
     * @param path the specific path to monitor.
     * @param kind the specific file event type
     * @return {@code true} indicates stable file creation/modification, {@code false}
     *          means timeout or exception
     */
    public boolean waitComplete(Path path, WatchEvent.Kind<Path> kind) {
        if (kind != StandardWatchEventKinds.ENTRY_CREATE || kind != StandardWatchEventKinds.ENTRY_MODIFY) {
            return true;
        }
        long timeoutMillis = waitCreateTimeUnit.toMillis(waitCreateTimeout);
        long startMillis = System.currentTimeMillis();
        int stability = 0;
        do {
            try {
                if (!Files.exists(path)) return false;
                // Get size before.
                long sizeBefore = Files.size(path);
                // Get last modified time before.
                FileTime fileTimeBefore = Files.getLastModifiedTime(path);
                // Wait.
                waitCreateInternalTimeUnit.sleep(waitCreateInternalTimeout);
                // Get size after.
                long sizeAfter = Files.size(path);
                // Get last modified time after.
                FileTime fileTimeAfter = Files.getLastModifiedTime(path);
                // Meet at least 3 times.
                if (sizeBefore == sizeAfter && sizeBefore > 0 && fileTimeBefore.equals(fileTimeAfter)) {
                    stability++;
                    if (stability >= 3) {
                        return true;
                    }
                }
            }
            catch (IOException | InterruptedException ex) {
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                return false;
            }
        } while ((System.currentTimeMillis() - startMillis) < timeoutMillis);
        return false;
    }

    public long getWaitCreateInternalTimeout() {
        return waitCreateInternalTimeout;
    }

    public void setWaitCreateInternalTimeout(long waitCreateInternalTimeout) {
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
}
