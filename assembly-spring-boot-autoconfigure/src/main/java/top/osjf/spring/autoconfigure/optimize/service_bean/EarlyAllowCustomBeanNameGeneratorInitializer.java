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

package top.osjf.spring.autoconfigure.optimize.service_bean;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import top.osjf.optimize.service_bean.context.ServiceContextApplicationRunListener;

/**
 * Set {@code setCustomBeanNameGeneratorForSpringApplicationContext} as early as possible
 * so that custom{@link BeanNameGenerator} can be set to{@link ConfigurableApplicationContext}
 * properly.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public class EarlyAllowCustomBeanNameGeneratorInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static boolean isPresent;

    static {
        //Determine whether a service optimization interface has been
        // introduced to determine whether optimization should be performed.
        try {
            isPresent = ClassUtils.isPresent("top.osjf.optimize.service_bean.context.ServiceContext",
                    EarlyAllowCustomBeanNameGeneratorInitializer.class.getClassLoader());
        } catch (Throwable e) {
            isPresent = false;
        }
    }

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext context) {
        if (isPresent) {
            ServiceContextApplicationRunListener.setCustomBeanNameGeneratorForSpringApplicationContext(context);
        }
    }
}
