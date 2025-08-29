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

import org.springframework.core.env.Environment;
import top.osjf.spring.autoconfigure.ConditionalOnPropertyProfiles;

import java.lang.annotation.*;

/**
 * The matching result between the configuration value corresponding to
 * {@code spring.schedule.cron.datasource.driven.active-profiles.matched} and the current
 * activated environment {@link Environment#getActiveProfiles()} in this annotation determines
 * whether the configuration is loaded.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ConditionalOnPropertyProfiles(propertyName = "spring.schedule.cron.scheduled-driven.active-profiles-matched")
@Documented
public @interface EnableDatasourceScheduledProfiles {
}