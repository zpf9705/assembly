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


package top.osjf.sdk.spring.annotation;

import com.alibaba.qlexpress4.InitOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.osjf.sdk.core.caller.CallOptions;
import top.osjf.sdk.spring.SpringRequestCaller;
import top.osjf.sdk.spring.runner.SdkExpressRunner;

/**
 * {@code SpringRequestCaller} configuration class, used to define
 * a {@code SpringRequestCaller} to be managed by the Spring container.
 * <p>
 * In version 1.0.3, a new bean {@link SdkExpressRunner} was added to
 * add template instructions for SDK execution.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@Configuration(proxyBeanMethods = false)
public class SpringCallerConfiguration {

    /**
     * Create a {@code SpringRequestCaller} bean for sdk proxy bean
     * to support resolve {@link CallOptions} annotation.
     *
     * @return a singleton for {@code SpringRequestCaller}.
     */
    @Bean(SdkManagementConfigUtils.INTERNAL_SPRING_REQUEST_CALLER_BEAN_NAME)
    public SpringRequestCaller requestCaller() {
        return new SpringRequestCaller();
    }

    /**
     * @since 1.0.3
     */
    @Bean(SdkManagementConfigUtils.INTERNAL_SDK_EXPRESS_RUNNER_BEAN_NAME)
    public SdkExpressRunner sdkExpress4Runner(ObjectProvider<InitOptions> provider) {
        InitOptions initOptions = provider.orderedStream().findFirst().orElse(null);
        if (initOptions == null) {
            initOptions = InitOptions.DEFAULT_OPTIONS;
        }
        return new SdkExpressRunner(initOptions);
    }
}
