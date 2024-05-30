package top.osjf.assembly.simplified.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class ThreadScoped implements Scope ,Runnable{

    private final ThreadLocal<Map<String, Object>> threadScope =
            new NamedThreadLocal<Map<String, Object>>("ThreadScoped") {
                @Override
                protected Map<String, Object> initialValue() {
                    return new HashMap<>();
                }
            };

    /**
     * Map from attribute name String to destruction callback Runnable.
     */
    private final ThreadLocal<List<Runnable>> callbackThreadLocal = ThreadLocal.withInitial(LinkedList::new);

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
        List<Runnable> destructionCallbacks = callbackThreadLocal.get();
        destructionCallbacks.add(callback);
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

    @Override
    public void run() {
        List<Runnable> destructionCallbacks = callbackThreadLocal.get();
        if (CollectionUtils.isNotEmpty(destructionCallbacks)){
            for (Runnable destructionCallback : destructionCallbacks) {
                destructionCallback.run();
            }
            callbackThreadLocal.remove();
        }
        threadScope.remove();
    }
}
