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


package top.osjf.sdk.core.caller;

import com.google.common.annotations.Beta;

import java.lang.annotation.*;

/**
 * Annotations from subscribers.
 *
 * <p>This annotation is used to mark method parameters, indicating
 * that the parameter is related to the subscriber, to identify which
 * parameters are used to process logic or data related to the subscriber.
 *
 * <p>In event driven or message passing systems, this annotation can
 * be used to mark the subscriber parameters that receive events or
 * messages.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Beta
public @interface Subscription {
}
