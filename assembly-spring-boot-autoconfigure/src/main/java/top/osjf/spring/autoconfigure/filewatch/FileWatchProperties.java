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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * File-watch properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@ConfigurationProperties(prefix = "file-watch")
public class FileWatchProperties {

    /**
     * Enable tag configuration for dynamic file listening.
     */
    private boolean enable = true;

    /**
     * A list of path information for registering file listening services is required.
     */
    private List<FileWatch> fileWatches;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<FileWatch> getFileWatches() {
        return fileWatches;
    }

    public void setFileWatches(List<FileWatch> fileWatches) {
        this.fileWatches = fileWatches;
    }

    public static class FileWatch {

        /**
         * Listen to the file path array.
         */
        private String path = System.getProperty("user.dir");

        /**
         * This Boolean tag indicates whether there is an independent listener thread,
         * which is not independently owned by default.
         */
        private boolean peculiarWatchThread = false;

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
}
