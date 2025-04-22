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

/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.osjf.sdk.core.spi;

import java.lang.annotation.*;

/**
 * NOTE: This file has been copied and slightly modified from {com.alibaba.csp.sentinel.spi}.
 * <p>
 * Annotation for Provider class of SPI.
 *
 * @see SpiLoader
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Spi {

    /**
     * Alias name of Provider class
     *
     * @return the alias name of the Provider class, which can be used to
     * reference the provider in configuration or code.

     */
    String value() default "";

    /**
     * Whether create singleton instance
     *
     * @return {@code true} if the provider should be created as a singleton
     * instance, false otherwise.
     */
    boolean isSingleton() default true;

    /**
     * Whether is the default Provider
     *
     * @return {@code true} if this provider is the default provider, false otherwise.

     */
    boolean isDefault() default false;

    /**
     * Order priority of Provider class
     *
     * @return the order priority of the Provider class, which can be used to determine the
     * precedence of providers when multiple are available.
     */
    int order() default 0;

    /**
     * Useful constant for the highest precedence value.
     * @see java.lang.Integer#MIN_VALUE
     */
    int ORDER_HIGHEST = Integer.MIN_VALUE;

    /**
     * Useful constant for the lowest precedence value.
     * @see java.lang.Integer#MAX_VALUE
     */
    int ORDER_LOWEST = Integer.MAX_VALUE;
}
