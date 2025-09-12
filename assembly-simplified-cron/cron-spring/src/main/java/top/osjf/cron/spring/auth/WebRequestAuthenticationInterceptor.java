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
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.core.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Specify the request interceptor {@link WebRequestInterceptor} instance for the relevant operations
 * exposed in HTTP interface format in the cron framework.
 *
 * <p>Implements Spring MVC web request interception for authentication verification
 * on specific request paths.
 * <h3>Main features:</h3>
 * <ul>
 * <li>Enable/disable authentication via configuration {@link #enableAuthentication}</li>
 * <li>Register path patterns requiring authentication {@link #addInterceptors}</li>
 * <li>Verify authentication token in request headers {@link #postHandle(WebRequest, ModelMap)}</li>
 * <li>Support custom regex authentication strategies via AuthenticationPredicate {@link RegexPathMatcher}</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class WebRequestAuthenticationInterceptor implements AuthenticationInterceptor, WebMvcConfigurer {

    /**
     * Authentication header name
     * Clients must include this header with a valid token for authentication
     */
    public static final String AUTHENTICATION_WEB_HEADER_NAME = "spring-cron-web-request-authentication";

    /**
     * Resource access requires a collection of registered and authenticated URLs.
     */
    private final Set<String> authenticationPaths = new LinkedHashSet<>(16);

    /**
     * Authentication enable flag
     * Read from configuration property: {@code spring.schedule.cron.web-request-authentication.enable}
     */
    private final boolean enableAuthentication;

    /**
     * A collection of identity validators provided by developers themselves.
     */
    private List<AuthenticationPredicate> authenticationPredicates;

    /**
     * The default authentication device.
     */
    private AuthenticationPredicate defaultAuthenticationPredicate;

    /**
     * Constructs a {@link WebRequestAuthenticationInterceptor} with given args.
     * @param provider    the {@link ObjectProvider} of {@link AuthenticationPredicate}.
     * @param environment the {@link Environment} instance.
     */
    public WebRequestAuthenticationInterceptor(ObjectProvider<AuthenticationPredicate> provider,
                                               Environment environment) {
        enableAuthentication
                = environment.getProperty("spring.schedule.cron.web-request-authentication.enable", boolean.class,
                false);
        if (enableAuthentication) {
            List<AuthenticationPredicate> authenticationPredicates = provider.orderedStream().collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(authenticationPredicates)) {
                this.authenticationPredicates = authenticationPredicates;
            } else {
                this.defaultAuthenticationPredicate = new EnvironmentPropertyAuthenticationPredicate(environment);
            }
        }
    }

    /**
     * Register a URL path that requires resource access authentication.
     * @param authenticationPath a specify authentication path.
     */
    public void registerAuthenticationPath(String authenticationPath) {
        Assert.hasText(authenticationPath, "authenticationPath");
        authenticationPaths.add(authenticationPath);
    }

    private String asAuthenticationPattern() {
        return ".*(" + String.join("|", authenticationPaths) +")$";
    }

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        if (enableAuthentication) {
            registry.addWebRequestInterceptor(this)
                    .addPathPatterns(asAuthenticationPattern())
                    .pathMatcher(new RegexPathMatcher());
        }
    }

    @Override
    public void preHandle(@NotNull WebRequest request) throws AuthenticationException {
        String token = request.getHeader(AUTHENTICATION_WEB_HEADER_NAME);
        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("Missing header information for access verification: "
                    + AUTHENTICATION_WEB_HEADER_NAME);
        }
        boolean authenticationFlag;
        if (CollectionUtils.isNotEmpty(authenticationPredicates)) {
            authenticationFlag = authenticationPredicates.stream().allMatch(p -> p.test(token));
        } else {
            authenticationFlag = defaultAuthenticationPredicate.test(token);
        }
        if (!authenticationFlag) {
            throw new AuthenticationException("Identity dynamic verification failed, unable to access.");
        }
    }

    /**
     * Implementation class of {@link PathMatcher} based on regular expression validation.
     */
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

    /**
     * The resource access requires {@link WebRequestAuthenticationInterceptor} to provide
     * an interface for registering and authenticating the URL collection.
     */
    public interface AuthenticationProvider extends Supplier<List<String>> {
        /**
         * Return the collection of URLs that require {@link WebRequestAuthenticationInterceptor}
         * registration and authentication for resource access.
         * @return Resource access requires a collection of registered and authenticated URLs.
         */
        @Override
        @NotNull
        List<String> get();
    }
}
