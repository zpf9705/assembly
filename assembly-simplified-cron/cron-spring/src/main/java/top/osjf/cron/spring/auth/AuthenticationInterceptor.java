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


package top.osjf.cron.spring.auth;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import top.osjf.cron.core.lang.NotNull;

import javax.annotation.Nullable;

/**
 * Authentication interceptor interface, used to execute authentication logic at different
 * stages of web request processing.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface AuthenticationInterceptor extends WebRequestInterceptor {

    @Override
    default void preHandle(@NotNull WebRequest request) throws AuthenticationException {
    }

    @Override
    default void postHandle(@NotNull WebRequest request, @Nullable ModelMap model) throws AuthenticationException {
    }

    @Override
    default void afterCompletion(@NotNull WebRequest request, @Nullable Exception ex) throws AuthenticationException {
    }
}
