/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.cron.spring.annotation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import top.osjf.cron.spring.MappedAnnotationAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Due to the use of {@link org.springframework.core.annotation.AliasFor} annotations
 * in {@link Cron}, but not using Spring's parsing during the registration process,
 * it is necessary to manually call {@link AnnotationUtils#getAnnotationAttributes(Annotation)}
 * for directed parsing in this class and use {@link AnnotationAttributes}'s API to simplify
 * the retrieval process.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public final class CronAnnotationAttributes extends MappedAnnotationAttributes {

    /**
     * Create a {@link MappedAnnotationAttributes} encapsulated map structure using {@link Map map}.
     *
     * @param map original source of annotation attribute <em>key-value</em> pairs
     */
    public CronAnnotationAttributes(Map<String, Object> map) {
        super(map);
    }

    /**
     * Static for Create a {@link CronAnnotationAttributes} using {@link Method cronMethod}.
     *
     * @param cronMethod {@link Cron} proxy Method.
     * @return {@link Cron} Attributes.
     */
    public static CronAnnotationAttributes of(Method cronMethod) {
        return new CronAnnotationAttributes(of(cronMethod, Cron.class));
    }

    /**
     * Returning {@link Cron#expression()}, because {@link org.springframework.core.annotation.AliasFor}
     * was used but {@link AnnotationUtils#getAnnotationAttributes(Annotation)} was
     * adopted in the static method, which is compatible with support for
     * {@link org.springframework.core.annotation.AliasFor}, even if {@link Cron#value()}
     * is assigned, {@link Cron#expression()} will still redirect to obtain the same value.
     * <p>The default value is {@link Cron#DEFAULT_CRON_EXPRESSION}.
     *
     * @return {@link Cron#expression()}.
     */
    public String getExpression() {
        String cronExpression = getString(Cron.SELECT_OF_EXPRESSION_NAME);
        if (StringUtils.isBlank(cronExpression)) cronExpression = Cron.DEFAULT_CRON_EXPRESSION;
        return cronExpression;
    }

    /**
     * @return {@link Cron#profiles()}.
     */
    public String[] getProfiles() {
        return getStringArray(Cron.SELECT_OF_PROFILES_NAME);
    }
}
