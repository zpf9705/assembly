package top.osjf.assembly.simplified.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class ThreadScoped implements Scope {

    private final ThreadScopedDestructionRegistry registry;

    private final ThreadLocal<Map<String, Object>> threadScope =
            new NamedThreadLocal<Map<String, Object>>("ThreadScoped") {
                @Override
                protected Map<String, Object> initialValue() {
                    return new HashMap<>();
                }
            };

    public ThreadScoped(ThreadScopedDestructionRegistry registry) {
        this.registry = registry;
    }

    @Override
    @NotNull
    public Object get(@NotNull String name, @NotNull ObjectFactory<?> objectFactory) {
        Map<String, Object> scope = this.threadScope.get();
        // NOTE: Do NOT modify the following to use Map::computeIfAbsent. For details,
        // see https://github.com/spring-projects/spring-framework/issues/25801.
        Object scopedObject = scope.get(name);
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            scope.put(name, scopedObject);
        }
        return scopedObject;
    }

    @Override
    @Nullable
    public Object remove(@NotNull String name) {
        Map<String, Object> scope = this.threadScope.get();
        return scope.remove(name);
    }

    @Override
    public void registerDestructionCallback(@NotNull String name, @NotNull Runnable callback) {
        registry.registerDestructionCallback(callback, threadScope::remove);
    }

    @Override
    @Nullable
    public Object resolveContextualObject(@NotNull String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return Thread.currentThread().getName();
    }
}
