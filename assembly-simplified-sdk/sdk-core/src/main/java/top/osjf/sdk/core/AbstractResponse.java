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

/**
 * Abstract response class, defining basic properties and methods for response objects.
 * <p>This class is an abstract class that implements the `Response` interface, providing
 * a basic framework for all specific response objects.
 *
 * <p>It defines the following properties and methods:
 * <ul>
 * <li>{@link #serialVersionUID}: A unique identifier for serialization version, ensuring
 * compatibility during serialization and deserialization.</li>
 * <li>{@link #isSuccess}: Returns a boolean indicating whether the operation was successful,
 * defaulting to {@code DEFAULT_IS_SUCCESS}.</li>
 * <li>{@link #getMessage}: Returns the response message content, defaulting to `DEFAULT_MESSAGE`.</li>
 * <li>{@link #setErrorCode}: A method to set the error code, with an empty implementation.
 * Specific subclasses should override as needed.</li>
 * <li>{@link #setErrorMessage}: A method to set the error message, with an empty implementation.
 * Specific subclasses should override as needed.</li>
 * </ul>
 * <p>Subclasses should inherit from this class and override relevant methods, especially
 * `isSuccess`,`setErrorCode`, and `setErrorMessage`, to provide specific response logic.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractResponse implements Response {

    private static final long serialVersionUID = 4294123081630652115L;
    // The default success status, initialized to false
    private static final boolean DEFAULT_IS_SUCCESS = false;
    // The default message content, initialized to "UNKNOWN"
    private static final String DEFAULT_MESSAGE = "UNKNOWN";

    /**
     * {@inheritDoc}
     * <p>Returns a boolean indicating whether the operation was successful, defaulting to `false`.
     */
    @Override
    public boolean isSuccess() {
        return DEFAULT_IS_SUCCESS;
    }

    /**
     * {@inheritDoc}
     * <p>Returns the response message content, defaulting to `"UNKNOWN"`.
     */
    @Override
    public String getMessage() {
        return DEFAULT_MESSAGE;
    }

    @Override
    public void setErrorCode(Object code) {
    }

    @Override
    public void setErrorMessage(String message) {
    }
}
