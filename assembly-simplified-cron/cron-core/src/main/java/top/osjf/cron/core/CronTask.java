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


package top.osjf.cron.core;

import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public final class CronTask {

    @NotNull
    private final String expression;

    @NotNull
    private final Runnable runnable;

    @Nullable
    private final Method method;

    public CronTask(@NotNull String expression, @NotNull Runnable runnable, @Nullable Method method) {
        Objects.requireNonNull(expression,"<expression> == <null>");
        Objects.requireNonNull(expression,"<runnable> == <null>");
        this.expression = expression;
        this.runnable = runnable;
        this.method = method;
    }

    @NotNull
    public String getExpression() {
        return expression;
    }

    @NotNull
    public Runnable getRunnable() {
        return runnable;
    }

    @Nullable
    public Method getMethod() {
        return method;
    }
}
