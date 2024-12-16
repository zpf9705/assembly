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

package top.osjf.sdk.http.process;

import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.SdkSupport;

/**
 * It is an abstract extension of {@code AbstractHttpRequestParams}, dedicated
 * to simplifying the direct rewriting of {@link HttpRequest#matchSdkEnum()}
 * methods instead of using annotations {@link HttpSdkEnumCultivate}.
 *
 * <p>This abstract class initializes static {@link HttpSdkEnumManager}, i.e.
 * {@link HttpSdkEnum} management, to retrieve and cache {@code HttpSdkEnum}.
 *
 * <p>The implementation extension of this abstract class is limited by Java's
 * single inheritance, so the pass method of the {@link InstanceHolder#getSdkEnumManager()}
 * method's static call is overridden by the subclass {@link HttpRequest#matchSdkEnum()}
 * and called to implement the functionality extension of this abstract class.
 *
 * @param <R> Subclass generic type of {@code AbstractHttpResponse}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class CultivateSupportHttpRequestParams<R extends AbstractHttpResponse>
        extends AbstractHttpRequestParams<R> {

    private static final long serialVersionUID = 3512402192630016740L;

    /**
     * Get {@code HttpSdkEnum} managed by {@code HttpSdkEnumManager}.
     *
     * @return {@inheritDoc}
     * @throws IllegalStateException as see {@link HttpSdkEnumManager#getAndSetHttpSdkEnum}
     */
    @Override
    @NotNull
    public final HttpSdkEnum matchSdkEnum() throws IllegalStateException {
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
                "top.osjf.sdk.http.process.DefaultHttpSdkEnumManager");

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
