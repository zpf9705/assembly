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

package top.osjf.sdk.http.annotation.resolver;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;

/**
 * A {@code Resolver} abstract implementation class for handling
 * {@code JavacProcessingEnvironment}.
 *
 * <p>The methods related to rewriting {@code Resolver} in this class
 * all involve conversion processing and related processing requirements
 * for {@code JavacProcessingEnvironment}.
 *
 * <p>Subclasses require rewriting requirements from this class and
 * can inherit related information.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class JavacProcessingEnvironmentResolver implements Resolver {

    /**
     * {@inheritDoc}.
     * Confirm whether the {@link ResolverMetadata#getProcessingEnvironment()}
     * parameter is {@code JavacProcessingEnvironment} or its subclass.
     *
     * @param initResolverMetadata {@inheritDoc}.
     * @return {@inheritDoc}.
     */
    @Override
    public boolean test(ResolverMetadata initResolverMetadata) {
        return initResolverMetadata.getProcessingEnvironment() instanceof JavacProcessingEnvironment;
    }
}
