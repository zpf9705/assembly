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


package top.osjf.cron.datasource.driven.scheduled.nacosconfig;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Nacos configuration task element serializer manager, responsible for managing and retrieving
 * serializers corresponding to different configuration formats.
 *
 * <p>This class uses the SPI (Service Provider Interface) mechanism to load all implementations of
 * {@link NacosConfigTaskElementSerializer}, and selects the highest-priority implementation for each
 * {@link ConfigFormat} based on the {@link SerializerSpi} annotation. If no external implementation
 * is provided, a built-in default serializer is used.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class NacosConfigTaskElementSerializerManager {

    /** A map that stores the best (highest priority) serializer for each configuration format. */
    private static final Map<ConfigFormat, NacosConfigTaskElementSerializer> SERIALIZER_MAP = new HashMap<>();

    static {  loadHighestPrioritySerializer(); }

    /**
     * Loads the highest-priority serializer for each configuration format.
     *
     * <p>Discovers all {@link NacosConfigTaskElementSerializer} implementations via Java SPI, sorts them
     * by the 'order' value in the {@link SerializerSpi} annotation, and selects the one with the smallest
     * order (highest priority). If multiple implementations have the same smallest order, an exception is
     * thrown to prevent ambiguity.
     *
     * <p>For formats not explicitly loaded (e.g., no third-party implementation), registers the default
     * serializer.
     */
    private static void loadHighestPrioritySerializer() {

        Map<ConfigFormat, List<NacosConfigTaskElementSerializer>> loadResultMap = new HashMap<>();

        // Traverse all serializer implementations loaded by SPI.
        for (NacosConfigTaskElementSerializer serializer : ServiceLoader.load(NacosConfigTaskElementSerializer.class)) {
            Class<? extends NacosConfigTaskElementSerializer> clazz = serializer.getClass();
            SerializerSpi spi = clazz.getAnnotation(SerializerSpi.class);
            if (spi == null) {
                System.err.println("Warning: Serializer implementation " + clazz.getName() +
                        " is missing @SerializerSpi annotation. Skipping...");
                continue;
            }
            loadResultMap.computeIfAbsent(serializer.getConfigFormat(), k -> new ArrayList<>()).add(serializer);
        }

        for (Map.Entry<ConfigFormat, List<NacosConfigTaskElementSerializer>> entry : loadResultMap.entrySet()) {
            ConfigFormat format = entry.getKey();
            List<NacosConfigTaskElementSerializer> serializers = entry.getValue();

            // Sort in ascending order by order
            serializers.sort(Comparator.comparingInt(s -> s.getClass().getAnnotation(SerializerSpi.class).order()));

            // Take the first one (with the highest priority)
            NacosConfigTaskElementSerializer highest = serializers.get(0);
            int minOrder = highest.getClass().getAnnotation(SerializerSpi.class).order();

            // Check for duplicate minOrders (conflicts)
            long countOfMinOrder = serializers.stream()
                    .map(s -> s.getClass().getAnnotation(SerializerSpi.class).order())
                    .filter(order -> order == minOrder)
                    .count();

            if (countOfMinOrder > 1) {
                throw new IllegalStateException(
                        "Multiple serializers for format [" + format + "] have the same order [" + minOrder +
                                "]. Conflicting classes: " + serializers.stream()
                                .filter(s -> s.getClass().getAnnotation(SerializerSpi.class).order() == minOrder)
                                .map(s -> s.getClass().getName())
                                .collect(Collectors.joining(", "))
                );
            }

            SERIALIZER_MAP.put(format, highest);
        }

        // Default check, add default implementation sequence for types that do not exist.
        setDefault(ConfigFormat.TEXT, DefaultTextNacosConfigTaskElementSerializer::new);
        setDefault(ConfigFormat.JSON, DefaultJSONNacosConfigTaskElementSerializer::new);
        setDefault(ConfigFormat.XML, DefaultXmlNacosConfigTaskElementSerializer::new);
        setDefault(ConfigFormat.YAML, DefaultYamlNacosConfigTaskElementSerializer::new);
        setDefault(ConfigFormat.HTML, DefaultHtmlNacosConfigTaskElementSerializer::new);
        setDefault(ConfigFormat.PROPERTIES, DefaultPropertiesNacosConfigTaskElementSerializer::new);
    }

    /**
     * Sets the default serializer for the specified configuration format if none is present.
     * @param format   the configuration format.
     * @param lazyDef  supplier for lazily creating the default serializer.
     */
    private static void setDefault(ConfigFormat format, Supplier<NacosConfigTaskElementSerializer> lazyDef) {
        if (!SERIALIZER_MAP.containsKey(format)) {
            SERIALIZER_MAP.put(format, lazyDef.get());
        }
    }

    /**
     * Serializes the list of configuration task elements using the specified configuration format.
     * @param configFormat the configuration format determining which serializer to use, must not
     *                     be {@literal null}.
     * @param elements     the list of elements to serialize, must not be {@literal null}.
     * @return the serialized string
     */
    public static String serialize(ConfigFormat configFormat, List<NacosConfigTaskElement> elements) {
        return findNacosConfigTaskElementSerializer(configFormat).serialize(elements);
    }

    /**
     * Deserializes a configuration string into a list of configuration task elements.
     * @param configFormat the configuration format determining which deserializer to use,
     *                     must not be {@literal null}
     * @param configInfo   the string content to deserialize, must not be {@literal null} or empty.
     * @return the deserialized list of configuration task elements.
     */
    public static  List<NacosConfigTaskElement> deserialize(ConfigFormat configFormat, String configInfo) {
        return findNacosConfigTaskElementSerializer(configFormat).deserialize(configInfo);
    }

    /**
     * Looks up the serializer for the given configuration format.
     * @param configFormat the configuration format, must not be {@literal null}.
     * @return the found serializer, or {@literal null} if not registered.
     */
    private static NacosConfigTaskElementSerializer findNacosConfigTaskElementSerializer(ConfigFormat configFormat) {
        if (configFormat == null) {
            throw new NullPointerException("configFormat");
        }
        return SERIALIZER_MAP.get(configFormat);
    }
}
