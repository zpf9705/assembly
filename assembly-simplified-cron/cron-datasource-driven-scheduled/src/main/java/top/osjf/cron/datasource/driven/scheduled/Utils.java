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


package top.osjf.cron.datasource.driven.scheduled;

import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.StringUtils;

/**
 * Common internal utility for datasource driven scheduler.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
abstract class Utils {

    public static <T> T getConfigSafe(@Nullable DataSourceConfigLoader loader, String configKey,
                                      Class<T> type, T defaultValue) {
        if (loader == null) {
            return defaultValue;
        }
        try {
            T configValue = loader.getConfig(configKey, type);
            // Config exists but value is null, use default
            return configValue != null ? configValue : defaultValue;
        }
        catch (Throwable ex) {
            // Any loading error, fallback to default value
            return defaultValue;
        }
    }

    /**
     * Clear task binding id, reset update mark and status description for task element.
     * @param element target task element to purge
     * @return {@code true} if any data cleared, {@code false} if task id is blank
     */
    public static boolean purgeElement(TaskElement element) {
        String taskId = element.getTaskId();
        if (StringUtils.isNotBlank(taskId)) {
            element.setTaskId("");
            element.setUpdateSign(UpdateSign.NO_UPDATE.getCode());
            element.setStatusDescription("");
            return true;
        }
        return false;
    }
}
