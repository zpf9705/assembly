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


package top.osjf.spring.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;

import java.util.Arrays;
import java.util.Map;

/**
 * A custom Spring Boot condition that checks if a specific property is set and
 * if the application is running with a specific profile. This condition is used
 * to determine if a configuration should be applied based on the presence of a
 * property and the active profile.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class OnPropertyProfilesConditional extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String sourceClass = "";
        if (metadata instanceof ClassMetadata) {
            sourceClass = ((ClassMetadata) metadata).getClassName();
        }
        Environment environment = context.getEnvironment();
        ConditionMessage.Builder message = ConditionMessage.forCondition(OnPropertyProfilesConditional.class.getName()
                , sourceClass);
        Map<String, Object> attr
                = metadata.getAnnotationAttributes(ConditionalOnPropertyProfiles.class.getCanonicalName());
        if (attr != null) {
            String propertyName = (String) attr.get("propertyName");
            BindResult<String[]> specified = Binder.get(environment).bind(propertyName, String[].class);
            if (!specified.isBound()) {
                return ConditionOutcome.match(message.because("The property '" + propertyName + "' was not" +
                        " found in the configuration."));
            }
            final String reasonTemplate = "required profile '" + Arrays.toString(specified.get()) + "' %s active";
            String reason;
            if (environment.acceptsProfiles(Profiles.of(specified.get()))) {
                reason = String.format(reasonTemplate, "is");
            }
            else {
                reason = String.format(reasonTemplate, "is not");
            }
            return ConditionOutcome.match(message.because(reason));
        }
        return ConditionOutcome.noMatch(message.because("Annotation attributes [top.osjf.spring.autoconfigure" +
                ".ConditionalOnPropertyProfiles] does not exist"));
    }
}
