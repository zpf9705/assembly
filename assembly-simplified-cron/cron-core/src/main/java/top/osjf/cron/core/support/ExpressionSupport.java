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


package top.osjf.cron.core.support;

/**
 * Regarding support for expressions.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public final class ExpressionSupport {
    private ExpressionSupport() {
    }
    /**
     * The default expression for the second level timing architecture.
     */
    private static final String SECOND_LEVEL_DEFAULT_EXPRESSION = "0/1 * * * * ?";
    /**
     * The default expression for minute level timing architecture.
     */
    private static final String MINUTE_LEVEL_DEFAULT_EXPRESSION = "* * * * *";

    /**
     * Return the default expression for second level timing architecture.
     * @return the default expression for second level timing architecture.
     */
    public static String secondLevelDefaultExpression() {
        return SECOND_LEVEL_DEFAULT_EXPRESSION;
    }

    /**
     * Return the default expression for minute level timing architecture.
     * @return the default expression for minute level timing architecture.
     */
    public static String minuteLevelDefaultExpression() {
        return MINUTE_LEVEL_DEFAULT_EXPRESSION;
    }
}
