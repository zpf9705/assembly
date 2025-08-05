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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import top.osjf.filewatch.FileWatchListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Listener for monitoring JAR file changes and triggering application startup commands.
 *
 * <p>When the target JAR file is modified/created/deleted (configurable via {@link TriggerKind}),
 * this listener executes predefined shell commands to restart the application.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class ApplicationStartupFileWatchListener implements FileWatchListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStartupFileWatchListener.class);

    private Map<String, FileWatchApplicationStartupProperties.StartupJarElement> startupJarElementMap;

    /**
     * Constructs a new {@code ApplicationStartupFileWatchListener}.
     * @param properties the specify file-watch application startup properties.
     */
    public ApplicationStartupFileWatchListener(FileWatchApplicationStartupProperties properties) {
        initStartupJarElementMap(properties);
    }

    void initStartupJarElementMap(FileWatchApplicationStartupProperties properties) {
        startupJarElementMap = properties.getElements().stream()
                .collect(Collectors.toConcurrentMap(FileWatchApplicationStartupProperties.StartupJarElement::getJarFileName,
                        Function.identity()));
    }

    /**
     * {@inheritDoc}
     * Determines if this listener supports the given watch event.
     */
    @Override
    public boolean supports(WatchEvent<Path> event) {
        String jarFileName = event.context().toString();
        FileWatchApplicationStartupProperties.StartupJarElement jarElement
                = startupJarElementMap.getOrDefault(jarFileName, null);
        return jarElement != null
                && (jarElement.getTriggerKind() == TriggerKind.ALL
                || Objects.equals(jarElement.getTriggerKind().name(), event.kind().name()));
    }

    /**
     * {@inheritDoc}
     * Handles the file watch event by executing configured startup commands.
     */
    @Override
    public void onWatchEvent(WatchEvent<Path> event) {
        FileWatchApplicationStartupProperties.StartupJarElement
                jarElement = startupJarElementMap.get(event.context().toString());
        String jarFileName = jarElement.getJarFileName();
        List<String> sortedStartupCommands = wrapWithBashShell(jarElement.getSortedStartupCommands());
        try {
            Process process = new ProcessBuilder(sortedStartupCommands).start();
            try {
                if (process.waitFor(jarElement.getTimeout(), jarElement.getUnit())) {
                    // Read error stream before checking exit value
                    String errorOutput = StreamUtils.copyToString(process.getErrorStream(), StandardCharsets.UTF_8);
                    if (process.exitValue() == 0) {
                        LOGGER.info("Successfully launched {} via commands: {}", jarFileName, sortedStartupCommands);
                    }
                    else {
                        LOGGER.info("Failed to start jar application {}, returning receipt error message {}.",
                                jarFileName, errorOutput);
                    }
                }
                else {
                    process.destroyForcibly().waitFor(); //Force termination upon timeout.
                    LOGGER.error("Command {} execution timeout", sortedStartupCommands);
                }
            }
            finally {
                process.destroy();
            }
        }
        catch (IOException ex) {
            LOGGER.error("IO error executing commands {}", sortedStartupCommands, ex);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // Restore interrupt status.
            LOGGER.error("Command execution interrupted for {}", jarFileName, ex);
        }
    }

    /**
     * Wrap the original command list in Bash shell execution format.
     * @param commands commands Original command list
     * @return The complete list of shell commands after packaging, in the
     * format of ["/bin/bash", "- c", "Command1&&Command2"]
     */
    private static List<String> wrapWithBashShell(List<String> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return commands;
        }
        List<String> shellCommands = new ArrayList<>();
        shellCommands.add("/bin/bash");
        shellCommands.add("-c");
        shellCommands.add(String.join(" && ", commands));
        return shellCommands;
    }
}
