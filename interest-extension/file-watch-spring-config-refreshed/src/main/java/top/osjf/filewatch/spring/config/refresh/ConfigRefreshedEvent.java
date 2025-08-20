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


package top.osjf.filewatch.spring.config.refresh;

import org.springframework.context.ApplicationEvent;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * The {@code DynamicsYamlConfigLoadingEvent} that configure the dynamic callback
 * of the refreshed field after refreshing, and perform some business operations
 * here.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class ConfigRefreshedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 9104976355076441852L;

    /** The mapping map of the refreshed bean configuration and its annotation
     * {@link org.springframework.beans.factory.annotation.Value} for the refreshed field. */
    private final Map<Object, Set<Field>> beanValueConfigFieldMap;

    /** The content refreshed this time comes from the configuration file path.*/
    private final String refreshConfigFilePath;

    /**
     * Constructs a {@link ConfigRefreshedEvent} in source object with
     * given {@link #beanValueConfigFieldMap}.
     * @param source the object on which the event initially occurred or with
     * which the event is associated (never {@code null})
     * @param beanValueConfigFieldMap {@link #beanValueConfigFieldMap}
     *                                    suggest setting up a thread safe map.
     * @param refreshConfigFilePath Configure the source file path for refreshing.
     */
    public ConfigRefreshedEvent(Object source, Map<Object, Set<Field>> beanValueConfigFieldMap,
                                String refreshConfigFilePath) {
        super(source);
        this.beanValueConfigFieldMap = beanValueConfigFieldMap;
        this.refreshConfigFilePath = refreshConfigFilePath;
    }

    /**
     * @return The content refreshed this time comes from the configuration file path.
     */
    public String getRefreshConfigFilePath() {
        return refreshConfigFilePath;
    }

    /**
     * Check if the input bean is the configuration bean instance that was refreshed this time.
     * <p>It is recommended to use {@link org.springframework.context.event.EventListener}
     * together to detect notifications.
     * <pre>
     * {@code
     *     public class Example {
     *         EventListener(condition = "event.isConfigRefreshedBean(#example)")
     *         public void onApplicationEvent(ConfigRefreshedEvent event) {
     *         }
     *     }}
     * </pre>
     * @param bean the input bean to check.
     * @return {@code true} judge the input bean is the configuration bean instance,{@code false}
     *          otherwise.
     */
    public boolean isConfigRefreshedBean(Object bean) {
        if (beanValueConfigFieldMap.isEmpty()) {
            return false;
        }
        return beanValueConfigFieldMap.containsKey(bean);
    }

    /**
     * Return the set of fields in the input bean that have been refreshed and configured, and
     * use them together with {@link #isConfigRefreshedBean(Object)}.
     * @param bean the input bean to refer.
     * @return Set of fields with refreshed values.
     */
    public Set<Field> getConfigRefreshedFields(Object bean) {
        return beanValueConfigFieldMap.getOrDefault(bean, Collections.emptySet());
    }
}
