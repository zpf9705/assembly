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

package top.osjf.cron.spring.hutool;

import cn.hutool.core.util.ReflectUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.env.Environment;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.spring.AbstractCronTaskRegistrant;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.CronAnnotationAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Hutool of scheduled task registration actors.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronTaskRegistrant extends AbstractCronTaskRegistrant {

    public HutoolCronTaskRegistrant(CronTaskRepository<String, Runnable> cronTaskRepository) {
        super(cronTaskRepository);
    }

    @Override
    public void register(Class<?> realBeanType, Object bean, Environment environment) throws Exception {
        Method[] methods = realBeanType.getDeclaredMethods();
        String[] activeProfiles = environment.getActiveProfiles();
        //Determine if there is a timing method.
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Cron.class)
                    || Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            CronAnnotationAttributes cronAttribute = getCronAttribute(method);
            String expression = cronAttribute.getExpression();
            Runnable rab = () -> ReflectUtil.invoke(bean, method);
            if (ArrayUtils.isEmpty(activeProfiles)) {
                //When the environment is not activated, it indicates that
                // everything is applicable and can be registered directly.
                register0(expression, rab);
            } else {
                if (profilesCheck(cronAttribute.getProfiles(), activeProfiles)) {
                    register0(expression, rab);
                }
            }
        }
    }

    void register0(String expression, Runnable runnable) throws CronExpressionInvalidException {
        CronTaskRepository<String, Runnable> cronTaskRepository = getCronTaskRepository();
        cronTaskRepository.register(expression, runnable);
    }
}
