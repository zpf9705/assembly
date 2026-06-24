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


package top.osjf.cron.core.exception;

/**
 /**
 * Exception thrown when the cron expression fails validation against the rule
 * of target task repository.Occurs when the cron expression cannot be parsed
 * or is not supported by the current scheduling framework repository.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class CronExpressionInvalidException extends CronInternalException {

    private static final long serialVersionUID = -635833751597710732L;

    private final String inValidExpression;

    private final String checkedRepositoryName;

    /**
     * Constructs a new {@code CronExpressionInvalidException} with specified invalid expression
     * and target repository name.
     *
     * @param inValidExpression      the invalid cron expression that failed validation
     * @param checkedRepositoryName the name of the repository executing the cron expression check
     */
    public CronExpressionInvalidException(String inValidExpression, String checkedRepositoryName) {
        super(String.format("Expression validation failed in repository [%s], invalid cron expression: %s",
                checkedRepositoryName, inValidExpression));
        this.inValidExpression = inValidExpression;
        this.checkedRepositoryName = checkedRepositoryName;
    }

    /**
     * Constructs a new {@code CronExpressionInvalidException} with invalid expression, repository name
     * and root cause.
     *
     * @param inValidExpression      the invalid cron expression that failed validation
     * @param checkedRepositoryName the name of the repository executing the cron expression check
     * @param cause                  the underlying root cause of the expression validation failure
     */
    public CronExpressionInvalidException(String inValidExpression, String checkedRepositoryName,
                                          Throwable cause) {
        super(String.format("Expression validation failed in repository [%s], invalid cron expression: %s",
                checkedRepositoryName, inValidExpression), cause);
        this.inValidExpression = inValidExpression;
        this.checkedRepositoryName = checkedRepositoryName;
    }

    /**
     * Get the invalid cron expression which failed repository rule validation.
     *
     * @return invalid cron expression string
     */
    public String getInValidExpression() {
        return inValidExpression;
    }

    /**
     * Get the name of the task repository that performed the cron expression validation.
     *
     * @return repository class/instance name
     */
    public String getCheckedRepositoryName() {
        return checkedRepositoryName;
    }
}
