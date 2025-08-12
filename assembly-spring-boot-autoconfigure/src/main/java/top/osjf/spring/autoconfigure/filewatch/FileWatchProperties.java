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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.osjf.filewatch.FileWatchPath;
import top.osjf.filewatch.application.startup.StartupJarElement;

import java.util.ArrayList;
import java.util.List;

/**
 * File-watch properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@ConfigurationProperties(prefix = "file-watch")
public class FileWatchProperties implements InitializingBean {

    /**
     * Enable tag configuration for dynamic file listening.
     * <p>
     * This feature is an additional service and is not enabled by
     * default. If necessary, automatic configuration needs to be
     * manually enabled.
     */
    private boolean enable = false;

    /**
     * A list of path information for registering file listening services is required.
     */
    private List<FileWatchPath> fileWatchPaths = new ArrayList<>();

    /**
     * Configuration items for application startup.
     */
    private ApplicationStartup applicationStartup = new ApplicationStartup();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<FileWatchPath> getFileWatchPaths() {
        return fileWatchPaths;
    }

    public void setFileWatchPaths(List<FileWatchPath> fileWatchPaths) {
        this.fileWatchPaths = fileWatchPaths;
    }

    public ApplicationStartup getApplicationStartup() {
        return applicationStartup;
    }

    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    @Override
    public void afterPropertiesSet() {
        if (!enable) return;
        Assert.notEmpty(fileWatchPaths, "fileWatchPaths not empty");
        Assert.isTrue(fileWatchPaths.stream().allMatch(f -> StringUtils.hasText(f.getPath())),
                "path not be null");
        applicationStartup.afterPropertiesSet();
    }

    /**
     * Application self-starting configuration class.
     */
    public static class ApplicationStartup implements InitializingBean {

        /**
         * Register the self-starting jar package information configuration collection.
         */
        private List<StartupJarElement> elements = new ArrayList<>();

        @Override
        public void afterPropertiesSet() {
            Assert.isTrue(elements.isEmpty() ||
                    elements.stream().allMatch(startupJarElement -> startupJarElement.getJarFileName() != null
                    && !startupJarElement.getSortedStartupCommands().isEmpty()),
                    "Missing jarFileName or sortedStartupCommands");
        }

        public List<StartupJarElement> getElements() {
            return elements;
        }

        public void setElements(List<StartupJarElement> elements) {
            this.elements = elements;
        }
    }
}
