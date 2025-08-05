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


package top.osjf.spring.autoconfigure.filewatch.application.startup;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.osjf.filewatch.WaitCreateConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * File-watch application startup properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@ConfigurationProperties(prefix = "file-watch.application.startup")
public class FileWatchApplicationStartupProperties implements InitializingBean {

    /**
     * Enable global configuration for application monitoring.
     */
    private StartupJarGlobal global = new StartupJarGlobal();

    /**
     * Specify the jar name and apply multiple supports for unique configurations.
     */
    private List<StartupJarElement> elements = new ArrayList<>();

    public StartupJarGlobal getGlobal() {
        return global;
    }

    public void setGlobal(StartupJarGlobal global) {
        this.global = global;
    }

    public List<StartupJarElement> getElements() {
        return elements;
    }

    public void setElements(List<StartupJarElement> elements) {
        this.elements = elements;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notEmpty(elements, "elements not be null");
        Assert.isTrue(elements.stream().allMatch(e -> StringUtils.hasText(e.jarFileName)), "jarFileName not be blank");
        elements.forEach(e-> e.nonsDefaultByGlobal(global));
        Assert.isTrue(elements.stream().noneMatch(e -> e.sortedStartupCommands.isEmpty()), "sortedStartupCommands not be empty");
    }

    public static class StartupJarGlobal {

        /**
         * What changes occur to the file corresponding to {@link StartupJarElement#jarFileName} that
         * trigger the enumeration type of {@link #sortedStartupCommands}.
         */
        private TriggerKind triggerKind = TriggerKind.ALL;

        /**
         * Specify the collection of shell commands to be executed in the order
         * of starting when monitoring changes in jar files.
         */
        private List<String> sortedStartupCommands = new ArrayList<>();

        /**
         * Specify the timeout duration for execution.
         */
        private Long timeout = 60L;

        /**
         * Enumeration of timeout duration units for specified execution.
         */
        private TimeUnit unit = TimeUnit.SECONDS;

        /**
         * The file creation completion detection configuration.
         */
        @NestedConfigurationProperty
        private WaitCreateConfiguration configuration = new WaitCreateConfiguration();

        public TriggerKind getTriggerKind() {
            return triggerKind;
        }

        public void setTriggerKind(TriggerKind triggerKind) {
            this.triggerKind = triggerKind;
        }

        public List<String> getSortedStartupCommands() {
            return sortedStartupCommands;
        }

        public void setSortedStartupCommands(List<String> sortedStartupCommands) {
            this.sortedStartupCommands = sortedStartupCommands;
        }

        public Long getTimeout() {
            return timeout;
        }

        public void setTimeout(Long timeout) {
            this.timeout = timeout;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }

        public WaitCreateConfiguration getConfiguration() {
            return configuration;
        }

        public void setConfiguration(WaitCreateConfiguration configuration) {
            this.configuration = configuration;
        }
    }

    public static class StartupJarElement {

        /**
         * The jar file name that the listener needs to listen to changes in.
         */
        private String jarFileName = "application.jar";

        /**
         * What changes occur to the file corresponding to {@link StartupJarElement#jarFileName} that
         * trigger the enumeration type of {@link #sortedStartupCommands}.
         */
        private TriggerKind triggerKind;

        /**
         * Specify the collection of shell commands to be executed in the order
         * of starting when monitoring changes in jar files.
         */
        private List<String> sortedStartupCommands;

        /**
         * Specify the timeout duration for execution.
         */
        private Long timeout;

        /**
         * Enumeration of timeout duration units for specified execution.
         */
        private TimeUnit unit;

        /**
         * The file creation completion detection configuration.
         */
        @NestedConfigurationProperty
        private WaitCreateConfiguration configuration;

        public String getJarFileName() {
            return jarFileName;
        }

        public void setJarFileName(String jarFileName) {
            this.jarFileName = jarFileName;
        }

        public TriggerKind getTriggerKind() {
            return triggerKind;
        }

        public void setTriggerKind(TriggerKind triggerKind) {
            this.triggerKind = triggerKind;
        }

        public List<String> getSortedStartupCommands() {
            return sortedStartupCommands;
        }

        public void setSortedStartupCommands(List<String> sortedStartupCommands) {
            this.sortedStartupCommands = sortedStartupCommands;
        }

        public Long getTimeout() {
            return timeout;
        }

        public void setTimeout(Long timeout) {
            this.timeout = timeout;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }

        public WaitCreateConfiguration getConfiguration() {
            return configuration;
        }

        public void setConfiguration(WaitCreateConfiguration configuration) {
            this.configuration = configuration;
        }

        public StartupJarElement nonsDefaultByGlobal(StartupJarGlobal global) {
            if (triggerKind == null) triggerKind = global.triggerKind;
            if (sortedStartupCommands == null) sortedStartupCommands = global.sortedStartupCommands;
            if (timeout == null) timeout = global.timeout;
            if (unit == null) unit = global.unit;
            if (configuration == null) configuration = global.configuration;
            return this;
        }
    }
}
