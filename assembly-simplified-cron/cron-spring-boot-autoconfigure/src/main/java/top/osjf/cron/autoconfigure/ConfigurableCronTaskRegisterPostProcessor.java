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

package top.osjf.cron.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;
import top.osjf.cron.core.util.ArrayUtils;
import top.osjf.cron.spring.CronTaskRegisterPostProcessor;
import top.osjf.cron.spring.RegistrantCollector;

import java.lang.reflect.Method;

/**
 * Configurable {@link CronTaskRegisterPostProcessor} for more flexible assembly expansion.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ConfigurableCronTaskRegisterPostProcessor extends CronTaskRegisterPostProcessor {

    private final CronProperties cronProperties;

    private final CronProperties.ClientType clientType;

    public ConfigurableCronTaskRegisterPostProcessor(CronProperties cronProperties,
                                                     CronProperties.ClientType clientType) {
        this.cronProperties = cronProperties;
        this.clientType = clientType;
    }

    @Override
    protected boolean advanceApprovalOfCondition(Class<?> realBeanType) {
        return super.advanceApprovalOfCondition(realBeanType)
                || advanceApprovalOfCondition0(realBeanType);
    }

    @Override
    protected void finishRegistration() {
        addMetadata(cronProperties.withClientToMetadata(clientType));
        super.finishRegistration();
    }

    /**
     * By determining that its target method is to create a bean and return a {@link RegistrantCollector} type.
     *
     * @param realBeanType bean real type.
     * @return If {@code true} skips registration and returns registration detection.
     */
    private boolean advanceApprovalOfCondition0(Class<?> realBeanType) {
        Method[] declaredMethods = ReflectionUtils.getDeclaredMethods(realBeanType);
        if (!ArrayUtils.isEmpty(declaredMethods)) {
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.isAnnotationPresent(Bean.class) &&
                        super.advanceApprovalOfCondition(declaredMethod.getReturnType())) {
                    return true;
                }
            }
        }
        return false;
    }
}
