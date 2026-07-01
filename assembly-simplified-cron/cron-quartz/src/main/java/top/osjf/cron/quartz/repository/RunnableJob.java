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


package top.osjf.cron.quartz.repository;

import org.quartz.*;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.Assert;
import top.osjf.cron.core.listener.RunningThreadHolder;
import top.osjf.cron.core.repository.CronTaskRepository;

import static top.osjf.cron.core.micrometer.RepositoryMicrometerConstants.WRAPPER_RUNNABLE_TYPE_TAG_KEY;

/**
 * {@code RunnableJob} is an implementation class that encapsulates a
 * single {@link Runnable} execution of {@link Job}/{@link InterruptableJob}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class RunnableJob implements InterruptableJob {

    private final RunningThreadHolder runningThreadHolder = new RunningThreadHolder();

    @NotNull private final String id;
    @NotNull private final Runnable runnable;

    /**
     * Creates a {@code RunnableJob} by given {@code Runnable}.
     *
     * @param id the given task {@link JobKey#toString() id}.
     * @param runnable the given {@code Runnable}.
     */
    public RunnableJob(@NotNull String id, @NotNull Runnable runnable) {

        Assert.hasText(id, "Runnable can not be null");
        Assert.notNull(runnable, "Runnable can not be null");

        this.id = id;
        this.runnable = runnable;
    }


    @Override
    public void execute(JobExecutionContext context) {
        runningThreadHolder.addCurrentRunningThread(id);
        CronTaskRepository.LongTimedExecutor executor = longTimed(context);
        if (executor != null) executor.start();
        try {
            runnable.run();
        }
        finally {
            runningThreadHolder.removeCurrentRunningThread(id);
            if (executor != null) executor.stop();
        }
    }

    @Nullable
    private CronTaskRepository.LongTimedExecutor longTimed(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        // Verify here whether the stored resource class is its own.
        Object repositoryObj = jobDataMap.get(JobConstants.SELF_REPOSITORY);
        if (!(repositoryObj instanceof QuartzCronTaskRepository)) {
            return null;
        }
        return ((QuartzCronTaskRepository) repositoryObj).longTimed
                (String.format("Tasks whose scheduling capability is provided by the {%s} resource client",
                        ((QuartzCronTaskRepository) repositoryObj).getName()),
                        WRAPPER_RUNNABLE_TYPE_TAG_KEY, RunnableJob.class.getName());
    }

    /**
     * When multiple thread tasks are executing within a specified single task unit, executing this
     * method for the first time will {@link Thread#interrupt()} all currently executing tasks.
     * Subsequent calls to the {@link Scheduler#interrupt(JobKey)} method within the
     * {@link Scheduler#getCurrentlyExecutingJobs()} snapshot loop in the process
     * {@link org.quartz.impl.StdScheduler#interrupt(JobKey)} will be ineffective,
     * meaning it is only effective for the first time. However, this is not a problem,
     * as the ultimate goal is achieved.
     *
     * @see RunningThreadHolder#removeCurrentRunningThread
     * @see org.quartz.impl.StdScheduler#interrupt(JobKey)
     * @see Scheduler#interrupt(JobKey)
     * @see Scheduler#getCurrentlyExecutingJobs()
     */
    @Override
    public void interrupt() {
        runningThreadHolder.removeRunningThreads(id);
    }
}
