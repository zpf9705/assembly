package top.osjf.assembly.cache.help.expiremap;

import top.osjf.assembly.cache.help.ExpireHelper;
import top.osjf.assembly.cache.help.ExpireHelperFactory;
import top.osjf.assembly.cache.util.JdkProxyUtils;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * ExpireMap Connection factory creating for {@code ExpireHelperFactory}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapHelperFactory implements ExpireHelperFactory {

    private final ExpireMapHelper helper;

    public ExpireMapHelperFactory(@NotNull ExpireMapClientConfiguration clientConfiguration) {
        this.helper = doCreateExpireMapHelp(clientConfiguration);
    }

    @Override
    @NotNull
    public ExpireHelper getHelper() {
        return this.helper;
    }

    /**
     * Setting an Expire Map connection
     *
     * @param clientConfiguration {@link ExpireMapClientConfiguration}
     * @return return a {@link ExpireMapHelper}
     */
    public ExpireMapHelper doCreateExpireMapHelp(ExpireMapClientConfiguration clientConfiguration) {
        //Real object generated singleton operation
        ExpireMapCenter expireMapCenter = ExpireMapCenter.singletonWithConfiguration(clientConfiguration);
        //To approach the processor
        ExpireMapPersistenceProcessor processor = ExpireMapPersistenceProcessor.buildProcessor(
                new ExpireMapRealHelper(() -> expireMapCenter)
        );
        return JdkProxyUtils.createProxy(processor);
    }
}
