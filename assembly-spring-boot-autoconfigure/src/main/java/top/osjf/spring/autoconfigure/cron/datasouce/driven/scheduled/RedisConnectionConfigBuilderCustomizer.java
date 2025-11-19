/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.spring.autoconfigure.cron.datasouce.driven.scheduled;

import top.osjf.cron.datasource.driven.scheduled.redis.config.RedisConnectionConfig;

/**
 * Callback interface that can be implemented by beans wishing to customize Redis's
 * {@link RedisConnectionConfig.Builder Builder} before it is used.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface RedisConnectionConfigBuilderCustomizer {

    /**
     * Customize the redis connection config builder.
     * @param builder the redis connection config to customize.
     */
    void customize(RedisConnectionConfig.Builder builder);
}
