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


package top.osjf.optimize.idempotent.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to enable idempotent support and load the necessary
 * configurations for {@code IdempotentConfiguration} idempotent.
 *
 * <p>The method of {@link Idempotent} annotation identification will be the
 * primary support object after enabling idempotent support. Based on the
 * relevant content of the annotation, idempotent control can be given
 * around {@link top.osjf.optimize.idempotent.aspectj.IdempotentMethodAspect}.
 * Developers only need to define an idempotent unique key to easily implement
 * idempotent schemes, and can release idempotent control at the appropriate
 * time according to customization.
 *
 * <p>This annotation is used to determine whether to enable idempotent support,
 * and whether to remove it will enable idempotent plug-in support.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(IdempotentConfiguration.class)
public @interface EnableIdempotent {
}
