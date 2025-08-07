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


package top.osjf.spring.autoconfigure;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class AbstractOnProgramArgumentsConditional extends BeanFactoryRequiredSpringBootCondition {
    /**
     * Check if there is a bean named {@code springApplicationArguments} type
     * {@link ApplicationArguments}, and if there is no mismatch, provide a
     * reason. Otherwise, continue with subsequent matching processing.
     * @param context     {@inheritDoc}
     * @param metadata    {@inheritDoc}
     * @param builder     {@inheritDoc}
     * @param beanFactory {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public final ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata,
                                            ConditionMessage.Builder builder,
                                            ConfigurableListableBeanFactory beanFactory) {
        if (!beanFactory.containsBean("springApplicationArguments")) {
            return ConditionOutcome.noMatch(builder.because("Missing bean springApplicationArguments"));
        }
        return getMatchOutcome(context, metadata, builder, beanFactory, beanFactory.getBean("springApplicationArguments",
                ApplicationArguments.class));
    }
    /**
     * Determine the outcome of the match along with suitable log output.
     * @param context     the condition context
     * @param metadata    the annotation metadata
     * @param builder     the source class {@link ConditionMessage} builder.
     * @param beanFactory the bean of {@link ConfigurableListableBeanFactory}.
     * @param arguments   the bean of {@link ApplicationArguments}.
     * @return the condition outcome.
     */
    public abstract ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata,
                                                     ConditionMessage.Builder builder,
                                                     ConfigurableListableBeanFactory beanFactory,
                                                     ApplicationArguments arguments);
}
