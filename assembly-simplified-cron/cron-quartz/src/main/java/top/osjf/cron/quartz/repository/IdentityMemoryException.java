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


package top.osjf.cron.quartz.repository;

import org.quartz.SchedulerException;

/**
 * Throwing this exception should prove that there is an error or duplication
 * in the correspondence between ID and {@link org.quartz.JobKey} in {@link IdentityMemory}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class IdentityMemoryException extends SchedulerException {

    private static final long serialVersionUID = -4512621133690131308L;

    public IdentityMemoryException(String msg) {
        super(msg);
    }

    public IdentityMemoryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
