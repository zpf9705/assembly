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
 * The basic function is equivalent to {@link EnableServiceCollection}.
 *
 * <p>On top of this, it supports the selection of configuration types
 * {@link #type()}.
 *
 * <p>The two types provide different service collection modes and different
 * service acquisition methods. The two methods have different concepts in
 * terms of project startup time consumption.
 *
 * <p>Compared to {@link EnableServiceCollection}'s configuration, this configuration
 * takes up slightly more startup time, but is more convenient to use.
 *
 * @author zpf
 * @since 2.0.6
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ServiceContextSelectorConfiguration.class})
public @interface EnableServiceCollection2 {

    /**
     * Select the configuration you need.
     * @return Refer to {@link Type}.
     * @deprecated {@link Type#SIMPLE} There is a problem with bean loading
     * delay, temporarily suspending use.
     */
    @Deprecated
    Type type() default Type.CLASSES;
}
