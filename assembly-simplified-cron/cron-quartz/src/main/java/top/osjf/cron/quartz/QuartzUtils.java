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

import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;
import top.osjf.commons.lang.NotNull;
import top.osjf.cron.core.util.GsonUtils;

/**
 * Coordinate Quartz's easy-to-use tool class and provide relevant
 * static tool methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class QuartzUtils {

    /**
     * Calls {@link JobKey#toString()} as id.
     * @param jobKey the input {@link JobKey}
     * @return the id result.
     */
    public static String jobKeyAsId(@NotNull JobKey jobKey) {
        return jobKey.toString();
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
