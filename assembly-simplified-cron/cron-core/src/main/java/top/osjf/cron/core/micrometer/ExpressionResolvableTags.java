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

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;

import java.lang.annotation.*;

/**
 * Annotation to declare extra metric tags that support expression dynamic resolution.
 *
 * <p>Refer to the native {@link Counted#extraTags()} and {@link Timed#extraTags()}
 * designs, used to declare custom monitoring tag key value pairs on methods; Different
 * from native static tags, the tag Key and Value configured in this annotation will
 * be dynamically parsed through {@link ExpressionResolver} to achieve runtime dynamic
 * generation of indicator dimensions.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 *
 * @see RepositoryTagsBasedOnJoinPointFunction
 * @see Counted#extraTags()
 * @see Timed#extraTags()
 * @see io.micrometer.core.aop.CountedAspect
 * @see io.micrometer.core.aop.TimedAspect
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpressionResolvableTags {

    /**
     * An array of key-value pairs for extra metric tags.
     * <p>
     * Entries must be provided in pairs following the format [key1, value1, key2, value2].
     * Each key and value will be resolved via {@link ExpressionResolver}.
     * Plain strings will be used directly if no resolvable configuration key is matched.
     *
     * @return an array of tag key-value pairs
     * @see io.micrometer.core.instrument.Timer.Builder#tags(String...)
     */
    String[] value() default {};
}
