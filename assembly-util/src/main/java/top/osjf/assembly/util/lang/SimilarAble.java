package top.osjf.assembly.util.lang;

/**
 * It is difficult not to associate with the canonical {@link String} type
 * and related methods {@link String#equals(Object)} and {@link String#startsWith(String)}
 * and {@link String#endsWith(String)} and {@link String#contains(CharSequence)}
 * when comparing with the specified object being passed in.
 *
 * <p>Of course, it is only a canonical data type, and the definition of this
 * interface can also be used to specify its own similarity conditions.
 * This interface is not within the scope of JSR standards and is only
 * used for scenario needs.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see Similarator
 * @since 1.1.3
 */
public interface SimilarAble<T> {

    /**
     * Compare the similarity between this object and the specified object.
     * When the first parameter is similar to the second parameter, return
     * {@code true}, otherwise return {@code false}.<p>
     *
     * @param o the object to be similared.
     * @return a boolean type where true represents
     * similarity and vice versa represents no similarity.
     * @throws NullPointerException if an argument is null and this
     *                              comparator does not permit null arguments
     * @throws ClassCastException   if the arguments' types prevent them from
     *                              being compared by this comparator.
     */
    boolean similarTo(T o);
}
