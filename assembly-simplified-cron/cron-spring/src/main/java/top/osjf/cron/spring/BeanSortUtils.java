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


package top.osjf.cron.spring;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import top.osjf.cron.core.lang.NotNull;

import java.util.List;

/**
 * Bean sorting tool, involving sorting elements related to the Spring framework.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class BeanSortUtils {
/**
* Retrieve the bean with the highest priority.
* @param beans bean list.
* @param <T>   bean type.
* @return the highest priority bean.
*/
public static <T> T getPriorityBean(@NotNull List<T> beans) {
    AnnotationAwareOrderComparator.sort(beans);
    return beans.get(0);
}
}
