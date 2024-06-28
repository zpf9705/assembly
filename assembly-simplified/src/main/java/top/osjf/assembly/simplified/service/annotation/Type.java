/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.assembly.simplified.service.annotation;

/**
 * Enumeration of types selected for service context configuration loading.
 *
 * @author zpf
 * @since 2.0.6
 */
public enum Type {

    /**
     * @see top.osjf.assembly.simplified.service.context.ClassesServiceContext
     */
    CLASSES,

    /**
     * @see top.osjf.assembly.simplified.service.context.SimpleServiceContext
     */
    @Deprecated //2.2.0
    SIMPLE
}
