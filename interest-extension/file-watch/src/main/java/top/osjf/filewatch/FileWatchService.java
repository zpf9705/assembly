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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 /**
 * A file monitoring service that watches registered directories for changes
 * and notifies registered listeners when events occur.
 *
 * <p>Typical usage:
 * <pre>{@code
 * FileWatchService watchService = new FileWatchService();
 * watchService.registerListener(new MyFileChangeListener());
 * watchService.registerWatches("/path/to/watch", "/another/path");
 * new Thread(watchService).start();
 * }</pre>
 *
 * <p>This implementation uses Java NIO's {@link WatchService} API and supports
 * concurrent event processing through a thread-safe listener registry.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 *
 * @see FileWatchListener
 * @see java.nio.file.WatchService
 * @see java.nio.file.WatchEvent
 * @see java.nio.file.StandardWatchEventKinds
 */
@SuppressWarnings("unchecked")
public class FileWatchService implements Runnable, Supplier<Thread>, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatchService.class);
    /**
     * The event kinds to register for monitoring.
     */
    private static final WatchEvent.Kind<?>[] KINDS = {StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.OVERFLOW};
    /**
     * The underlying watch service instance.
     */
    private WatchService watchService;

    /**
     * A mapping between watch keys and their associated paths.
     */
    private final Map<WatchKey, String> watchKeyMap = new ConcurrentHashMap<>();

    /**
     * Thread-safe list of registered file watch listeners.
     */
    private final CopyOnWriteArrayList<FileWatchListener> listeners = new CopyOnWriteArrayList<>();

    private final Map<String, WaitCreateConfiguration> waitCreateConfigurationMap = new ConcurrentHashMap<>();

    /**
     * Constructs an empty {@link FileWatchService} to init a {@link WatchService}.
     */
    public FileWatchService() { initWatchService(); }

    /**
     * Init a {@link WatchService} to support monitoring file changes in specified file paths.
     */
    private void initWatchService() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        }
        catch (IOException ex) {
            throw new FileWatchException("Failed to create java.nio.file.WatchService", ex);
        }
    }

    /**
     * Register a {@link FileWatchListener listener} to call back information about
     * changes in the specified path file.
     * @param listener the specific {@link FileWatchService} to register.
     */
    public void registerListener(FileWatchListener listener) {
        listeners.addIfAbsent(listener);
    }

    /**
     * Register file change monitoring for multiple specified paths.
     * @param paths the specific paths to register.
     */
    public void registerWatches(String... paths) {
        for (String path : paths) {
            registerWatch(path);
        }
    }

    /**
     * To register a file listener for {@link Path} with a specified path and detect
     * callback awareness when the file path changes, it is necessary to ensure that
     * it is a valid and traceable correct path.
     * @param path the specific path to register.
     * @throws NullPointerException If the path input is {@literal null}.
     * @throws FileWatchException   If the input path is invalid or the registration
     *                              listening fails.
     */
    public void registerWatch(String path) {
        if (path == null) {
            throw new NullPointerException("path");
        }
        Path obtainPath;
        try {
            obtainPath = Paths.get(path);
            watchKeyMap.put(obtainPath.register(watchService, KINDS), path);
        }
        catch (InvalidPathException ex) {
            throw new FileWatchException("Invalid path " + path, ex);
        }
        catch (IOException ex) {
            throw new FileWatchException("Failed to register WatchService", ex);
        }
    }

    /**
     * Register a specified file creation notification {@link StandardWatchEventKinds#ENTRY_CREATE}
     * and configure the waiting time for completion of creation {@code WaitCreateConfiguration}.
     * @param pathContext   the specific pathContext to register.
     * @param configuration the specific waiting time for completion of creation {@code WaitCreateConfiguration}
     *                      to register.
     */
    public void registerWaitCreateConfiguration(String pathContext, WaitCreateConfiguration configuration) {
        if (pathContext == null || configuration == null) {
            throw new NullPointerException("pathContext or configuration");
        }
        waitCreateConfigurationMap.putIfAbsent(pathContext, configuration);
    }

    /**
     * Main monitoring loop that processes file system events.
     * <p>This method runs indefinitely until the thread is interrupted.
     */
    @Override
    public void run() {
        while (true) {
            WatchKey key;
            try { key = watchService.take(); }
            catch (InterruptedException ex) {
                LOGGER.info("File watch service interrupted, shutting down...", ex);
                Thread.currentThread().interrupt(); // interrupt action.
                break;
            }
            String path = watchKeyMap.get(key);
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                Path pathContext = pathEvent.context();
                WatchEvent.Kind<Path> kind = pathEvent.kind();
                LOGGER.info("Watch event for path: {}, event type: {}", pathContext, kind);
                if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("{} event and event type {} to indicate that events may have been lost or " +
                                "discarded，do not perform callback processing.", pathContext, kind);
                    }
                    continue;
                }
                for (FileWatchListener listener : listeners) {
                    if (listener.supports(pathEvent)) {
                        if (!waitCreateConfigurationMap.getOrDefault(pathContext.toString(),
                                WaitCreateConfiguration.INSTANCE).waitComplete(Paths.get(path, pathContext.toString()), kind)) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Waiting for the completion of context {} creation timeout or " +
                                        "IO exception, please check if the corresponding path file exists or " +
                                        "is too large (the latter, please readjust the timeout).", pathContext);
                            }
                            continue;
                        }
                        try {
                            listener.onWatchEvent(pathEvent);
                        }
                        catch (Throwable ex) {
                            LOGGER.error("Failed to handle watch event for path: {}, event type: {}",
                                    pathContext, kind, ex);
                        }
                    }
                    else {
                        LOGGER.info("Unsupported notification context {}, event type: {}", pathContext, kind);
                    }
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                boolean debugEnabled = LOGGER.isDebugEnabled();
                if (debugEnabled) {
                    LOGGER.warn("Watch key cannot be reset, its corresponding {} is no longer valid, and " +
                            "listening will be canceled.", path);
                }
                key.cancel();
                if (debugEnabled) {
                    LOGGER.warn("Monitoring of path {} has been cancelled.", path);
                }
            }
        }
    }

    @Override
    public Thread get() {
        return new Thread(this);
    }

    /**
     * Closes this watch service.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        watchService.close();
    }
}
