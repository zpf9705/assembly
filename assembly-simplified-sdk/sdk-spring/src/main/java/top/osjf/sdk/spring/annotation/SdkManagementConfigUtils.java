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


package top.osjf.sdk.spring.annotation;

/**
 * Configuration constants for internal.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class SdkManagementConfigUtils {

    /**
     * The name of the internal bean {@code SpringRequestCaller}.
     */
    public static final String INTERNAL_SPRING_REQUEST_CALLER_BEAN_NAME
            = "top.osjf.sdk.spring.internalSpringRequestCaller";

    /**
     * the attribute name of {@code SpringRequestCaller} from the
     * {@code SdkProxyFactoryBean} class.
     */
    public static final String REQUEST_CALLER_FIELD_NAME = "requestCaller";
}
