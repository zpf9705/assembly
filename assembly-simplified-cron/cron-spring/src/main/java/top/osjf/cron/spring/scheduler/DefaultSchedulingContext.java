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

import java.util.List;

/**
 * Default impl of {@link SchedulingContext}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class DefaultSchedulingContext implements SchedulingContext {

    private final String id;

    private final Runnable rawRunnable;

    private final List<SchedulingListener> rawSchedulingListeners;

    public DefaultSchedulingContext(String id,
                                    Runnable rawRunnable,
                                    List<SchedulingListener> rawSchedulingListeners) {
        this.id = id;
        this.rawRunnable = rawRunnable;
        this.rawSchedulingListeners = rawSchedulingListeners;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Runnable getRawRunnable() {
        return rawRunnable;
    }

    @Override
    public List<SchedulingListener> getRawListeners() {
        return rawSchedulingListeners;
    }
}
