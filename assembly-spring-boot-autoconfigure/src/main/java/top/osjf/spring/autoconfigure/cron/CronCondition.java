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


package top.osjf.spring.autoconfigure.cron;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.spring.autoconfigure.SourceClassMessageCondition;

/**
 * Base of all {@link Condition} implementations used with Spring Boot. Provides sensible
 * logging to help the user diagnose what {@link ClientType} configuration classes are loaded.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class CronCondition extends SourceClassMessageCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata,
                                            ConditionMessage.Builder message) {
        Environment environment = context.getEnvironment();
        try {
            BindResult<ClientType> specified = Binder.get(environment).bind("spring.schedule.cron.client-type",
                    ClientType.class);
            if (!specified.isBound()) {
                return ConditionOutcome.match(message.because("automatic cron client type"));
            }
            ClientType required = CronConfigurations.getType(((AnnotationMetadata) metadata)
                    .getClassName());
            if (specified.get() == required) {
                return ConditionOutcome.match(message.because(specified.get() + " cron client type"));
            }
        } catch (BindException ex) {
        }
        return ConditionOutcome.noMatch(message.because("unknown cron client type"));
    }
}
