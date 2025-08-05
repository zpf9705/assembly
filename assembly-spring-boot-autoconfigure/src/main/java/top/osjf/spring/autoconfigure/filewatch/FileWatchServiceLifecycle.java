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


package top.osjf.spring.autoconfigure.filewatch;

import org.springframework.context.Lifecycle;
import top.osjf.filewatch.FileWatchService;

/**
 * Bean for intelligent lifecycle management of {@link FileWatchService FileWatchService Thread}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class FileWatchServiceLifecycle implements Lifecycle {

    private final Thread fileWatchServiceThread;

    public FileWatchServiceLifecycle(FileWatchService fileWatchService) {
        this.fileWatchServiceThread = new Thread(fileWatchService);
    }

    @Override
    public void start() {
        fileWatchServiceThread.start();
    }

    @Override
    public void stop() {
        fileWatchServiceThread.interrupt();
    }

    @Override
    public boolean isRunning() {
        return fileWatchServiceThread.getState() != Thread.State.NEW;
    }
}
