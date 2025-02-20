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
     * The attribute name of {@code SpringRequestCaller} from the
     * {@code SdkProxyFactoryBean} class.
     */
    public static final String REQUEST_CALLER_FIELD_NAME = "requestCaller";

    /**
     * Default regular expression for domain name validation.
     * <p>
     * This pattern is used to validate whether a domain name conforms to the following rules:
     * <ul>
     * <li>The domain name cannot start or end with a hyphen (-).</li>
     * <li>The main part of the domain name (i.e., the part before the dot (.)) consists of 1 to 63 characters,
     * which can be English letters (both uppercase and lowercase), digits, or hyphens.</li>
     * <li>The top-level domain (i.e., the part after the last dot) consists of 2 or more English letters.</li>
     * </ul>
     */
    public static final String DEFAULT_DOMAIN_PATTERN
            = "^(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.[A-Za-z]{2,}$";

    /**
     * Default regular expression for IP address validation.
     * <p>
     * This pattern is used to validate whether an IPv4 address conforms to the following default rules:
     * <ul>
     * <li>The IP address consists of four octets, each with a value between 0 and 255.</li>
     * <li>The octets are separated by dots (.).</li>
     * </ul>
     */
    public static final String DEFAULT_IP_PATTERN =
            "((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}";
}
