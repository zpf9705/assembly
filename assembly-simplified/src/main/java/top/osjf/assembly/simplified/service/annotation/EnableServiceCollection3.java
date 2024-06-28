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

package top.osjf.assembly.simplified.service.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.assembly.simplified.service.ServiceContextUtils;

import java.lang.annotation.*;

/**
 * The third version of service collection is more accurate
 * than the previous two versions, which collects rule bean
 * names during bean renaming.
 *
 * <p>In version 2.2.5, the collection of services has been
 * changed to store within the scope {@link ServiceContextUtils#SERVICE_SCOPE}.
 *
 * @see EnableServiceCollection
 * @see EnableServiceCollection2
 * @see ServiceContextRecordConfiguration
 * @see ServiceContextRecordConfiguration.ServiceContextRecordImportConfiguration
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ServiceContextRecordConfiguration.class,
        ServiceContextRecordConfiguration.ServiceContextRecordImportConfiguration.class})
public @interface EnableServiceCollection3 {
}
