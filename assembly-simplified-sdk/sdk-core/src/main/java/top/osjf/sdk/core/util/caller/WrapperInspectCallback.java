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


package top.osjf.sdk.core.util.caller;

import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.util.ReflectUtil;

/**
 * The {@code WrapperInspectCallback} abstract class be Used for response callbacks
 * and checking response types.
 *
 * <p>This is a generic abstract class that implements the {@code Callback} interface
 * and provides a mechanism for type-checking and processing response objects. It requires
 * subclasses to implement the {@link #getType} method to specify the specific type of the
 * response and the {@link #successInternal} method to handle responses that match the type.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class WrapperInspectCallback<R extends Response> implements Callback {

    /**
     * {@inheritDoc}
     * {@code success} callback method with expected response type
     * conversion check.
     *
     * @param response {@inheritDoc}
     * @throws ClassCastException An error is thrown when the expected
     *                            type is inconsistent with the type
     *                            provided by the callback.
     */
    @Override
    public void success(@NotNull Response response) throws ClassCastException {
        Class<R> type = getType();
        if (response.isWrapperFor(type)) {
            successInternal(response.unwrap(type));
        } else {
            throw new ClassCastException(response.getClass().getName() + " cannot be cast to " + type.getName());
        }
    }

    /**
     * Gets the expected response type.
     *
     * <p>This method should be implemented by subclasses to return
     * a {@code Class} object representing the expected response type.
     *
     * @return a {@code Class} object representing the expected response type.
     */
    @NotNull
    public Class<R> getType() {
        return ReflectUtil.getSuperGenericType(this, 0);
    }

    /**
     * This method is called to handle a response that matches the
     * expected type.
     *
     * <p>This method should be implemented by subclasses to handle
     * the response object that matches the type.
     *
     * @param response the response object that matches the type.
     */
    public abstract void successInternal(@NotNull R response);
}
