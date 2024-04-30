package top.osjf.assembly.simplified.cache;

import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
            new AtomicInteger(0));

    /**
     * Add the current {@link Exchange} in an orderly.
     *
     * <p>When adding a {@link Exchange}, its repeatability
     * is not taken into account. Instead, the additions are
     * arranged in sequence and repeated calculations are
     * performed during subsequent induction.
     *
     * @param exchange the current change.
     */
    public static void addCurrentExchange(Exchange exchange) {
        //Empty value items will not be recorded here temporarily.
        if (exchange != null) {
            exchanges.get().add(exchange);
            //Add the current index counter.
            counter.get().incrementAndGet();
        }
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
     * Returns all change information for the current thread,
     * and convert according to the provided template.
     *
     * @param <T> The type that needs to be converted.
     * @return all change information for the current thread.
     */
    public static <T extends Exchange> List<T> currentPurgeExchanges() {
        List<Exchange> purgeChanges = new CopyOnWriteArrayList<>();
        currentExchanges().stream().collect(Collectors.groupingBy(Exchange::getValue)).forEach(
                (value, exchanges) -> {
                    if (exchanges.stream().anyMatch(Exchange::result)){
                        Exchange exchange = exchanges.get(0);
                        exchange.ifSetResult();
                        purgeChanges.add(exchange);
                    }
                }
        );
        return (List<T>) purgeChanges;
    }

    /**
     * Returns the total count of current changes {@link Exchange}.
     *
     * @return the total count of current changes {@link Exchange}.
     */
    public static int getCurrentExchangeNumber() {
        return counter.get().get();
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
        return (T) exchange0.get(/*Because the count starts from 0, the index sorting order starts with -1*/
                getCurrentExchangeNumber() - 1);
    }

    /**
     * Delete all change information for the current thread.
     */
    public static void removeCurrentExchanges() {
        exchanges.remove();
        counter.remove();
    }
}
