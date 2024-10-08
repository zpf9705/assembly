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

package top.osjf.cron.spring;

/**
 * Timed task registration actor interface, dividing the registration
 * of different components.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface CronTaskRealRegistrant {

    /**
     * Returns whether the currently passed {@link Registrant} is a
     * type that supports registration.
     *
     * @param registrant The true type of bean.
     * @return whether the currently {@link Registrant} support register.
     */
    boolean supports(Registrant registrant);

    /**
     * Register scheduled tasks based on the bean's conditions.
     *
     * @param registrant The true type of bean.
     * @throws Exception Possible abnormal issues that may arise from
     *                   registration behavior.
     */
    void register(Registrant registrant) throws Exception;
}
