/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.driven.scheduled.serverless.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.util.Assert;
import top.osjf.commons.util.StreamUtils;
import top.osjf.commons.util.StringUtils;
import top.osjf.cron.datasource.driven.scheduled.ResolvedRunnablePostProcessor;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.cron.driven.scheduled.serverless.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * {@code LocalServerlessResolvedUnablePostProcessor} is used to convert processable tasks
 * {@link Runnable} into {@link LocalServerlessRunnable} computed by local functions.
 *
 * <p>This {@link ResolvedRunnablePostProcessor} implementation class can customize a unique
 * id {@link TaskElement#getId()} (non {@link TaskElement#getTaskId()}) based on the task,
 * add jar packages for function startup {@link #taskFunctionJarFileMapping}, and set timeout
 * control settings for JVM process execution {@link #taskProcessTimeoutMapping}. Of course,
 * it is not possible to set a unified function jar package, which can be used as the default
 * when no special function jar package is added, and also includes global default timeout control.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class LocalServerlessResolvedRunnablePostProcessor
        extends AbstractTaskParameterRegistry implements ResolvedRunnablePostProcessor {


    /** Mapping of task unique custom ID {@link TaskElement#getId()} and the specific function jar package.*/
    private final ConcurrentHashMap<String, File> taskFunctionJarFileMapping = new ConcurrentHashMap<>();

    /** Mapping of task unique custom ID {@link TaskElement#getId()} and process timeout control. */
    private final ConcurrentHashMap<String, Timeout> taskProcessTimeoutMapping = new ConcurrentHashMap<>();

    /** The default function jar package can be used as the default when not set.*/
    private File defaultFunctionJarFile;

    /** The default process control time (30 minutes) can be used as the default when not set.*/
    private Timeout defaultProcessTimeout = new Timeout(30, TimeUnit.MINUTES);

    /**
     * Add an executable and effective local function executable jar package mapping
     * for a specific task.
     * @param taskId          the task unique custom ID.
     * @param functionJarFile the function JAR file required for task execution
     * @throws IllegalArgumentException If the task ID is empty or not a qualified
     *       function jar package.
     */
    public void addTaskFunctionJarFile(String taskId, File functionJarFile) {
        Assert.hasText(taskId, "TaskId cannot be blank");
        Assert.notNull(functionJarFile, "FunctionJarFile cannot be null");
        Assert.isTrue(functionJarFile.exists(), "Jar file does not exist: " + functionJarFile);
        Assert.isTrue(functionJarFile.isFile(), "Path is not a valid file: " + functionJarFile);
        Assert.isTrue(functionJarFile.canRead(), "Jar file cannot be read: " + functionJarFile);

        // Verify if it is a valid Jar file (suffix+file header)
        Assert.isTrue(isValidJarFile(functionJarFile),
                "Invalid JAR file, only standard JAR files are supported: " + functionJarFile.getName());

        taskFunctionJarFileMapping.put(taskId, functionJarFile);
    }

    /**
     * Verify if the file is a valid standard JAR file
     * Validation: .jar suffix + ZIP/JAR magic number 50 4B 03 04
     *
     * @param file file to validate
     * @return true=valid JAR, false=invalid file
     */
    private boolean isValidJarFile(File file) {
        if (!file.getName().toLowerCase().endsWith(".jar")) {
            return false;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[4];
            if (fis.read(header) < 4) {
                return false;
            }
            // JAR / ZIP magic number
            return header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Add a function jar package to execute a mapping of process control time for a specific task.
     * @param taskId          the task unique custom ID.
     * @param processTimeout task execution timeout object.
     * @throws IllegalArgumentException If the unique ID of the task is empty or {@link Timeout}
     *      is {@literal null}.
     */
    public void addTaskProcessTimeout(String taskId, Timeout processTimeout) {
        Assert.hasText(taskId, "TaskId cannot be blank");
        Assert.notNull(processTimeout, "ProcessTimeout cannot be null");
        taskProcessTimeoutMapping.put(taskId, processTimeout);
    }

    /**
     * Set a default {@link File function jar package}.
     * @param defaultFunctionJarFile the default function JAR file
     */
    public void setDefaultFunctionJarFile(File defaultFunctionJarFile) {
        Assert.notNull(defaultFunctionJarFile, "DefaultFunctionJarFile can not be null");
        this.defaultFunctionJarFile = defaultFunctionJarFile;
    }

    /**
     * Set a default function {@link Timeout  process timeout control}.
     * @param defaultProcessTimeout the default timeout object
     */
    public void setDefaultProcessTimeout(Timeout defaultProcessTimeout) {
        Assert.notNull(defaultProcessTimeout, "DefaultProcessTimeout can not be null");
        this.defaultProcessTimeout = defaultProcessTimeout;
    }

    /**
     * Get the required executable JAR file for the task (non-null)
     * First try task-specific JAR, then fall back to global default JAR
     *
     * @param taskId unique task identifier
     * @return valid executable JAR file
     * @throws IllegalArgumentException if no valid executable JAR file
     */
    private File getRequiredFunctionJarFile(String taskId) {
        File functionJarFile = taskFunctionJarFileMapping.getOrDefault(taskId, defaultFunctionJarFile);
        Assert.notNull(functionJarFile, "Task "+ taskId +" has no executable function jar file");
        return functionJarFile;
    }


    /**
     * Get task execution timeout.
     * Priority: task-specific config > global default
     *
     * @param taskId unique task identifier
     * @return task timeout object
     */
    private Timeout getProcessTimeout(String taskId) {
        return taskProcessTimeoutMapping.getOrDefault(taskId, defaultProcessTimeout);
    }


    /**
     * Post-process: wrap the original Runnable, inject JAR file and timeout config
     * Return a Runnable that can run directly in the local serverless environment
     *
     * @param resolvedRunnable original resolved Runnable
     * @param taskElement      task metadata
     * @return wrapped local serverless Runnable
     */
    @Override
    @NotNull
    public Runnable postProcessResolvedRunnable(@NotNull Runnable resolvedRunnable, @NotNull TaskElement taskElement) {
        String id = taskElement.getId();
        return new LocalServerlessRunnable(resolvedRunnable, taskElement,
                getRequiredFunctionJarFile(id), getProcessTimeout(id), getTaskParameter(id), this);
    }

    private static class LocalServerlessRunnable implements Runnable {

        private final Logger LOGGER = LoggerFactory.getLogger(LocalServerlessRunnable.class);

        private final Runnable resolvedRunnable;

        private final TaskElement taskElement;

        private final File functionJarFile;

        private final Timeout processTimeout;

        private final TaskParameter taskParameter;

        private final AbstractTaskParameterRegistry parameterRegistry;

        public LocalServerlessRunnable(Runnable resolvedRunnable, TaskElement taskElement, File functionJarFile,
                                       Timeout processTimeout, TaskParameter taskParameter,
                                       AbstractTaskParameterRegistry parameterRegistry) {
            this.resolvedRunnable = resolvedRunnable;
            this.taskElement = taskElement;
            this.functionJarFile = functionJarFile;
            this.processTimeout = processTimeout;
            this.taskParameter = taskParameter;
            this.parameterRegistry = parameterRegistry;
        }

        @Override
        public void run() {

            // Local parameters are prioritized for startup execution.
            TaskParameter modTaskParameter =
                    parameterRegistry.getLocalTaskParameter() != null ?
                            parameterRegistry.getLocalTaskParameter() : taskParameter;

            // After disabling the function service, directly call the local task to run.
            if (modTaskParameter instanceof DisabledLocalServerlessTaskParameter) {
                resolvedRunnable.run();
                return;
            }

            Map<Parameter.Type, String> startupParameter = ParameterHelp.resolveJarStartupParameter(modTaskParameter);

            String applicationStartupCommand = buildStartupCommand(startupParameter);

            try {
                LOGGER.info("Task function [{} - {}] && parameter [{}] start execute command [{}].",
                        taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand);

                Process process = new ProcessBuilder(wrapWithBashShell(Collections.singletonList(applicationStartupCommand)))
                        .start();
                if (process.waitFor(processTimeout.getDuration(), processTimeout.getTimeUnit())) {
                    if (process.exitValue() == 0) {
                        LOGGER.info("Task function [{} - {}] && parameter [{}] && command [{}] has been successfully executed.",
                                taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand);
                    }
                    else {
                        String errorOutput
                                = StreamUtils.copyToString(process.getErrorStream(), StandardCharsets.UTF_8);
                        LOGGER.error("Failed to execute task function [{} - {}] && parameter [{}] && command [{}], error message [{}]",
                                taskElement.getId(), taskElement.getTaskName(), taskParameter,
                                applicationStartupCommand, errorOutput);
                    }
                }
                else {
                    process.destroyForcibly().waitFor();
                    LOGGER.error("Task function [{} - {}] && parameter [{}] && command [{}] execution timeout",
                            taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand);
                }
            }
            catch (IOException ex) {
                LOGGER.error("IO error occurred during the execution of task function [{} - {}]/parameter [{}] && command [{}]",
                        taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand, ex);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt(); // Restore interrupt status.
                LOGGER.error("Interrupted occurred during the execution of task function [{} - {}]/parameter [{}] && command [{}]",
                        taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand, ex);
            }
            finally {
                // Clear local parameters...
                if (parameterRegistry.getLocalTaskParameter() != null) {
                    parameterRegistry.setLocalTaskParameter(null);
                }
            }
        }

        /**
         * Build Java - jar xxx. jar -- param=xxx command
         * @param startupParameter the parameter map.
         * @return java startup command.
         */
        private String buildStartupCommand(Map<Parameter.Type, String> startupParameter) {
            StringBuilder sb = new StringBuilder();

            String jvmParam = startupParameter.get(Parameter.Type.JVM);

            sb.append("java ");
            if (StringUtils.isNotBlank(jvmParam)) {
                sb.append(jvmParam).append(" ");
            }

            sb.append(" -jar ").append(functionJarFile.getPath()).append(" ");

            String applicationParam = startupParameter.get(Parameter.Type.APPLICATION);

            if (StringUtils.isNotBlank(applicationParam)) {
                sb.append(applicationParam);
            }

            return sb.toString();
        }

        /**
         * Wrap the original command list in Bash shell execution format.
         * @param commands commands Original command list
         * @return The complete list of shell commands after packaging, in the
         * format of ["/bin/bash", "- c", "Command1&&Command2"]
         */
        private static List<String> wrapWithBashShell(List<String> commands) {
            if (commands == null || commands.isEmpty()) {
                return commands;
            }
            List<String> shellCommands = new ArrayList<>();
            shellCommands.add("/bin/bash");
            shellCommands.add("-c");
            shellCommands.add(String.join(" && ", commands));
            return shellCommands;
        }
    }
}
