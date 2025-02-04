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


package top.osjf.cron.core.exception;

/**
 * Thrown to indicate that the specified operation is not supported in
 * a certain {@link top.osjf.cron.core.lifecycle.Lifecycle}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class UnsupportedLifecycleException extends UnsupportedOperationException {

    private static final long serialVersionUID = 7207060019635833868L;

    /**
     * Constructs an {@code UnsupportedLifecycleException} with the specified
     * operation name.
     *
     * @param unsupportedLifecycleOperationName the operation name that is not supported
     *                                          in a certain lifecycle.
     */
    public UnsupportedLifecycleException(String unsupportedLifecycleOperationName) {
        super(unsupportedLifecycleOperationName);
    }
}
