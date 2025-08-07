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

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;

/**
 * Abstract base class for condition implementations that need to evaluate
 * based on the source class.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class SourceClassMessageCondition extends SpringBootCondition {
    /**
     * Retrieve the original attribute of the annotation condition tag source class and
     * hand it over to the subsequent component condition execution.
     * @param context  {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public final ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String sourceClass = "";
        if (metadata instanceof ClassMetadata) {
            sourceClass = ((ClassMetadata) metadata).getClassName();
        }
        return getMatchOutcome(context, metadata, ConditionMessage.forCondition(getClass().getName(), sourceClass));
    }

    /**
     * Determine the outcome of the match along with suitable log output.
     * @param context                            the condition context
     * @param metadata                           the annotation metadata
     * @param sourceClassConditionMessageBuilder the source class {@link ConditionMessage} builder.
     * @return the condition outcome.
     */
    public abstract ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata,
                                                     ConditionMessage.Builder sourceClassConditionMessageBuilder);
}
