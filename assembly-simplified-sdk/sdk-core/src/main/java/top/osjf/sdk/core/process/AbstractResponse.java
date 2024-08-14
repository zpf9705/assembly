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

/**
 * <p>The abstract implementation of {@link Response} mainly focuses on
 * default implementation of some rules and methods of {@link Response}.</p>
 *
 * <p>The default implementation is to convert the format when {@link DefaultErrorResponse}
 * users encounter exceptions.</p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractResponse implements Response {

    private static final long serialVersionUID = 4294123081630652115L;

    private static final boolean DEFAULT_IS_SUCCESS = false;

    private static final String DEFAULT_MESSAGE = "UNKNOWN";

    @Override
    public boolean isSuccess() {
        return DEFAULT_IS_SUCCESS;
    }

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
