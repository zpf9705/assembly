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


package top.osjf.cron.datasource.driven.scheduled.serialization;

import top.osjf.cron.core.lang.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Default properties-based serializer for {@link ConfigurableTaskElement}.
 *
 * This implementation serializes a list of {@link ConfigurableTaskElement} into standard Java
 * properties format and deserializes it back. It processes non-static fields declared in the
 * superclass of {@link ConfigurableTaskElement}, preserving their declaration order.
 *
 * <h3>Supported Configuration Example:</h3>
 * <pre>
 * elements[0].id=task-001
 * elements[0].taskId=
 * elements[0].taskName=health-check-task
 * elements[0].profiles=dev
 * elements[0].taskDescription=Performs periodic health checks on services
 * elements[0].status=ACTIVE
 * elements[0].statusDescription=Currently running without issues
 * elements[0].expression=0 0/5 * * * ?
 * elements[0].updateSign=0
 *
 * elements[1].id=task-002
 * elements[1].taskId=
 * elements[1].taskName=log-purge-task
 * elements[1].profiles=prod
 * elements[1].taskDescription=Cleans up logs older than 7 days
 * elements[1].status=INACTIVE
 * elements[1].statusDescription=Paused until next maintenance window
 * elements[1].expression=0/1 * * * * ?
 * elements[1].updateSign=0
 * </pre>
 *
 * <h3>Features:</h3>
 * <ul>
 *   <li>Supports sparse indices (e.g., [0], [2]) with automatic gap filling</li>
 *   <li>Skips empty lines and comments (lines starting with #)</li>
 *   <li>Preserves field declaration order using LinkedHashMap</li>
 *   <li>Handles basic type conversion (String, int/Integer)</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultPropertiesConfigTaskElementSerializer extends NullableResolvedConfigTaskElementSerializer {

    /** Prefix used to identify each element in the configuration. */
    private static final String PREFIX = "elements";

    /**
     * A map of non-static fields from the parent class of {@link ConfigurableTaskElement},
     * ordered by declaration sequence. Field accessibility is pre-enabled.
     */
    private static final Map<String, Field> FIELD_MAP
            = Arrays.stream(ConfigurableTaskElement.class.getSuperclass().getDeclaredFields())
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .peek(field -> field.setAccessible(true))
            .collect(Collectors.toMap(Field::getName, Function.identity(), (f1, f2) -> f1, LinkedHashMap::new));

    /**
     * Regular expression to parse each line:
     * Pattern: elements[index].fieldName=value
     */
    private static final Pattern LINE_PATTERN = Pattern.compile("^" + PREFIX + "\\[(\\d+)\\]\\.(\\w+)=(.*)$");

    @Override
    public String serializeInternal(@NotNull List<ConfigurableTaskElement> elements) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {
            ConfigurableTaskElement element = elements.get(i);
            for (Field field : FIELD_MAP.values()) {
                Object value;
                try {
                    value  = field.get(element);
                }
                catch (IllegalAccessException | IllegalArgumentException ex) {
                    throw new IOException(ex);
                }
                builder.append(PREFIX).append("[").append(i).append("].").append(field.getName())
                        .append("=").append(value).append("\n");
            }
        }
        return builder.toString();
    }

    @Override
    public List<ConfigurableTaskElement> deserializeInternal(@NotNull String configInfo) throws IOException {

        List<ConfigurableTaskElement> elements = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(configInfo))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                Matcher matcher = LINE_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    throw new IOException("Invalid configuration line format: \"" + line + "\". Expected pattern: " +
                            "elements[<index>].<fieldName>=<value>");
                }

                int index = Integer.parseInt(matcher.group(1));
                String fieldName = matcher.group(2);
                String value = matcher.group(3);

                while (elements.size() <= index) {
                    elements.add(new ConfigurableTaskElement());
                }

                Field field = FIELD_MAP.get(fieldName);
                if (field != null) {
                    try {
                        field.set(elements.get(index), convertValue(value, field.getType()));
                    }
                    catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw new IOException(ex);
                    }
                }
            }
        }

        return elements;

    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.PROPERTIES;
    }

    /**
     * The value conversion of field types should be compatible with {@link ConfigurableTaskElement}.
     * @param value the value of {@link Field}.
     * @param type  the type of {@link Field}.
     * @return the convert value.
     */
    private Object convertValue(String value, Class<?> type) {
        if (value == null || value.equals("null")) {
            return null;
        }
        if (type == String.class) {
            return value;
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }
        return null;
    }
}
