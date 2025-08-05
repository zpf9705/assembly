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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    /** Target JAR filename to monitor */
    private final String jarFileName;

    /** Event types that trigger command execution */
    private final TriggerKind triggerKind;

    /** Pre-wrapped shell commands for process execution */
    private final List<String> sortedStartupCommands;

    /** Command execution timeout */
    private final long timeout;

    /** Command execution timeout unit*/
    private final TimeUnit unit;

    /**
     * Constructs a new {@code ApplicationStartupFileWatchListener}.
     *
     * @param jarFileName           the jar file name to watch.
     * @param triggerKind           the trigger condition type.
     * @param sortedStartupCommands the commands to execute.
     * @param timeout               command execution timeout.
     * @param unit                  timeout unit.
     * @throws IllegalArgumentException if commands list is empty.
     */
    public ApplicationStartupFileWatchListener(String jarFileName,
                                               TriggerKind triggerKind,
                                               List<String> sortedStartupCommands,
                                               long timeout,
                                               TimeUnit unit) {
        if (CollectionUtils.isEmpty(sortedStartupCommands)) {
            throw new IllegalArgumentException("Startup commands cannot be empty");
        }
        this.jarFileName = jarFileName;
        this.triggerKind = triggerKind;
        this.sortedStartupCommands = wrapWithBashShell(sortedStartupCommands);
        this.timeout = timeout;
        this.unit = unit;
    }

    /**
     * {@inheritDoc}
     * Determines if this listener supports the given watch event.
     */
    @Override
    public boolean supports(WatchEvent<Path> event) {
        return (triggerKind == TriggerKind.ALL || Objects.equals(triggerKind.name(), event.kind().name()))
                && Objects.equals(event.context().toString(), jarFileName);
    }

    /**
     * {@inheritDoc}
     * Handles the file watch event by executing configured startup commands.
     */
    @Override
    public void onWatchEvent(WatchEvent<Path> event) {
        try {
            Process process = new ProcessBuilder(sortedStartupCommands).start();
            try {
                if (process.waitFor(timeout, unit)) {
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
