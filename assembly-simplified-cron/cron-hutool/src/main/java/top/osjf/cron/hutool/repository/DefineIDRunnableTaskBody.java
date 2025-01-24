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

import top.osjf.cron.core.repository.RunnableTaskBody;
import top.osjf.cron.core.repository.TaskBody;

/**
 * The implementation class of interface {@link TaskBody} carries an
 * executable {@link Runnable} object and a user defined ID.
 *
 * <p>Support the scenario where developers can customize task
 * IDs during scheduled task registration in hutool.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class DefineIDRunnableTaskBody extends RunnableTaskBody {
    /**
     * User defined ID.
     */
    private final String id;
    /**
     * Creates a new {@code DefineIDRunnableTaskBody} by given {@link Runnable}.
     *
     * @param id       the user defined ID.
     * @param runnable the executable {@code Runnable}.
     */
    public DefineIDRunnableTaskBody(Runnable runnable, String id) {
        super(runnable);
        this.id = id;
    }
    /**
     * Returns a user defined ID.
     * @return user defined ID.
     */
    public String getId() {
        return id;
    }
}
