package top.osjf.assembly.simplified.dcache;

/**
 * The storage interface for change information, which records
 * certain information that the cache needs to change based on.
 *
 * <p>It can be understood as the basis for changes in {@link CacheObj}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see CacheObj
 * @since 2.2.4
 */
public interface Exchange {

    /**
     * Same as {@link CacheObj#getValue().
     *
     * @return Fill in the values according to the scene.
     */
    String getValue();
}
