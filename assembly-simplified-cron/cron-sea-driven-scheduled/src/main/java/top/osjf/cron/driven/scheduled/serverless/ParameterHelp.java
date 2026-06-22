/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.driven.scheduled.serverless;


import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.BeanInstantiationException;
import top.osjf.commons.util.BeanUtils;
import top.osjf.commons.util.ReflectionUtils;
import top.osjf.commons.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Parameter resolution utility class.
 * Resolves {@link Parameter} annotated fields in {@link TaskParameter} instances, automatically
 * resolves parameter names, obtains field values, converts non-String types via specified serialization
 * strategies, and finally assembles into --key=value formatted JAR startup parameters.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class ParameterHelp {

    private static final ConcurrentHashMap<Class<?>, ObjectToStringSerializationStrategy> STRATEGY_CACHE
            = new ConcurrentHashMap<>();

    public static Map<Parameter.Type, String> resolveJarStartupParameter(@Nullable TaskParameter taskParameter) {
        if (taskParameter == null) {
            return Collections.emptyMap();
        }

        List<ParameterDescription> descriptions = new ArrayList<>();
        Field[] fields = taskParameter.getClass().getDeclaredFields();

        for (Field field : fields) {
            // Only process fields with @ Parameter annotation
            if (!field.isAnnotationPresent(Parameter.class)) {
                continue;
            }

            Parameter parameter = field.getAnnotation(Parameter.class);
            // Get parameter name: Annotation name>Field name
            String paramName = StringUtils.isNotBlank(parameter.name()) ? parameter.name() : field.getName();
            // Get field values (automatically process private fields)
            Object fieldValue = getFieldValue(field, taskParameter);

            if (fieldValue == null) {
                continue;
            }

            String paramValue;
            if (fieldValue instanceof String) {
                paramValue = (String) fieldValue;
            } else {
                // Retrieve or create serialization policies
                ObjectToStringSerializationStrategy strategy = getOrCreateStrategy(field, parameter);
                paramValue = strategy.serializeToString(fieldValue);
            }

            descriptions.add(new ParameterDescription(paramName, paramValue, parameter.type()));
        }

        if (descriptions.isEmpty()) {
            return Collections.emptyMap();
        }

        return descriptions.stream().collect(Collectors.groupingBy(ParameterDescription::getType,
                Collectors.mapping(ParameterDescription::toString, Collectors.joining(" "))));
    }

    private static class ParameterDescription {

        private final String paramName;

        private final String paramValue;

        private final Parameter.Type type;

        public ParameterDescription(String paramName, String paramValue, Parameter.Type type) {
            this.paramName = paramName;
            this.paramValue = paramValue;
            this.type = type;
        }

        public Parameter.Type getType() {
            return type;
        }

        @Override
        public String toString() {
            return (type == Parameter.Type.JVM ? "-" : "--")
                    + ((paramName.startsWith("D") || type == Parameter.Type.APPLICATION) ? paramName + "=" : paramName)
                    + paramValue;
        }
    }

    private static ObjectToStringSerializationStrategy getOrCreateStrategy(Field field, Parameter parameter) {
        Class<?> fieldType = field.getType();

        return STRATEGY_CACHE.computeIfAbsent(fieldType, type -> {
            Class<? extends ObjectToStringSerializationStrategy> strategyClass = parameter.serializationStrategy();

            if (strategyClass == SimpleObjectToStringSerializationStrategy.class) {
                return SimpleObjectToStringSerializationStrategy.INSTANCE;
            }

            try {
                return BeanUtils.instantiateClass(strategyClass);
            }
            catch (BeanInstantiationException ex) {
                throw new ServerlessException(ex.getMessage(), ex);
            }
        });
    }

    @Nullable
    private static Object getFieldValue(Field f, TaskParameter taskParameter) {
        ReflectionUtils.makeAccessible(f);
        try {
            return ReflectionUtils.getField(f, taskParameter);
        }
        catch (Exception ex) {  throw new ServerlessException(ex.getMessage(), ex); }
    }
}
