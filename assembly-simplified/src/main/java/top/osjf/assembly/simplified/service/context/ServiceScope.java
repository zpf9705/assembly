package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The scope processing class of the corresponding bean for
 * service collection. The services to be collected will
 * not be initialized at project startup, but will be
 * separately initialized when the context is actually
 * used for retrieval, and saved to the storage structure
 * of the current class.
 *
 * <p>The corresponding service cache lifecycle is equivalent
 * to that of an application, and a unified {@link DisposableBean}
 * destruction callback is performed when the application is closed.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class ServiceScope implements Scope, DisposableBean {

    /*** There is a map for service mapping.*/
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>(16);

    /*** Destroy the cache collection of logic {@link DisposableBean}.*/
    private final List<Runnable> destructionCallbacks = new LinkedList<>();

    /*** placeholder for {@link #getConversationId()}*/
    private final String uniqueId = UUID.randomUUID().toString();

    @Override
    @NotNull
    public Object get(@NotNull String name, @NotNull ObjectFactory<?> objectFactory) {
        Object bean = serviceMap.get(name);
        if (bean == null) {
            bean = objectFactory.getObject();
            serviceMap.put(name, bean);
        }
        return bean;
    }

    @Nullable
    @Override
    public Object remove(@NotNull String name) {
        return serviceMap.remove(name);
    }

    @Override
    public void registerDestructionCallback(@NotNull String name, @NotNull Runnable callback) {
        destructionCallbacks.add(callback);
    }

    @Nullable
    @Override
    public Object resolveContextualObject(@NotNull String key) {
        return null;
    }

    @Nullable
    @Override
    public String getConversationId() {
        return uniqueId;
    }

    @Override
    public void destroy() {
        synchronized (this.destructionCallbacks) {
            for (Runnable runnable : this.destructionCallbacks) {
                runnable.run();
            }
            this.destructionCallbacks.clear();
        }
    }
}
