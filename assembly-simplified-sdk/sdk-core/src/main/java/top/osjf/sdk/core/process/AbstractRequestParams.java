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

package top.osjf.sdk.core.process;

import top.osjf.sdk.core.exception.SdkException;

import java.util.Collections;
import java.util.Map;

/**
 * <p>The abstract implementation of {@link Request} mainly focuses on
 * default implementation of some rules and methods of {@link Request}.</p>
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractRequestParams<R extends AbstractResponse> implements Request<R>

{

    private static final long serialVersionUID = 6875912567896987011L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getHeadMap() {
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() throws SdkException {
    }
}
