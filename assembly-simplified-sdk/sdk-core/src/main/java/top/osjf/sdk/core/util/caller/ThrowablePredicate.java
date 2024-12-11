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

package top.osjf.sdk.core.util.caller;

import java.util.function.Predicate;

/**
 * Abnormal condition judgment interface, used to determine which Throwable
 * objects meet specific conditions.
 *
 * <p>This interface is typically used in conjunction with a retry mechanism
 * to determine whether a retry should occur in the event of an exception.
 * For example, a {@code ThrowablePredicate} implementation can be defined,
 *
 * <p>This implementation only returns {@literal true} when the exception is of
 * a specific type or meets specific conditions, triggering a retry.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface ThrowablePredicate extends Predicate<Throwable> {
}
