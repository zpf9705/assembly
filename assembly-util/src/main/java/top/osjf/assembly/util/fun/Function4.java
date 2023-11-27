
package top.osjf.assembly.util.fun;

/**
 * A functional interface (callback) that computes a value
 * based on multiple input values.
 *
 * @param <T1> the first value type
 * @param <T2> the second value type
 * @param <T3> the third value type
 * @param <T4> the fourth value type
 * @param <R>  the result type
 * @author zpf
 * @since 1.0.8
 */
@FunctionalInterface
public interface Function4<T1, T2, T3, T4, R> {

    /**
     * Calculate a value based on the input values.
     *
     * @param t1 the first value.
     * @param t2 the second value.
     * @param t3 the third value.
     * @param t4 the fourth value.
     * @return the result value.
     * @throws Exception if the implementation wishes to throw any type of exception.
     */
    R doRequest(T1 t1, T2 t2, T3 t3, T4 t4) throws Exception;
}
