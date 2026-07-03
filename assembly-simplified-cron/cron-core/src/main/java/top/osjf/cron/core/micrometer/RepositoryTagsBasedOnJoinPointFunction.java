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
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import top.osjf.commons.util.AnnotationUtils;
import top.osjf.commons.util.Assert;
import top.osjf.commons.util.CollectionUtils;
import top.osjf.cron.core.repository.CronTaskRepository;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static top.osjf.cron.core.micrometer.RepositoryMicrometerConstants.MODULE_TAG_KEY;

/**
 * Function implementation class for generating storage layer monitoring labels based on
 * cross-sectional connection points.
 * <ul>
 * <li>Automatically obtain the name of the currently executed {@link CronTaskRepository}
 * implementation class and generate default module labels;</li>
 * <li>Perform dynamic expression parsing on the Key and Value of tags through {@link ExpressionResolver};</li>
 * <li>Priority is given to using the expression parser passed in by the constructor, and if
 * there is no input, the system property parser {@link SystemPropertyExpressionResolver} is
 * used by default</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class RepositoryTagsBasedOnJoinPointFunction implements Function<ProceedingJoinPoint, Iterable<Tag>> {

    private final ExpressionResolver expressionResolver;

    /**
     * Constructs a {@code RepositoryTagsBasedOnJoinPointFunction} default to initialize
     * a {@link SystemPropertyExpressionResolver}.
     */
    public RepositoryTagsBasedOnJoinPointFunction() {
        this(new SystemPropertyExpressionResolver());
    }

    /**
     * Constructs a {@code RepositoryTagsBasedOnJoinPointFunction} with the given {@link ExpressionResolver}.
     * @param expressionResolver the given {@link ExpressionResolver}.
     */
    public RepositoryTagsBasedOnJoinPointFunction(ExpressionResolver expressionResolver) {
        Assert.notNull(expressionResolver, "expressionResolver must not be null");
        this.expressionResolver = expressionResolver;
    }

    @Override
    public Iterable<Tag> apply(ProceedingJoinPoint pjp) {
        List<Tag> tagList = new ArrayList<>();
        // Obtain the real target object of the proxy
        Object target = pjp.getTarget();

        Assert.isTrue(target instanceof CronTaskRepository,
                "Only can resolve top.osjf.cron.core.repository.CronTaskRepository");

        // Splicing built-in storage module labels...
        CronTaskRepository repository = (CronTaskRepository) target;
        tagList.add(Tag.of(MODULE_TAG_KEY, repository.getName()));

        // Get the current execution method...
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method targetMethod = methodSignature.getMethod();

        // Find merged ExpressionResolvableTags of the specified type from the target method.
        Set<ExpressionResolvableTags> annotations
                = AnnotationUtils.findMethodMergedAnnotations(targetMethod, ExpressionResolvableTags.class);
        if (CollectionUtils.isNotEmpty(annotations)) {
            for (ExpressionResolvableTags resolvableTags : annotations) {
                // Traverse the configured tag array and perform expression parsing on key and value separately.
                for (Tag originalTag : /* Using native adaptation methods */Tags.of(resolvableTags.value())) {
                    String resolvedValue = expressionResolver.resolveExpression(originalTag.getValue());
                    tagList.add(Tag.of(originalTag.getKey(), resolvedValue));
                }
            }
        }

        return tagList;
    }
}
