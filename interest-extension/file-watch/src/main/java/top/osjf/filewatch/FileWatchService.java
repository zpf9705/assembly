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

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@SuppressWarnings("unchecked")
public class FileWatchService implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatchService.class);

    private final String[] paths;

    private WatchService watchService;

    private final List<FileWatchListener> listeners = new ArrayList<>();

    public FileWatchService(String[] paths) {
        this.paths = requireNonNull(paths, "paths = null");
    }

    public void register() {
        if (watchService == null) {
            try {
                watchService = FileSystems.getDefault().newWatchService();
            } catch (IOException ex) {
                throw new FileWatchException("Failed to create java.nio.file.WatchService", ex);
            }
        }

        for (String path : paths) {
            Path obtainPath;
            try {
                obtainPath = Paths.get(path);
                obtainPath.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.OVERFLOW,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
            }
            catch (InvalidPathException ex) {
                throw new FileWatchException("Invalid path " + path, ex);
            }
            catch (IOException ex) {
                throw new FileWatchException("Failed to register WatchService", ex);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            }
            catch (InterruptedException ex) {
                throw new FileWatchException("File watch service thread is already occupied.", ex);
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                for (FileWatchListener listener : listeners) {
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    if (listener.supports(pathEvent)) {
                        listener.onWatchEvent(pathEvent);
                    }
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}
