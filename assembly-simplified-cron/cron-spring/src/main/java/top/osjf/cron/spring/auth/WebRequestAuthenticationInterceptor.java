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

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.core.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class WebRequestAuthenticationInterceptor implements WebRequestInterceptor, EnvironmentAware {

    public static final String AUTHENTICATION_WEB_HEADER_NAME = "spring-cron-web-request-authentication";

    private final List<AuthenticationPredicate> predicates;

    private AuthenticationPredicate envAuthenticationPredicate;

    private boolean enableAuthentication;

    public WebRequestAuthenticationInterceptor(List<AuthenticationPredicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public void setEnvironment(Environment environment) {
        enableAuthentication
                = environment.getProperty("spring.schedule.cron.web.request.authentication.enable", boolean.class,
                false);
        if (enableAuthentication){
            envAuthenticationPredicate = new EnvironmentAuthenticationPredicate(environment);
        }
    }

    @Override
    public void preHandle(@NotNull WebRequest request) {
        if (enableAuthentication) {
            String token = request.getHeader(AUTHENTICATION_WEB_HEADER_NAME);
            if (StringUtils.isBlank(token)) {
                throw new AuthenticationException("Missing header information for access verification: "
                        + AUTHENTICATION_WEB_HEADER_NAME);
            }
            boolean authenticationFlag;
            if (CollectionUtils.isNotEmpty(predicates)) {
                authenticationFlag = predicates.stream().allMatch(p -> p.test(token));
            } else {
                authenticationFlag = envAuthenticationPredicate.test(token);
            }
            if (!authenticationFlag) {
                throw new AuthenticationException("Identity dynamic verification failed, unable to access.");
            }
        }
    }

    @Override
    public void postHandle(@NotNull WebRequest request, @Nullable ModelMap model) {
    }

    @Override
    public void afterCompletion(@NotNull WebRequest request, @Nullable Exception ex) {
    }
}
