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

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Service collection configuration enables annotations.
 *
 * <p>Developed based on Spring, it is mainly aimed at implementing the injection collection
 * problem of multiple implementation classes for a single interface.
 *
 * <p>This annotation does not have relevant parameter attributes.
 *
 * <p>For the scanning analysis of {@link ServiceCollection} wearable classes, the package
 * path where the startup class is located is used, and path parameters are not provided
 * for manual writing. Before use, your corresponding class needs to be placed under the
 * package where the startup class is located.
 *
 * @author zpf
 * @since 2.0.4
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ServiceContextConfiguration.class)
public @interface EnableServiceCollection {
}
