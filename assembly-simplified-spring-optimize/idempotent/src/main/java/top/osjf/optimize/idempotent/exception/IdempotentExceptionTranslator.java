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


package top.osjf.optimize.idempotent.exception;

/**
 * Strategy interface to translate {@link IdempotentException} into another {@link Throwable}.
 * It is typically used to unify exception handling or adapt framework-required exception formats
 * in the exception handling chain.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@FunctionalInterface
public interface IdempotentExceptionTranslator {

    /**
     * Translate the specified {@code IdempotentException} exception into a specific exception
     * type instance object according to requirements, and throw the return exception type of
     * the method when subsequent idempotent parity check fails.
     * @param ex the {@code IdempotentException} to be translated, typically thrown
     *           by business logic.
     * @return The translated Throwable, which can be a {@code RuntimeException},
     * {@code Throwable}, or custom exception type.
     */
    Throwable translate(IdempotentException ex);
}
