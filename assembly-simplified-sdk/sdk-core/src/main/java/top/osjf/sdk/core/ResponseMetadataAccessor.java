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


package top.osjf.sdk.core;

import java.lang.reflect.Type;

/**
 * Metadata access interface for response class {@link Response Response}.
 *
 * <p>The main purpose of this interface is to describe the metadata information
 * of the response class, which is used for operations on the response class after
 * the request is completed, such as object transformation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface ResponseMetadataAccessor {

    /**
     * Gets the response {@code java.lang.reflect.Type}.
     *
     * <p>This method returns the response {@code java.lang.reflect.Type} information
     * associated with the current response.The specific representation of the type
     * depends on the class (such as {@code Request}) implementing this interface.
     *
     * @return The {@code java.lang.reflect.Type} information of the response.
     */
    Type getResponseType();
}
