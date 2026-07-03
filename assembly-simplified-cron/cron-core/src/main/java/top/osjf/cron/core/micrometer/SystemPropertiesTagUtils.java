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

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import top.osjf.commons.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * The system attribute monitoring tag utility class is used to uniformly parse
 * the built-in environment placeholders in the system.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class SystemPropertiesTagUtils {

    /** Key is fixed tag name, Value is environment variable placeholder expression */
    public static final String[] systemTagArray =
            {
                    /* os */
                    "os.name", "${os.name}",
                    "os.arch", "${os.arch}",
                    "os.version", "${os.version}",

                    /* java */
                    "java.vendor", "${java.vendor}",
                    "java.version", "${java.version}",
                    "java.vm.name", "${java.vm.name}",
                    "java.specification.version", "${java.specification.version}",

                    /* coding */
                    "file.encoding", "${file.encoding}"
            };

    /** System tag template constant */
    private static final Tags systemTags = Tags.of(systemTagArray);

    /**
     * Merge resolved global system tags into source business tags and return the combined tags
     *
     * @param source               the original business custom tags.
     * @param expressionResolver   the placeholder expression resolver.
     * @return the combined tags of business tags and system tags.
     */
    public static Tags mergResolvedSystemTags(Tags source, ExpressionResolver expressionResolver) {

        Assert.notNull(source, "source Tags must not be null");

        return source.and(getResolvedSystemTags(expressionResolver));
    }

    /**
     * Resolve placeholder expressions and return system tags with real parsed environment values
     *
     * @param expressionResolver the placeholder expression resolver.
     * @return the resolved system dimension tags.
     */
    public static Tags getResolvedSystemTags(ExpressionResolver expressionResolver) {

        Assert.notNull(expressionResolver, "expressionResolver must not be null");

        List<Tag> tags = new ArrayList<>();

        for (Tag systemTag : systemTags) {
            String resolvedValue = expressionResolver.resolveExpression(systemTag.getValue());
            tags.add(Tag.of(systemTag.getKey(), resolvedValue));
        }

        return Tags.of(tags);
    }
}
