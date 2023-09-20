package top.osjf.assembly.cache.help;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * Thread-safe factory of Expire helpers
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireHelperFactory {

    /**
     * Provides a suitable helper for interacting with expire.
     *
     * @return Helper for interacting with expiry.
     */
    @NotNull
    ExpireHelper getHelper();
}
