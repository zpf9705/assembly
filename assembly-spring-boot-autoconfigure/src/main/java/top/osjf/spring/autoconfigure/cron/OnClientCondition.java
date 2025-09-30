/*
 * Copyright 2025-? the original author or authors.
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
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import top.osjf.cron.core.util.ArrayUtils;
import top.osjf.spring.autoconfigure.SourceClassMessageCondition;

import java.util.Arrays;

/**
 * {@link Condition} evaluation class that determines whether a bean or component should be
 * created based on the configured {@code spring.schedule.cron.client-type} in the environment.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
class OnClientCondition extends SourceClassMessageCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata,
                                            ConditionMessage.Builder message) {

        Environment environment = context.getEnvironment();
        try {
            BindResult<ClientType> specified = Binder.get(environment).bind("spring.schedule.cron.client-type",
                    ClientType.class);
            if (specified.isBound()) {
                AnnotationAttributes att = AnnotationAttributes
                        .fromMap(metadata.getAnnotationAttributes(ConditionalOnClient.class.getCanonicalName()));
                if (att != null) {
                    ClientType[] clientTypes = (ClientType[]) att.get("value");
                    if (!ArrayUtils.isEmpty(clientTypes)) {
                        ClientType clientType = specified.get();
                        if (Arrays.binarySearch(clientTypes, clientType) >= 0) {
                            return ConditionOutcome
                                    .match(message.because(clientType + " cron client type matched"));
                        }
                    }
                }
            }
        }
        catch (BindException ex) {
        }
        return ConditionOutcome.noMatch(message.because("unknown or non matched cron client type"));
    }
}
