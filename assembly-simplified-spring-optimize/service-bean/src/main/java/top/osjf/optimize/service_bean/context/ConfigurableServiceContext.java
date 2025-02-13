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


package top.osjf.optimize.service_bean.context;

import java.io.Closeable;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2025.02.13
 */
public interface ConfigurableServiceContext extends ServiceContext, Closeable {

    /**
     * Define a constant to represent a custom scope name {@code service} for a
     * Bean in the Spring framework.
     *
     * <p>This constant means that it represents a custom scope named {@code service}
     * , and in Spring configuration, the behavior of this scope can be defined through
     * a specific class {@link ServiceScope}.
     *
     * @see org.springframework.beans.factory.config.Scope
     * @see org.springframework.context.annotation.AnnotationScopeMetadataResolver
     */
    String SUPPORT_SCOPE = "service";
}
