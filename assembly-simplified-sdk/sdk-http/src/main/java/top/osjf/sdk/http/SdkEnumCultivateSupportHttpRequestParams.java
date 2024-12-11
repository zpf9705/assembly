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

import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.SdkSupport;
import top.osjf.sdk.http.process.AbstractHttpRequestParams;
import top.osjf.sdk.http.process.AbstractHttpResponse;
import top.osjf.sdk.http.process.HttpSdkEnum;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class SdkEnumCultivateSupportHttpRequestParams<R extends AbstractHttpResponse>
        extends AbstractHttpRequestParams<R> {

    private static final long serialVersionUID = 3512402192630016740L;

    /**
     * Get {@code HttpSdkEnum} managed by {@code HttpSdkEnumManager}.
     *
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public final HttpSdkEnum matchSdkEnum() {
        return InstanceHolder.getSdkEnumManager().getAndSetHttpSdkEnum(this);
    }

    /**
     * Any instance holder.
     */
    static class InstanceHolder {

        /***
         * The manager instance of {@code HttpSdkEnum}.
         */
        private static final HttpSdkEnumManager SDK_EUM_MANAGER = SdkSupport.loadInstance(HttpSdkEnumManager.class,
                "top.osjf.sdk.http.DefaultHttpSdkEnumManager");

        /**
         * Return a {@code HttpSdkEnum} manager instance for
         * {@code HttpSdkEnumManager}.
         *
         * @return {@code HttpSdkEnum} global static manager instance.
         */
        static HttpSdkEnumManager getSdkEnumManager() {
            return SDK_EUM_MANAGER;
        }
    }
}
