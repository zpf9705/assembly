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

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

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
@SuppressWarnings({"unchecked", "rawtypes"})
public class FileWatchService implements Runnable, Closeable {

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

    /** The {@code Boolean} flag that indicates whether the template instance has been started.*/
    private AtomicBoolean isStarted;

    /** A thread pool used to support asynchronous execution of file listener change tasks {@link #run()}.*/
    private ExecutorService executor;

    /**
     * Constructs an empty {@link FileWatchService} to init a {@link WatchService}.
     */
    public FileWatchService() { initWatchService(true); }

    /**
     * Init a {@link WatchService} to support monitoring file changes in specified file paths.
     * @param isTemplate Is it a template prototype for {@link FileWatchService}.
     */
    private void initWatchService(boolean isTemplate) {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        }
        catch (IOException ex) {
            throw new FileWatchException("Failed to create java.nio.file.WatchService", ex);
        }
        if (isTemplate) {
            pathToServiceMap = new ConcurrentHashMap<>();
            lock = new ReentrantLock();
            fileWatchListeners = new FileWatchListeners();
            waitConfigurations = new WaitConfigurations();
            isStarted = new AtomicBoolean(false);
        }
    }

    /**
     * Private Constructs a {@link FileWatchService} with given {@link FileWatchListeners}
     * and {@link WaitConfigurations}.
     * @param fileWatchListeners        the given {@link FileWatchListeners} instance.
     * @param waitConfigurations        the given {@link WaitConfigurations} instance.
     */
    private FileWatchService(FileWatchListeners fileWatchListeners,
                             WaitConfigurations waitConfigurations) {
        initWatchService(false);
        this.fileWatchListeners = fileWatchListeners;
        this.waitConfigurations = waitConfigurations;
    }

    /**
     * Set a {@link Executor} for executing the listener task.
     * @param executor the thread pool instance that executes the listener task.
     * @since 3.0.2
     */
    public void setExecutor(ExecutorService executor) {
        this.executor = Objects.requireNonNull(executor, "executor == null");
    }

    /**
     * @return Return the thread pool instance that executes the listener task.
     * @since 3.0.2
     */
    public ExecutorService getExecutor() {
        if (executor != null) {
            return executor;
        }
        lock.lock();
        try {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            executor = new ThreadPoolExecutor(availableProcessors,
                    availableProcessors + 1, 60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(1000), r -> new Thread(r, r.toString()));
        }
        finally {
            lock.unlock();
        }
        return executor;
    }

    /**
     * Registers a file path with the {@code WatchService} to monitor specified file system events.
     * <p>
     * This method registers the given path with the {@code WatchService}, enabling notification
     * when any of the specified event types occur in the monitored directory.
     *
     * @param watchPath            the specify {@code FileWatchPath} to register.
     * @throws NullPointerException if {@code path} or {@code kinds} is {@literal null}.
     * @throws FileWatchException in the following cases:
     *                         - If the path is invalid ({@code InvalidPathException})
     *                         - If registration fails ({@code IOException})
     * @see #registerWatch(String, boolean, SensitivityWatchEventModifier, TriggerKind...)
     *
     * @see java.nio.file.WatchService
     * @see java.nio.file.StandardWatchEventKinds
     */
    public void registerWatch(FileWatchPath watchPath) {
        registerWatch(watchPath.getPath(), watchPath.isPeculiarWatchThread(),
                watchPath.getSensitivityModifier(), watchPath.getTriggerKinds());
    }

    /**
     * Registers a file path with the {@code WatchService} to monitor specified file system events.
     * <p>
     * This method registers the given path with the {@code WatchService}, enabling notification
     * when any of the specified event types occur in the monitored directory.
     *
     * @param path                The file system path to monitor (absolute or relative path).
     * @param peculiarWatchThread Whether to create a new independent {@link FileWatchService}.
     * @param sensitivityModifier Detecting sensitivity enumeration types.
     * @param triggerKinds        The array of event types to watch for (CREATE, MODIFY, DELETE, etc.).
     * @throws NullPointerException if {@code path} or {@code kinds} is {@literal null}.
     * @throws FileWatchException in the following cases:
     *                         - If the path is invalid ({@code InvalidPathException})
     *                         - If registration fails ({@code IOException})
     *
     * <p>Example usage:
     * {@code
     * registerWatch("/var/log", TriggerKind.CREATE, TriggerKind.MODIFY);
     * }
     *
     * @see java.nio.file.WatchService
     * @see java.nio.file.StandardWatchEventKinds
     */
    public void registerWatch(String path, boolean peculiarWatchThread,
                              SensitivityWatchEventModifier sensitivityModifier, TriggerKind... triggerKinds) {
        if (path == null || triggerKinds == null) {
            throw new NullPointerException("path or triggerKind");
        }
        Path registeredPath = Paths.get(path);
        if (peculiarWatchThread) {
            pathToServiceMap.compute(registeredPath, (key, fileWatchService) -> {
                if (fileWatchService == null) {
                    fileWatchService = new FileWatchService(fileWatchListeners, waitConfigurations);
                }
                fileWatchService.registerWatch(path, false, sensitivityModifier, triggerKinds);
                return fileWatchService;
            });
        }
        else {
            if (lock != null) lock.lock();
            try {
                if (registeredPaths.contains(registeredPath)) {
                    throw new IllegalArgumentException("Duplicate registration " + registeredPath);
                }
                WatchEvent.Kind<?>[] events = new WatchEvent.Kind[triggerKinds.length];
                for (int i = 0; i < triggerKinds.length; i++) events[i] = triggerKinds[i].kind;
                registeredPaths.add(registeredPath);
                watchKeyregisteredPathMap.put(registeredPath.register(watchService, events, sensitivityModifier),
                        registeredPath);
                LOGGER.info("File monitoring service for path {} has been registered.", registeredPath);
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
     * @param configuration the specific {@code BindingConfiguration} to register.
     */
    public void registerWaitConfiguration(BindingConfiguration configuration) {
        registerWaitConfiguration(configuration.getBindPath(),
                configuration.getPathContext(), configuration.getConfiguration());
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
                LOGGER.info("File watch service interrupted, shutting down...");
                Thread.currentThread().interrupt(); // interrupt action.
                break;
            }
            Path registeredPath = watchKeyregisteredPathMap.get(key);
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                Path pathContext = pathEvent.context();
                WatchEvent.Kind<Path> kind = pathEvent.kind();
                LOGGER.info("Watch event for context: {}, event type: {}", pathContext, kind);
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
                    if (!isParentPath) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Event path {} does not match the listener specified path {}," +
                                    " invalid notification filtering.", registeredPath, listener.getPath());
                        }
                        continue;
                    }
                    // Resolve define event type.
                    WatchEvent<Path> definedEvent;
                    try {
                        definedEvent = EventDefineTypeResolver.resolveEvent(listener, registeredPath, pathEvent);
                    }
                    catch (FileWatchException ex) {
                        LOGGER.error("Failed to instantiate the defined event type", ex);
                        continue;
                    }
                    if (listener.supports(definedEvent)) {
                        try {
                            listener.onWatchEvent(definedEvent);
                        }
                        catch (Throwable ex) {
                            LOGGER.error("Failed to handle watch event for context: {}, event type: {}",
                                    pathContext, kind, ex);
                        }
                    }
                    else {
                        LOGGER.info("Unsupported notification context {}, event type: {}, for {}", pathContext, kind,
                                listener.getClass().getName());
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

    /**
     * Activate the task of running the file listener for the current template instance
     * and the unique file listener for the special path, and convert it to {@link Thread}
     * for execution.
     * @throws IllegalStateException If it has already started.
     * @since 3.0.2
     */
    public void start() throws IllegalStateException {
        if (!isStarted.compareAndSet(false, true)) {
            throw new IllegalStateException("The file listener has started running!");
        }
        ExecutorService executor = getExecutor();
        executor.execute(this);
        peculiarFileWatchConsumer(executor::execute);
    }

    /**
     * Stop the currently running file listener and the dedicated path file listener.
     * @throws IllegalStateException if it has already stopped.
     * @since 3.0.2
     */
    public void stop() throws IllegalStateException {
        if (!isStarted.compareAndSet(true, false)) {
            throw new IllegalStateException("The file listener has stopped running!");
        }
        getExecutor().shutdownNow();
        closeWatchService(watchService);
        peculiarFileWatchConsumer(service -> closeWatchService(service.watchService));
    }

    /* @since 3.0.2 */
    private void closeWatchService(WatchService service) {
        try {
            service.close();
        }
        catch (IOException ex) {
            LOGGER.error("Close watchService occurs error", ex);
        }
    }

    /* @since 3.0.2 */
    private void peculiarFileWatchConsumer(Consumer<FileWatchService> consumer) {
        if (pathToServiceMap != null) {
            for (FileWatchService service : pathToServiceMap.values()) {
                consumer.accept(service);
            }
        }
    }


    /**
     * @return The {@code Boolean} flag that indicates whether the file listener has been started.
     * @since 3.0.2
     */
    public boolean isStarted() {
        return isStarted.get();
    }

    @Override
    public void close() {
        stop();
    }

    @Override
    public String toString() {
        return "File " + registeredPaths + " watch-thread";
    }
}
