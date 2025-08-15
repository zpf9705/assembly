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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.osjf.filewatch.FileWatchPath;
import top.osjf.filewatch.application.startup.StartupJarElement;
import top.osjf.spring.autoconfigure.filewatch.dynamics.yml.config.ConfigLoadingCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File-watch properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@ConfigurationProperties(prefix = "file-watch")
public class FileWatchProperties implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(FileWatchProperties.class);

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

    /**
     * Configuration items for dynamics loading yaml config.
     */
    public DynamicsYamlLoading dynamicsYamlLoading = new DynamicsYamlLoading();

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

    public DynamicsYamlLoading getDynamicsYamlLoading() {
        return dynamicsYamlLoading;
    }

    public void setDynamicsYamlLoading(DynamicsYamlLoading dynamicsYamlLoading) {
        this.dynamicsYamlLoading = dynamicsYamlLoading;
    }

    @Override
    public void afterPropertiesSet() {
        if (!enable) return;

        // Verification of fileWatchPaths.
        Assert.notEmpty(fileWatchPaths, "File watch paths can not be empty");
        Assert.isTrue(fileWatchPaths.stream().allMatch(f -> StringUtils.hasText(f.getPath())),
                "All elements must has path");

        // Verification of jar startup elements.
        List<StartupJarElement> elements = applicationStartup.getElements();
        Assert.isTrue(elements.isEmpty() ||
                        elements.stream().allMatch(startupJarElement -> startupJarElement.getJarFileName() != null
                                && !startupJarElement.getSortedStartupCommands().isEmpty()),
                "Missing jar file name or sorted startup commands");

        List<String> paths = fileWatchPaths.stream().map(FileWatchPath::getPath).collect(Collectors.toList());
        for (StartupJarElement element : elements) {
            if (!paths.contains(element.getBindPath())) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Binding path {} configured by jar name {} is not in the registered listening" +
                            " path list, and will lose the runtime auto start function. It is recommended to " +
                            "check and restart!", element.getBindPath(), element.getJarFileName());
                }
            }
        }
    }

    /**
     * Application self-starting configuration class.
     */
    public static class ApplicationStartup {

        /**
         * Register the self-starting jar package information configuration collection.
         */
        private List<StartupJarElement> elements = new ArrayList<>();

        public List<StartupJarElement> getElements() {
            return elements;
        }

        public void setElements(List<StartupJarElement> elements) {
            this.elements = elements;
        }
    }

    /**
     * Dynamics yaml config loading configuration class.
     */
    public static class DynamicsYamlLoading {

        /**
         * List of dynamics yaml config condition.
         */
        private List<ConfigLoadingCondition> loadingConditions = new ArrayList<>();

        public List<ConfigLoadingCondition> getLoadingConditions() {
            return loadingConditions;
        }

        public void setLoadingConditions(List<ConfigLoadingCondition> loadingConditions) {
            this.loadingConditions = loadingConditions;
        }
    }
}
