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


package top.osjf.cron.core.repository;

/**
 * Thrown to indicate that the {@link TaskBody} type is not supported.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class UnsupportedTaskBodyException extends UnsupportedOperationException {

    private static final long serialVersionUID = 8161867261983060710L;

    private final Class<? extends TaskBody> bodyClass;

    /**
     * Constructs an {@code UnsupportedOperationException} with the specified
     * {@link TaskBody} type.
     *
     * @param bodyClass the specified {@link TaskBody} type.
     */
    public UnsupportedTaskBodyException(Class<? extends TaskBody> bodyClass) {
        super("Unsupported parsing TaskBody type " + bodyClass);
        this.bodyClass = bodyClass;
    }

    /**
     * Return a {@link TaskBody} type of unsupported.
     * @return a {@link TaskBody} type of unsupported.
     */
    public Class<? extends TaskBody> getBodyClass() {
        return bodyClass;
    }
}
