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

import top.osjf.cron.core.listener.IdCronListener;

/**
 * The abstract service class of the listener for {@code Scheduling} includes the
 * stage method passing that specifies the task ID.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public abstract class AbstractSchedulingListener implements SchedulingListener, IdCronListener<String> {

    @Override
    public void onStart(SchedulingInfo value) {
        onStartWithId(value.getId());
    }

    @Override
    public void onSucceeded(SchedulingInfo value) {
        onSucceededWithId(value.getId());
    }

    @Override
    public void onFailed(SchedulingInfo value, Throwable exception) {
        onFailedWithId(value.getId(), exception);
    }

    @Override
    public void onStartWithId(String s) {

    }

    @Override
    public void onSucceededWithId(String s) {

    }

    @Override
    public void onFailedWithId(String s, Throwable exception) {

    }
}
