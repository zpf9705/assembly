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


package top.osjf.cron.spring.auth;

import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * Environment-based Authentication Predicate
 * <p>
 * Implements authentication verification by comparing the provided token
 * with the value configured in the Spring environment properties.
 *
 * <p>
 * This predicate checks against the property:
 * {@code spring.schedule.cron.web.request.authentication.token}
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 * @see AuthenticationPredicate
 */
public class EnvironmentPropertyAuthenticationPredicate implements AuthenticationPredicate {

    private final Environment environment;

    public EnvironmentPropertyAuthenticationPredicate(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean test(String token) {
        return Objects.equals(environment.getProperty("spring.schedule.cron.web.request.authentication.token"), token);
    }
}
