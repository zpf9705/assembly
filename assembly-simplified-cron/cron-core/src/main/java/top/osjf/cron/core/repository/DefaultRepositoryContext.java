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


package top.osjf.cron.core.repository;

import top.osjf.commons.util.Assert;

/**
 * The default implementation class of interface {@link RepositoryContext}.
 * <p>
 * By default, given an {@link CronTaskRepository}, all refined resources rely
 * on the default implementation of {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultRepositoryContext implements RepositoryContext {

    private final CronTaskRepository cronTaskRepository;

    public DefaultRepositoryContext(CronTaskRepository cronTaskRepository) {
        Assert.notNull(cronTaskRepository, "cronTaskRepository must not be null");
        this.cronTaskRepository = cronTaskRepository;
    }

    @Override
    public CronTaskRepository getRepository() {
        return cronTaskRepository;
    }
}
