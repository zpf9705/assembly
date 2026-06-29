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

/**
 * Strategy interface for resolving expression strings to actual runtime values.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ExpressionResolver {

    /**
     * Resolves the original expression string and return the parsed actual result;
     * If the string does not contain a parsed expression, return the original input
     * content directly.
     * @param strVal the original string to be resolved.
     * @return the parsed result string; returns the original input if no resolvable
     * expression exists.
     */
    String resolveExpression(String strVal);
}
