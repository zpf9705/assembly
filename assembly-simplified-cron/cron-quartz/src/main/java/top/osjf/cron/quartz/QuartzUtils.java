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


package top.osjf.cron.quartz;

import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.util.Assert;
import top.osjf.cron.core.util.GsonUtils;
import top.osjf.cron.quartz.repository.QuartzCronTaskRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Coordinate Quartz's easy-to-use tool class and provide relevant
 * static tool methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class QuartzUtils {

    @Deprecated
    private static final Map<String, JobKey> JOB_KEY_MAP = new ConcurrentHashMap<>(16);

    /**
     * Returns a unique identity string formatted according to {@link JobKey}.
     *
     * @param jobKey the input resolve {@link JobKey}.
     * @return Tag {@link Job} as a unique identity string.
     * @throws NullPointerException if input {@code JobKey} is {@literal null}.
     */
    @Deprecated
    public static String getJobIdentity(JobKey jobKey) {
        String id = jobKey.getName() + "@" + jobKey.getGroup();
        JOB_KEY_MAP.putIfAbsent(id, jobKey);
        return id;
    }

    /**
     * Returns a {@link JobKey} string formatted according to input id.
     *
     * @param id the input id.
     * @return the cache {@link JobKey}.
     * @throws NullPointerException if input {@code id} is {@literal null}.
     */
    @Deprecated
    public static JobKey getJobKey(String id) {
        return JOB_KEY_MAP.getOrDefault(id, null);
    }

    /**
     * Calls {@link JobKey#toString()} as id.
     * @param jobKey the input {@link JobKey}
     * @return the id result.
     */
    public static String jobKeyAsId(@NotNull JobKey jobKey) {
        return jobKey.toString();
    }

    /**
     * Resolves input id as {@link JobKey}.
     * @param id the input id.
     * @return {@link JobKey} of resolve result.
     * @see #jobKeyAsId(JobKey)
     */
    public static JobKey resolveIdAsJobKey(@NotNull String id) {
        /**
         * {@link QuartzCronTaskRepository#doRegisterInternal(String, JobKeyWrapperdRunnable)}
         * {@link JobKey#toString() getGroup() + '.' + getName()}
         */
        String[] idArray = id.split("\\.");
        Assert.isTrue(idArray.length == 2, "Illegal ID");
        return new JobKey(idArray[1], idArray[0]);
    }

    /**
     * Return different expressions based on the type of {@link Trigger}.
     *
     * @param trigger the input resolve {@link Trigger}.
     * @return If it is {@link Trigger}, return a cron expression,
     * and the rest return JSON data.
     */
    public static String getTriggerExpression(@NotNull Trigger trigger) {
        String expression;
        if (trigger instanceof CronTriggerImpl) {
            expression = ((CronTriggerImpl) trigger).getCronExpression();
        } else {
            expression = GsonUtils.toJson(trigger);
        }
        return expression;
    }
}
