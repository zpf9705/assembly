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


package top.osjf.commons.ability;

/**
 * Simple implementation of {@link Nameable}, using the fully qualified class name of the current
 * instance as the unique name identifier.
 *
 * <p>Advantages: stateless, no external dependencies, stable and unchanged during application runtime,
 * which can accurately distinguish different component implementation classes in logs, exceptions
 * and monitoring indicators. It is the most commonly used implementation scheme.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class SimpleClassNameable implements Nameable {

    @Override
    public String getName() {
        return getClass().getName();
    }
}
