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

package top.osjf.optimize.service_bean.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.optimize.service_bean.context.DefaultServiceContext;
import top.osjf.optimize.service_bean.context.ServiceContext;
import top.osjf.optimize.service_bean.context.ServiceContextAwareBeanPostProcessor;

/**
 * The related configuration classes of {@code ServiceContext}.
 *
 * <p>This configuration class mainly defines three beans:
 * <ul>
 * <li>Defined the default implementation class {@link DefaultServiceContext}
 * for {@code ServiceContext}.</li>
 * <li>Defined the {@link org.springframework.beans.factory.Aware} extension
 * for {@code ServiceContext}.
 * <p>Regarding the definition of {@link ServiceContextAwareBeanPostProcessor},
 * it is a bean processor that works internally and is not related to the end user,
 * so {@link BeanDefinition#ROLE_INFRASTRUCTURE} is set.
 * </li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@Configuration(proxyBeanMethods = false)
public class ServiceContextConfiguration {

    @Bean
    public ServiceContext serviceContext() {
        return new DefaultServiceContext();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ServiceContextAwareBeanPostProcessor serviceContextAwareBeanPostProcessor() {
        return new ServiceContextAwareBeanPostProcessor();
    }
}
