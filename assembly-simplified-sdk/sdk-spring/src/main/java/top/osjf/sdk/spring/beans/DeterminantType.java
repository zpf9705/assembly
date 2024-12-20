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


package top.osjf.sdk.spring.beans;

/**
 * Method to obtain the type of proxy service required.
 *
 * <p>In practical applications, this interface can be used
 * to dynamically identify and process different types of
 * proxy services at runtime,such as automatically wiring
 * corresponding beans based on the type of proxy service
 * in a dependency injection framework,or selecting the
 * correct processing logic based on type in a proxy
 * service invocation chain.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface DeterminantType {

    /**
     * Method to obtain the type of proxy service required.
     *
     * @return Returns a Class object representing the type
     * of the proxy service.
     */
    Class<?> getType();
}
