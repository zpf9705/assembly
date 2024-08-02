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

package top.osjf.cron.spring.scheduler;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.osjf.cron.core.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Task execution running body, including callbacks implemented for
 * each stage of the execution cycle.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class SchedulingRunnable implements Runnable, SchedulingInfoCapable {

    private final SchedulingInfo info;

    private final Runnable runnable;

    private final List<SchedulingListener> schedulingListeners = new ArrayList<>();

    public SchedulingRunnable(@NonNull String id, @NonNull Runnable runnable,
                              @Nullable List<SchedulingListener> springCronListeners) {
        this.runnable = runnable;
        this.info = new DefaultSchedulingInfo(id, runnable);
        if (CollectionUtils.isNotEmpty(springCronListeners)) {
            this.schedulingListeners.addAll(springCronListeners);
        }
    }

    @Override
    public void run() {
        schedulingListeners.forEach(c -> c.onStart(info));
        try {
            runnable.run();
            schedulingListeners.forEach(c -> c.onSucceeded(info));
        } catch (Throwable e) {
            schedulingListeners.forEach(c -> c.onFailed(info, e));
        }
    }

    @Override
    public SchedulingInfo getSchedulingInfo() {
        return info;
    }
}
