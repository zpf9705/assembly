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

import top.osjf.cron.core.lang.NotNull;

import java.util.function.Predicate;

/**
 * Dynamic HTTP Interface Authentication Predicate (for Cron Framework Authorization)
 *
 * <p>Core interface exposed by the Cron Framework for dynamic HTTP interface authorization.
 * Enables developers to implement custom dynamic validation logic
 * when HTTP interface authorization is enabled.
 * <h3>Design Intent:</h3>
 * <ul>
 *   <li>Provides flexible dynamic validation supporting database and other dynamic storage</li>
 *   <li>Enables request-level fine-grained permission control</li>
 *   <li>Serves as security extension point for Cron Framework's HTTP interfaces</li>
 * </ul>
 * <p>
 * <h3>Typical Use Cases:</h3>
 * <ul>
 *   <li>When needing dynamic credential validation from database</li>
 *   <li>When implementing role-based dynamic authorization</li>
 *   <li>When integrating with real-time authentication services</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface AuthenticationPredicate extends Predicate<String> {

    /**
     * Authentication method.
     *
     * <p>Implements the specific dynamic validation logic.
     * Developers should implement validation according to business requirements,
     * such as:
     * <ul>
     *   <li>Database query for credential validation</li>
     *   <li>Real-time authentication service calls</li>
     *   <li>JWT-based dynamic permission validation</li>
     * </ul>
     *
     * @param token The identity credential to validate (guaranteed non-null via {@link NotNull})
     * @return {@code true} if authentication succeeds, {@code false} otherwise
     */
    @Override
    boolean test(@NotNull String token);
}
