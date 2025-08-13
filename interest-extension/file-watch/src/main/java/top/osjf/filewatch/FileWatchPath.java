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

import java.io.Serializable;

/**
 * Register a listening entity class that contains necessary information
 * for registering path listening.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class FileWatchPath extends TriggerKindProvider implements Comparable<FileWatchPath>, Serializable {

    private static final long serialVersionUID = -7019939522442013076L;

    /** The file system path to monitor (absolute or relative path).*/
    private String path;

    /** Whether to create a new independent {@link FileWatchService}.*/
    private boolean peculiarWatchThread;

    @Override
    public int compareTo(FileWatchPath o) {
        return path.compareTo(o.path);
    }

    @Override
    public String toString() {
        return path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isPeculiarWatchThread() {
        return peculiarWatchThread;
    }

    public void setPeculiarWatchThread(boolean peculiarWatchThread) {
        this.peculiarWatchThread = peculiarWatchThread;
    }
}
