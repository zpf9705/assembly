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

package top.osjf.sdk.core.caller;

/**
 * When the request response indicates unsuccessful, an error is thrown.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class SdkResponseNonSuccessException extends SdkCallerException {

    private static final long serialVersionUID = -8031736713390106538L;

    /**
     * Creates a {@code SdkResponseNonSuccessException} without args.
     */
    public SdkResponseNonSuccessException() {
        super();
    }

    /**
     * Creates a {@code SdkResponseNonSuccessException} by given message.
     *
     * @param s the detail error message.
     */
    public SdkResponseNonSuccessException(String s) {
        super(s);
    }
}
