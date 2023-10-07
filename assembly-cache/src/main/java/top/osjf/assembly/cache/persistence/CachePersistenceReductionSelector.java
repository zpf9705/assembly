package top.osjf.assembly.cache.persistence;

import org.springframework.util.Assert;
import top.osjf.assembly.util.spi.SpiLoads;

/**
 * Use {@link SpiLoads} to provide query services for the {@link CachePersistenceReduction} interface.
 *
 * @author zpf
 * @since 1.0.0
 */
public final class CachePersistenceReductionSelector {

    private CachePersistenceReductionSelector() {
    }

    public static CachePersistenceReduction getReductionByClass(Class<?> reductionClass) {
        if (reductionClass == null) {
            return null;
        }
        return getReductionByClass0(reductionClass);
    }

    private static CachePersistenceReduction getReductionByClass0(Class<?> reductionClass) {
        CachePersistenceReduction reduction = SpiLoads.findSpi(CachePersistenceReduction.class)
                .getSpecifiedServiceBySubClassName(reductionClass.getName());
        Assert.notNull(reduction, "No found named [" + reductionClass.getName() + "] in serviceLoad");
        return reduction;
    }
}
