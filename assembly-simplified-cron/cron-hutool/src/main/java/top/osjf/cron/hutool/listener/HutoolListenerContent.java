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


package top.osjf.cron.hutool.listener;

import cn.hutool.cron.TaskExecutor;
import top.osjf.commons.lang.NotNull;
import top.osjf.cron.core.listener.AbstractListenerContext;
import top.osjf.cron.core.listener.ListenerContext;
import top.osjf.cron.core.repository.RepositoryContext;

/**
 * The listening context object of {@code Hutool}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class HutoolListenerContent extends AbstractListenerContext<TaskExecutor> implements ListenerContext {

    private final String id;

    /**
     * Creates a {@code HutoolListenerContent} by given {@code TaskExecutor}.
     * @param taskExecutor the Hutool scheduler listener obj.
     * @param repositoryContext {@inheritDoc}
     */
    public HutoolListenerContent(TaskExecutor taskExecutor, RepositoryContext repositoryContext) {
        super(taskExecutor, repositoryContext);
        this.id = String.valueOf(taskExecutor.getCronTask().getId());
    }

    @Override
    @NotNull
    public String getID() {
        return id;
    }
}
