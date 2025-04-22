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

package top.osjf.sdk.http;

import top.osjf.sdk.core.lang.NotNull;

import static top.osjf.sdk.http.CultivateSupportHttpRequest.InstanceHolder.getSdkEnumManager;

/**
 * Extended from {@code UrlQuerySPILoggerHttpRequest}, with its functionality
 * implementation and support as {@code CultivateSupportHttpRequest} to find
 * {@link HttpSdkEnumCultivate} annotations transform {@link HttpSdkEnum} and benefit
 * from {@link HttpSdkEnumManager} management.
 *
 * @param <R> Subclass generic type of {@code AbstractHttpResponse}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SuppressWarnings({"unchecked"})
public abstract class CultivateSupportUrlQuerySPILoggerHttpRequest<R extends AbstractHttpResponse>
        extends UrlQuerySPILoggerHttpRequest<R> {

    private static final long serialVersionUID = -453091418670430519L;

    /**
     * Get {@code HttpSdkEnum} managed by
     * {@code CultivateSupportHttpRequest#InstanceHolder#SDK_EUM_MANAGER}.
     *
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public final HttpSdkEnum matchSdkEnum() {
        return getSdkEnumManager().getAndSetHttpSdkEnum(this);
    }
}
