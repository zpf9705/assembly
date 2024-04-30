package top.osjf.assembly.simplified.cache;

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
     * Same as {@link CacheObj#getValue()}.
     *
     * @return Fill in the values according to the scene.
     */
    String getValue();

    /**
     * Return whether the operation corresponding to {@link #getValue()}
     * above has affected its cache.
     *
     * @return if {@code true} affected its cache,{@code false} otherwise.
     */
    boolean result();

    /**
     * Directly setting this change has already had an impact
     * on the target module.
     */
    void ifSetResult();

    /**
     * Calculate the hash code for the overall attribute value of {@link Exchange}.
     *
     * @return the hash code for the overall attribute.
     */
    @Override
    int hashCode();

    /**
     * Compare whether two {@link Exchange} are equal.
     *
     * @return if {@code true} equals, {@code false} otherwise.
     */
    @Override
    boolean equals(Object obj);
}
