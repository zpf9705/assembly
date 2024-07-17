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

package top.osjf.cron.core.exception;

/**
 * When the execution expression of the registered scheduled task
 * is invalid, an exception prompt is given.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class CronExpressionInvalidException extends CronException {

    private static final long serialVersionUID = -5171659277961608828L;

    private final String expression;

    public CronExpressionInvalidException(String expression) {
        super("The provided cron expression " + expression + " is not a valid value.");
        this.expression = expression;
    }

    /**
     * @return Returns an invalid cron expression.
     */
    public String getExpression() {
        return expression;
    }
}
