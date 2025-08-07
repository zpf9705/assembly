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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

    /** This is the mapping relationship from Path to {@code FileWatchService}. */
    private Map<Path, FileWatchService> pathToServiceMap;

    /** The underlying watch service instance.*/
    private WatchService watchService;

    /** The lock of register path */
    private Lock lock;

    /** The list of registered listening paths. */
    private final List<Path> registeredPaths = new ArrayList<>();

    /** Mapping between watch keys and their associated registered paths. */
    private final Map<WatchKey, Path> watchKeyregisteredPathMap = new HashMap<>();

    /** Management instance of listener {@link FileWatchListener}.*/
    private FileWatchListeners fileWatchListeners;

    /** The designated file created/modified/deleted under the path is waiting for the completion of the
     * configuration management instance. */
    private WaitConfigurations waitConfigurations;

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
        pathToServiceMap = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
        fileWatchListeners = new FileWatchListeners();
        waitConfigurations = new WaitConfigurations();
    }

    /**
     * Private Constructs a {@link FileWatchService} with given {@link FileWatchListeners}
     * and {@link WaitConfigurations}.
     * @param fileWatchListeners        the given {@link FileWatchListeners} instance.
     * @param waitConfigurations        the given {@link WaitConfigurations} instance.
     */
    private FileWatchService(FileWatchListeners fileWatchListeners,
                             WaitConfigurations waitConfigurations) {
        this.fileWatchListeners = fileWatchListeners;
        this.waitConfigurations = waitConfigurations;
    }

    /**
     * Registers a file path with the WatchService to monitor specified file system events.
     * <p>
     * This method registers the given path with the WatchService, enabling notification
     * when any of the specified event types occur in the monitored directory.
     *
     * @param path                The file system path to monitor (absolute or relative path).
     * @param peculiarWatchThread Whether to create a new independent {@link FileWatchService}.
     * @param kinds               The array of event types to watch for (CREATE, MODIFY, DELETE, etc.).
     * @throws NullPointerException if `path` or `kinds` is `null`.
     * @throws FileWatchException in the following cases:
     *                         - If the path is invalid (`InvalidPathException`)
     *                         - If registration fails (`IOException`)
     *
     * <p>Example usage:
     * {@code
     * registerWatch("/var/log", TriggerKind.CREATE, TriggerKind.MODIFY);
     * }
     *
     * @see java.nio.file.WatchService
     * @see java.nio.file.StandardWatchEventKinds
     */
    public void registerWatch(String path, boolean peculiarWatchThread, TriggerKind... kinds) {
        if (path == null || kinds == null) {
            throw new NullPointerException("path or triggerKind");
        }
        Path registeredPath = Paths.get(path);
        if (peculiarWatchThread) {
            pathToServiceMap.compute(registeredPath, (key, fileWatchService) -> {
                if (fileWatchService == null) {
                    fileWatchService = new FileWatchService(fileWatchListeners, waitConfigurations);
                }
                fileWatchService.registerWatch(path, false, kinds);
                return fileWatchService;
            });
        }
        else {
            if (lock != null) lock.lock();
            try {
                if (registeredPaths.contains(registeredPath)) {
                    throw new IllegalArgumentException("Duplicate registration " + registeredPath);
                }
                WatchEvent.Kind<?>[] events = new WatchEvent.Kind[kinds.length];
                for (int i = 0; i < kinds.length; i++) events[i] = kinds[i].kind;
                registeredPaths.add(registeredPath);
                watchKeyregisteredPathMap.put(registeredPath.register(watchService, events), registeredPath);
            }
            catch (InvalidPathException ex) {
                throw new FileWatchException("Invalid path " + path, ex);
            }
            catch (IOException ex) {
                throw new FileWatchException("Failed to register WatchService", ex);
            }
            finally {
                if (lock != null) lock.unlock();
            }
        }
    }

    /**
     * Register a {@link FileWatchListener listener} to call back information about
     * changes in the specified path file.
     * @param listener the specific {@link FileWatchService} to register.
     */
    public void registerListener(FileWatchListener listener) {
        fileWatchListeners.registerListener(listener);
    }

    /**
     * Register a specified file creation/modification/deletion notification
     * {@link StandardWatchEventKinds#ENTRY_CREATE} {@link StandardWatchEventKinds#ENTRY_MODIFY}
     * {@link StandardWatchEventKinds#ENTRY_DELETE} and configure the waiting time for completion
     * of creation {@code WaitCreateConfiguration}.
     * @param parent        the parent directory path to register.
     * @param pathContext   the context path for watching to register.
     * @param configuration the specific waiting time for completion of creation {@code WaitCreateConfiguration}
     *                      to register.
     */
    public void registerWaitConfiguration(String parent, String pathContext, WaitConfiguration configuration) {
        waitConfigurations.registerWaitConfiguration(Paths.get(parent), Paths.get(pathContext), configuration);
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
            Path registeredPath = watchKeyregisteredPathMap.get(key);
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
                // wait file complete ...
                if (waitConfigurations.hasWaitConfiguration(registeredPath, pathContext)) {
                    if (!waitConfigurations.getWaitConfiguration(registeredPath, pathContext)
                            .apply(registeredPath.resolve(pathContext), kind)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Waiting for the completion of context {} creation timeout or " +
                                    "IO exception, please check if the corresponding path file exists or " +
                                    "is too large (the latter, please readjust the timeout).", pathContext);
                        }
                        continue;
                    }
                }
                for (FileWatchListener listener : fileWatchListeners.getListeners()) {
                    boolean isParentPath = Optional.ofNullable(listener.getPath())
                            .map(path -> path.equals(registeredPath)).orElse(true);
                    AmapleWatchEvent amapleWatchEvent = new AmapleWatchEvent(registeredPath, pathEvent);
                    if (isParentPath && listener.supports(amapleWatchEvent)) {
                        try {
                            listener.onWatchEvent(amapleWatchEvent);
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
                            "listening will be canceled.", registeredPath);
                }
                key.cancel();
                if (debugEnabled) {
                    LOGGER.warn("Monitoring of path {} has been cancelled.", registeredPath);
                }
            }
        }
    }

    @Override
    public Thread get() {
        return new Thread(this,"File " + registeredPaths + " watch-thread");
    }

    /**
     * Closes this watch service.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        watchService.close();
        if (pathToServiceMap != null) {
            for (FileWatchService service : pathToServiceMap.values()) {
                service.close();
            }
        }
    }
}
