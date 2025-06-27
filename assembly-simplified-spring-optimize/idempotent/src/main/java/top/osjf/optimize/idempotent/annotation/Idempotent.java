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

import org.intellij.lang.annotations.Language;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * NOTE: This file has been copied and slightly modified from {com.healthy-chn.cloud}.
 * <p>
 * <strong>idempotent control annotation</strong>
 * <p>Used to mark methods that require idempotent control, preventing duplicate
 * submissions or processing of interfaces through unique identification and validity
 * mechanisms.
 * <p>Suitable for key business scenarios such as payment, order creation, and inventory
 * deduction, ensuring that the operation is only executed once.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * Unique Identifier for Idempotent Method.
     * <p>
     * The sole basis for idempotency verification. Supports two modes:
     * <ul>
     *     <li>Fixed constant: Directly specify a string (e.g., "ORDER_CREATE")</li>
     *     <li>SpEL expression: Dynamically generate via Spring Expression Language
     *     (e.g., "#request.userId + '_' + #params.orderId")</li>
     * </ul>
     * <p>
     * Recommended to use SpEL expressions with request parameters to avoid conflicts
     * across different requests.
     * </p>
     * <p>If the expression is {@literal null}, then the input parameters of the
     * idempotent method will be converted into JSON concatenation as the control
     * idempotent key for this access.
     *
     * @return Unique identifier string for idempotent method.
     */
    @Language("SpEL") String value() default "";

    /**
     * Add URI Prefix for Web Requests.
     * <p>
     * When set to {@code true}, if the current request is a web request (determined via
     * {@link RequestContextHolder#getRequestAttributes()}), the generated idempotent key
     * will be prefixed with the current request URI, e.g.:
     * <pre>
     * Original key: "USER_123" → Actual key: "/api/user/USER_123"
     * </pre>
     * <p>
     * Suitable for scenarios requiring idempotency isolation by request path (e.g., different
     * endpoints use distinct keys).
     * </p>
     *
     * @return {@code true} to add URI prefix for web requests, {@code false} otherwise
     */
    boolean addUriPrefixIfWebRequest() default true;

    /**
     * Idempotent Key TTL.
     * <p>
     * The duration for which the idempotent key remains valid. Unit is specified by {@link #timeUnit()}.
     * If the business processing time exceeds this duration, the key can be reused.
     * </p>
     * <p>
     * Default is 60 seconds. Adjust based on business requirements (e.g., longer TTL for payment scenarios).
     * </p>
     *
     * @return TTL duration of the idempotent key (numeric value).
     */
    long duration() default 60;

    /**
     * Time Unit for TTL.
     * <p>
     * Specifies the time unit for {@link #duration()}. Default is {@link TimeUnit#SECONDS}.
     * Options include milliseconds, seconds, minutes, hours, etc.
     * </p>
     *
     * @return Time unit enum value.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Error Message for Idempotency Check Failure.
     * <p>
     * The message returned to the client when idempotency check fails.
     * Default is: "Repeated request, please try again later".
     * </p>
     * <p>
     * Customize based on business requirements (e.g., "Order already submitted, please do not repeat").
     * </p>
     *
     * @return Error message for idempotency check failure
     */
    String message() default "Repeated request, please try again later";

    /**
     * Clear Key Immediately After Business Completion.
     * <p>
     * When set to {@code true}, the idempotent key is cleared immediately after successful
     * business logic execution.
     * Suitable for scenarios requiring high real-time performance.
     * </p>
     * <p>
     * Default is {@code false}, meaning the key is not actively cleared (relies on TTL expiration).
     * </p>
     *
     * @return {@code true} to clear immediately after business completion, {@code false} otherwise
     */
    boolean removeKeyWhenFinished() default false;

    /**
     * Clear Key Immediately on Business Error.
     * <p>
     * When set to {@code true}, the idempotent key is cleared immediately if business
     * logic throws an exception.
     * Prevents key leakage due to exceptions.
     * </p>
     * <p>
     * Default is {@code false}, meaning the key is not actively cleared on error
     * (relies on TTL expiration).
     * </p>
     *
     * @return {@code true} to clear immediately on business error, {@code false} otherwise
     */
    boolean removeKeyWhenError() default false;
}
