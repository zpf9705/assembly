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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.osjf.sdk.core.caller.CallOptions;
import top.osjf.sdk.core.caller.RequestCaller;
import top.osjf.sdk.core.util.ReflectUtil;
import top.osjf.sdk.spring.SpringRequestCaller;
import top.osjf.sdk.spring.proxy.SdkProxyFactoryBean;

import java.lang.reflect.Field;

/**
 * {@code SpringRequestCaller} configuration class, used to define
 * a {@code SpringRequestCaller} to be managed by the Spring container.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@Configuration(proxyBeanMethods = false)
public class SpringCallerConfiguration {

    /**
     * The name of the internal bean {@code SpringRequestCaller}.
     */
    protected static final String INTERNAL_SPRING_REQUEST_CALLER
            = "top.osjf.sdk.spring.SpringRequestCaller.internal";

    /**
     * the attribute name of {@code SpringRequestCaller} from the
     * {@code AbstractSdkProxyBean} class.
     */
    protected static String SPRING_REQUEST_CALLER_FIELD_NAME;

    /*
     * Find the attribute name of SpringRequestCaller from the AbstractSdkProxyBean class.
     * */
    static {
        for (Field declaredField : ReflectUtil.getAllDeclaredFields(SdkProxyFactoryBean.class)) {
            if (RequestCaller.class.isAssignableFrom(declaredField.getType())) {
                SPRING_REQUEST_CALLER_FIELD_NAME = declaredField.getName();
                break;
            }
        }
    }

    /**
     * Create a {@code SpringRequestCaller} bean for sdk proxy bean
     * to support resolve {@link CallOptions} annotation.
     *
     * @return a singleton for {@code SpringRequestCaller}.
     */
    @Bean(INTERNAL_SPRING_REQUEST_CALLER)
    public SpringRequestCaller requestCaller() {
        return new SpringRequestCaller();
    }
}
