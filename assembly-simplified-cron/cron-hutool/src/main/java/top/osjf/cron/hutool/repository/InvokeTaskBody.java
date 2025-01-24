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


package top.osjf.cron.hutool.repository;

import cn.hutool.cron.task.InvokeTask;
import top.osjf.cron.core.repository.TaskBody;

/**
 * The implementation class of interface {@link TaskBody} carries an
 * executable {@link InvokeTask} object.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class InvokeTaskBody implements TaskBody {
    /**
     * The reflection the execution task object.
     */
    private final InvokeTask invokeTask;
    /**
     * Creates a new {@code InvokeTaskBody} by given {@link InvokeTask}.
     *
     * @param invokeTask the reflection the execution task object.
     */
    public InvokeTaskBody(InvokeTask invokeTask) {
        this.invokeTask = invokeTask;
    }
    /**
     * Returns the reflection the execution task object.
     * @return the reflection the execution task object.
     */
    public InvokeTask getInvokeTask() {
        return invokeTask;
    }
}
