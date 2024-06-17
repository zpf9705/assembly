package top.osjf.assembly.util.lang;

/**
 * A similar tool used to compare two objects. The return rule uses
 * traditional Boolean types, which greatly facilitates the development
 * of rules.
 *
 * <p>This specification is not commonly used and has not been included
 * in the JDK development specification. It is only used for certain
 * occasions and is no longer included in the JSR specification.
 * It can be selected according to needs.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see SimilarAble
 * @since 1.1.3
 */
public interface Similarator<T> {

    /**
     * Compare the similarity between its two parameters. When the first parameter
     * is similar to the second parameter, return {@code true}, otherwise return
     * {@code false}.<p>
     *
     * @param o1 the first object to be similared.
     * @param o2 the second object to be similared.
     * @return a boolean type where true represents
     * similarity and vice versa represents no similarity.
     * @throws NullPointerException if an argument is null and this
     *                              similarator does not permit null arguments
     * @throws ClassCastException   if the arguments' types prevent them from
     *                              being similared by this comparator.
     */
    boolean similarTo(T o1, T o2);
}
