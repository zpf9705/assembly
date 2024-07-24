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

package top.osjf.cron.spring.cron4j;

import top.osjf.cron.spring.AbstractMethodRunnableRegistrantCollector;
import top.osjf.cron.spring.RunnableRegistrant;
import top.osjf.cron.spring.annotation.Cron;

import java.util.Objects;

/**
 * Cron4j's implementation of {@link top.osjf.cron.spring.RegistrantCollector}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class Cron4jRegistrantCollector extends AbstractMethodRunnableRegistrantCollector {

    /***  The expression for the shortest time interval supported by cron4j.. */
    private static final String cron4jMinExpression = "* * * * *";

    @Override
    public String ifGetDefaultExpression(String expression) {
        return Objects.equals(expression, Cron.DEFAULT_CRON_EXPRESSION) ? cron4jMinExpression : expression;
    }

    @Override
    protected RunnableRegistrant addRunnableRegistrantInternal(String expression, Runnable rab) {
        return new Cron4jRegistrant(expression, rab);
    }
}
