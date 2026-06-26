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


package top.osjf.cron.core.exception;

import top.osjf.cron.core.repository.DisallowConcurrentExecution;

/**
 * Thrown when attempting to dynamically revoke the concurrency restriction declared via
 * {@link DisallowConcurrentExecution} annotation.
 *
 * <p>The concurrency constraint defined by annotation belongs to static declarative configuration,
 * which is determined at the coding stage and immutable during runtime. Such constraints cannot
 * be cancelled or modified by programmatic calls to guarantee the stability of task execution rules.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class AnnotationConstraintCannotBeCancelledException extends CronFrameworkException {

    private static final long serialVersionUID = 2526570052771350989L;
    /**
     * Constructs a new {@code AnnotationConstraintCannotBeCancelledException} with {@code null} as its detail message.
     */
    public AnnotationConstraintCannotBeCancelledException() {
        super("The concurrency constraint configured by @DisallowConcurrentExecution annotation is a static " +
                "declaration rule and cannot be dynamically cancelled at runtime.");
    }
}
