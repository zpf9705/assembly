package top.osjf.assembly.cache.core.persistence;

import top.osjf.assembly.cache.util.AssertUtils;
import top.osjf.assembly.util.SpiLoads;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Persistent cache files to restore selector abstract class, through the collection of.
 * <p>
 * in the factory,according to the different implementations for recovery plant selection
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class ExpireGlobePersistenceRenewSelector {

    /**
     * The query cache persistent chemical plant
     *
     * @param factoryClass factory class
     * @return implements {@code PersistenceFactory}
     */
    public static PersistenceRenewFactory getPersistenceFRenewFactory(Class<?> factoryClass) {
        if (factoryClass == null) {
            return null;
        }
        return getPersistenceFRenewFactoryWithClass(factoryClass);
    }

    /**
     * The query cache persistent chemical plant with client class type
     *
     * @param factoryClass factory class
     * @return implements {@code PersistenceFactory}
     */
    private static PersistenceRenewFactory getPersistenceFRenewFactoryWithClass(@NotNull Class<?> factoryClass) {
        PersistenceRenewFactory factory = PersistenceFactoryProvider.findRenewFactory(factoryClass);
        AssertUtils.Persistence.notNull(factory, "Sorry , no found clint name [" + factoryClass.getName() + "] " +
                "Persistence Factory");
        return factory;
    }

    /**
     * The query cache persistent chemical plant provider
     */
    static class PersistenceFactoryProvider {

        /**
         * cache factory client
         */
        private static List<PersistenceRenewFactory> PERSISTENCE_FACTORIES;

        /**
         * initialize sign
         */
        private static final AtomicBoolean initialize = new AtomicBoolean(false);

        static {
            if (initialize.compareAndSet(false, true)) {
                PERSISTENCE_FACTORIES = new ArrayList<>();
            }
            /*
             * @see ServiceLoadUtils
             */
            PERSISTENCE_FACTORIES = SpiLoads.findSpi(PersistenceRenewFactory.class).getSubInstances();
        }

        /**
         * According to the class from the cache object to obtain the corresponding factory
         *
         * @param factoryClass factory class
         * @return implements {@code PersistenceFactory}
         */
        public static PersistenceRenewFactory findRenewFactory(@NotNull Class<?> factoryClass) {
            return PERSISTENCE_FACTORIES.stream()
                    .filter(f -> Objects.equals(factoryClass.getName(), f.getRenewFactoryName()))
                    .findFirst()
                    .orElse(null);
        }
    }
}
