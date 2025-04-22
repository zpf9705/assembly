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

package top.osjf.sdk.http.annotation;

import com.google.common.collect.ForwardingObject;
import top.osjf.sdk.core.lang.NotNull;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * An abstract implementation of {@code Logger}, the proxy logger executes
 * the obtained {@link Messager}, and the method for defining parameter
 * formatting requires subclass implementation, including various types
 * supported by {@link Diagnostic.Kind}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class AbstractLogger extends ForwardingObject implements Logger {

    @Override
    @NotNull
    public abstract Messager delegate();

    public abstract String formatMessage(String msg, Object... arguments);

    @Override
    public void error(String msg, Object... arguments) {
        delegate().printMessage(Diagnostic.Kind.ERROR, formatMessage(msg, arguments));
    }

    @Override
    public void warning(String msg, Object... arguments) {
        delegate().printMessage(Diagnostic.Kind.WARNING, formatMessage(msg, arguments));
    }

    @Override
    public void mandatoryWaring(String msg, Object... arguments) {
        delegate().printMessage(Diagnostic.Kind.MANDATORY_WARNING, formatMessage(msg, arguments));
    }

    @Override
    public void note(String msg, Object... arguments) {
        delegate().printMessage(Diagnostic.Kind.NOTE, formatMessage(msg, arguments));
    }

    @Override
    public void other(String msg, Object... arguments) {
        delegate().printMessage(Diagnostic.Kind.OTHER, formatMessage(msg, arguments));
    }
}
