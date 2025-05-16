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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.ui.ModelMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.spring.CronTaskInfoReadableWebMvcHandlerController;
import top.osjf.cron.spring.datasource.driven.scheduled.SpringHandlerMappingMybatisPlusDatasourceDrivenScheduled;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class WebRequestAuthenticationInterceptor implements WebRequestInterceptor, WebMvcConfigurer {

    public static final String AUTHENTICATION_WEB_HEADER_NAME = "spring-cron-web-request-authentication";

    public static final String AUTHENTICATION_PATTERN = ".*("
            + CronTaskInfoReadableWebMvcHandlerController.REQUEST_MAPPING_PATH_OF_GET_CRON_TASK_LIST
            + "|"
            + SpringHandlerMappingMybatisPlusDatasourceDrivenScheduled.RUNNING_MAPPING_PATH
            + ")$";

    private final boolean enableAuthentication;

    private List<AuthenticationPredicate> predicates;

    private AuthenticationPredicate defaultAuthenticationPredicate;

    public WebRequestAuthenticationInterceptor(ObjectProvider<AuthenticationPredicate> provider, Environment environment) {
        enableAuthentication
                = environment.getProperty("spring.schedule.cron.web.request.authentication.enable", boolean.class,
                false);
        if (enableAuthentication) {
            List<AuthenticationPredicate> predicates = provider.orderedStream().collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(predicates)) {
                this.predicates = predicates;
            } else {
                this.defaultAuthenticationPredicate = new EnvironmentPropertyAuthenticationPredicate(environment);
            }
        }
    }

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        if (enableAuthentication) {
            registry.addWebRequestInterceptor(this)
                    .addPathPatterns(AUTHENTICATION_PATTERN)
                    .pathMatcher(new RegexPathMatcher());
        }
    }

    @Override
    public void preHandle(@NotNull WebRequest request) {
        String token = request.getHeader(AUTHENTICATION_WEB_HEADER_NAME);
        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("Missing header information for access verification: "
                    + AUTHENTICATION_WEB_HEADER_NAME);
        }
        boolean authenticationFlag;
        if (CollectionUtils.isNotEmpty(predicates)) {
            authenticationFlag = predicates.stream().allMatch(p -> p.test(token));
        } else {
            authenticationFlag = defaultAuthenticationPredicate.test(token);
        }
        if (!authenticationFlag) {
            throw new AuthenticationException("Identity dynamic verification failed, unable to access.");
        }
    }

    @Override
    public void postHandle(@NotNull WebRequest request, @Nullable ModelMap model) {
    }

    @Override
    public void afterCompletion(@NotNull WebRequest request, @Nullable Exception ex) {
    }

    private static class RegexPathMatcher implements PathMatcher {
        @Override
        public boolean isPattern(@NotNull String path) {
            return false;
        }

        @Override
        public boolean match(@NotNull String pattern, String path) {
            return path.matches(pattern);
        }

        @Override
        public boolean matchStart(@NotNull String pattern, @NotNull String path) {
            throw new UnsupportedOperationException();
        }

        @Override
        @NotNull
        public String extractPathWithinPattern(@NotNull String pattern, @NotNull String path) {
            throw new UnsupportedOperationException();
        }

        @Override
        @NotNull
        public Map<String, String> extractUriTemplateVariables(@NotNull String pattern, @NotNull String path) {
            throw new UnsupportedOperationException();
        }

        @Override
        @NotNull
        public Comparator<String> getPatternComparator(@NotNull String path) {
            throw new UnsupportedOperationException();
        }

        @Override
        @NotNull
        public String combine(@NotNull String pattern1, @NotNull String pattern2) {
            throw new UnsupportedOperationException();
        }
    }
}
