package top.osjf.assembly.cache.command;

import top.osjf.assembly.cache.persistence.CachePersistenceSolver;
import top.osjf.assembly.cache.persistence.PersistenceExec;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.logger.Console;
import top.osjf.assembly.util.spi.SpiLoads;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The proxy monitoring process class for cache operation execution aims to perform cache persistence
 * operations based on {@link PersistenceExec} annotated by instruction set methods.
 * @param <T> The type of real object.
 * @author zpf
 * @since 1.0.0
 */
public class CacheInvocationHandler<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = 1220499099081639297L;

    private final T target;

    public CacheInvocationHandler(T target) {
        this.target = target;
    }

    @NotNull
    public T getTarget() {
        return this.target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final T target = this.target;
        Object result = method.invoke(target, args);
        PersistenceExec exec = method.getAnnotation(PersistenceExec.class);
        if (exec != null) {
            persistenceExec(result, exec, args);
        }
        return result;
    }

    /**
     * Perform cache persistence operations.
     *
     * @param result Cached execution results.
     * @param exec   Caching persistent annotations.
     * @param args   Cache execution parameter array.
     */
    @SuppressWarnings("rawtypes")
    public void persistenceExec(@CanNull Object result, PersistenceExec exec, Object[] args) {
        if (!exec.expectValue().test(result)) {
            return;
        }
        CachePersistenceSolver solver = SpiLoads.findSpi(CachePersistenceSolver.class)
                .getSpecifiedServiceBySubClass(exec.shouldSolver());
        if (solver == null) {
            Console.warn("Provider Persistence [{}] shouldSolver load null", exec.shouldSolver().getName());
            return;
        }
        exec.value().dispose(solver, args);
    }
}
