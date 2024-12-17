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

import top.osjf.sdk.core.support.SdkSupport;

/**
 * The default extension {@link ResponseData} is to check
 * whether the response is successful when the conditions
 * are met.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface InspectionResponseData extends ResponseData {

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean inspectionResponseResult() {
        return true;
    }

    /**
     * Check the placeholder data returned when the request fails,
     * instead of {@literal null}, to enhance the applicability
     * of SDK calls.
     *
     * <p>Analyze an example to see {@link SdkSupport#getResponse}.
     *
     * @return Placeholder returns data, which may be a global default
     * value,can be {@literal null}, adapted according to oneself.
     */
    Object failedSeatData();
}
