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


package top.osjf.cron.core.repository;

import top.osjf.cron.core.exception.UnsupportedLifecycleException;

/**
 * Thrown to indicate that the specified operation is not supported in
 * a certain {@link LifecycleRepository#reStart()}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ReStartedUnsupportedException extends UnsupportedLifecycleException {
    private static final long serialVersionUID = -3623734616720535175L;

    /**
     * Constructs an {@code ReStartedUnsupportedException} with name {@code reStart}.
     */
    public ReStartedUnsupportedException() {
        super("reStart");
    }
}
