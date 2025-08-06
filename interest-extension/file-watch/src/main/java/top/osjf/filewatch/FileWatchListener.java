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


package top.osjf.filewatch;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * An interface for receiving file system watch events.
 *
 * <p>Implementations of this interface can be registered with a WatchService
 * to receive notifications when files in a watched directory are modified.
 *
 * <p>Typical usage involves implementing both {@code supports()} to filter
 * relevant events and {@code onWatchEvent()} to handle them.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see java.nio.file.WatchService
 * @see java.nio.file.WatchEvent
 * @see java.nio.file.StandardWatchEventKinds
 * @see FileWatchService#registerListener(FileWatchListener)
 * @see FileWatchListeners#registerListener(FileWatchListener)
 */
public interface FileWatchListener extends PathExclusive {
    /**
     * Determines whether this listener is interested in the given watch event.
     *
     * <p>This method allows listeners to filter events by type or path before
     * processing. For example, a listener might only care about MODIFY events
     * for properties files.
     *
     * <p><strong>NOTE:</strong>
     * Implementations should not perform lengthy operations in this method
     * as it's called synchronously during event dispatch and returns a fixed
     * {@code boolean} type, and cannot throw exception information.
     *
     * @param event the watch event to evaluate (never {@literal null});
     * @return {@code true} if this listener should handle the event, {@code false}
     *          to ignore it.
     */
    boolean supports(WatchEvent<Path> event);

    /**
     * Processes a watch event that passed the {@link #supports} check.
     *
     * <p>This method is invoked asynchronously for each matching event.
     * Implementations should handle events efficiently to avoid blocking
     * the watch service.
     *
     * <p>Common event handling patterns include:
     * <ul>
     *   <li>Reloading configuration files when modified.</li>
     *   <li>Processing new files in an upload directory.</li>
     *   <li>Maintaining a cache of directory contents.</li>
     * </ul>
     *
     * @param event the watch event to process (never {@literal null})
     * @throws RuntimeException if event processing fails. Implementations should
     *         generally handle their own exceptions and only throw for fatal errors.
     *
     * @see java.nio.file.StandardWatchEventKinds#ENTRY_CREATE
     * @see java.nio.file.StandardWatchEventKinds#ENTRY_MODIFY
     * @see java.nio.file.StandardWatchEventKinds#ENTRY_DELETE
     */
    void onWatchEvent(WatchEvent<Path> event);
}
