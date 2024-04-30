package top.osjf.assembly.simplified.cache;

import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provide cache related support classes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
@SuppressWarnings("unchecked")
public final class CacheContextSupport {
    private CacheContextSupport() {
    }

    /*** Keep the changes made to the current thread.The initialization value is a {@link CopyOnWriteArrayList}.*/
    private static final ThreadLocal<List<Exchange>> exchanges = ThreadLocal.withInitial(CopyOnWriteArrayList::new);

    /*** A calculator for changing the execution order.The initialization value is a {@link AtomicInteger}.*/
    private static final ThreadLocal<AtomicInteger> counter = ThreadLocal.withInitial(() ->
            new AtomicInteger(-1));

    /**
     * Add a {@link Exchange} of the current change.
     *
     * @param exchange the current change.
     */
    @SuppressWarnings("rawtypes")
    public static void addCurrentExchange(Exchange exchange) {
        ((CopyOnWriteArrayList) exchanges.get()).addIfAbsent(exchange);
        counter.get().incrementAndGet();
    }

    /**
     * Returns all change information for the current thread,
     * and convert according to the provided template.
     *
     * @param <T> The type that needs to be converted.
     * @return all change information for the current thread.
     */
    public static <T extends Exchange> List<T> currentExchanges() {
        return (List<T>) exchanges.get();
    }

    /**
     * Returns appoint index change information for the current thread.
     *
     * @param <T> The type that needs to be converted.
     * @return appoint index change  information for the current thread.
     */
    public static <T extends Exchange> T currentOrderExchange() {
        List<Exchange> exchange0 = currentExchanges();
        if (CollectionUtils.isEmpty(exchange0)) return null;
        return (T) exchange0.get(counter.get().get());
    }

    /**
     * Delete all change information for the current thread.
     */
    public static void removeCurrentExchanges() {
        exchanges.remove();
        counter.remove();
    }
}
