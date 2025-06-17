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


package top.osjf.sdk.spring.runner.intercept;

import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;
import top.osjf.sdk.core.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Local Script Execution Context {@link ScriptExecuteContext} Manager (Thread Safe)
 *
 * <p>Manage the script execution context of the current thread, supporting the addition,
 * removal, and querying of contexts.
 *
 * <p>As a supplement to {@link ScriptExecuteInterceptor} parsing dynamic parameters
 * to obtain {@link ScriptExecuteContext}, developers can also dynamically add the
 * {@link ScriptExecuteContext} object execution set of running threads through this
 * type, and can also add execution when intercepting execution.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public abstract class LocalScriptExecuteContextManager {

    private static final ThreadLocal<List<ScriptExecuteContext>> contexts =
            new NamedThreadLocal<>("Script contexts");

    /**
     * Check if the current thread has any script execution contexts.
     * @return Returns {@code true} if contexts exist, {@code false} otherwise.
     */
    public static boolean hasContext() {
        List<ScriptExecuteContext> cc = contexts.get();
        return CollectionUtils.isNotEmpty(cc);
    }

    /**
     * Get all script execution contexts for the current thread (unmodifiable).
     * @return An unmodifiable list of script execution contexts.
     */
    public static List<ScriptExecuteContext> getContexts() {
        return Collections.unmodifiableList(contexts.get());
    }

    /**
     * Add a script execution context to the current thread.
     * @param context The script execution context to add, must not be {@literal null}.
     */
    public static void addContext(ScriptExecuteContext context) {
        Assert.notNull(context, "Context must not be null");
        List<ScriptExecuteContext> cc = contexts.get();
        // set ThreadLocal if none init.
        if (cc == null) {
            cc = new ArrayList<>();
            contexts.set(cc);
        }
        cc.add(context);
    }

    /**
     * Remove a specific script execution context from the current thread.
     * @param context The script execution context to remove.
     */
    public static void removeContext(ScriptExecuteContext context) {
        List<ScriptExecuteContext> cc = contexts.get();
        if (cc != null) {
            cc.remove(context);
        }
    }

    /**
     * Remove all script execution contexts for the current thread.
     */
    public static void removeAll() {
        contexts.remove();
    }
}
