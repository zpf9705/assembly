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


package top.osjf.cron.core.micrometer;

import java.lang.annotation.*;

/**
 * Annotation is used to automatically attach system runtime environment monitoring tags,
 * based on {@link ExpressionResolvableTags} to achieve configuration placeholder parsing,
 * automatically parsing the operating system JVM、 Host related environment variables
 * generate indicator dimensions.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 * @see ExpressionResolvableTags
 * @see RepositoryTagsBasedOnJoinPointFunction
 * @see SystemPropertiesTagUtils
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExpressionResolvableTags
        ({"os.name", "${os.name}", "java.version", "${java.version}", "hostname", "${HOSTNAME:local}"})
public @interface SystemPropertiesTags {
}
