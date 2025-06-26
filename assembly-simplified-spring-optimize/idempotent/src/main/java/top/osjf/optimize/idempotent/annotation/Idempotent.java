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
 * The annotation of information enrichment required for idempotency.
 *
 * <p>This annotation is used to indicate the idempotent information of a certain idempotent
 * method, including the name, idempotent control time, reminder when idempotent validation
 * fails, and idempotent active deletion settings to ensure idempotent activity.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * The unique identifier of idempotent methods serves as the sole basis for determining
     * idempotency, which can accept fixed constants and the el expression pattern of Spring.
     *
     * @return Unique identifier for idempotent methods.
     */
    @Language("SpEL")
    String value();

    /**
     * The {@code boolean} flag that add a URI prefix to the idempotent access method for
     * webpage requests.
     * <p>The setting of this property is {@code true}. If it is not a web request access,
     * it will not be concatenated because {@link RequestContextHolder#getRequestAttributes()}
     * has no value.
     * @return {@code true} add uri prefix if web request, {@code false} otherwise.
     */
    boolean addUriPrefixIfWebRequest() default true;

    /**
     * <p>
     * The duration of idempotent control must be greater than the processing time of the business.
     * </p>
     * The value is the duration of the marking of the idempotent key. If it exceeds the marking time,
     * the idempotent key can be used again.
     *
     * @return tag duration, default is 60 seconds.
     */
    long duration() default 60;

    /**
     * Idempotent control duration unit, default is {@link TimeUnit#SECONDS}.
     *
     * @return idempotent control duration unit.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Indicate the information reminder given when idempotent parity check fails.
     *
     * @return Reminder information.
     */
    String message() default "Repeated request, please try again later";

    /**
     * The {@code boolean} flag that should the idempotent key be cleared immediately
     * after the completion of the business
     *
     * @return {@code true}: Clear Now {@code false}: Not handled.
     */
    boolean removeKeyWhenFinished() default false;

    /**
     * The {@code boolean} flag that should the key of the exponent be cleared
     * immediately when the business execution is abnormal.
     *
     * @return {@code true}: Clear Now {@code false}: Not handled.
     */
    boolean removeKeyWhenError() default false;
}
