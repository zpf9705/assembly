package top.osjf.assembly.simplified.dcache;

import java.util.LinkedList;
import java.util.List;

/**
 * Provide cache related support classes.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public final class CacheContextSupport {
    private CacheContextSupport() {
    }

    /*** Keep the changes made to the current thread.*/
    private static final ThreadLocal<List<Exchange>> exchanges = new ThreadLocal<>();

    /**
     * Add a {@link Exchange} of the current change.
     *
     * @param exchange the current change.
     */
    public static void addCurrentExchange(Exchange exchange) {
        List<Exchange> local = currentExchanges();
        if (local == null) {
            local = new LinkedList<>();
            exchanges.set(local);
        }
        local.add(exchange);
    }

    /**
     * Returns all change information for the current thread.
     *
     * @return all change information for the current thread.
     */
    public static List<Exchange> currentExchanges() {
        return exchanges.get();
    }

    /**
     * Delete all change information for the current thread.
     */
    public static void removeCurrentExchanges() {
        exchanges.remove();
    }
}
