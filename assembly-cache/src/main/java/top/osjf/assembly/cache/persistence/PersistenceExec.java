package top.osjf.assembly.cache.persistence;

import java.lang.annotation.*;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Persistent cache processing markup annotations are targeted at methods that can generate changes to
 * cache persistent files.
 *
 * <p>The parameters will define the class {@link CachePersistenceSolver} for cache processing,
 * as well as some validation of related expected values (such as cache execution results)
 *
 * @author zpf
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PersistenceExec {

    /**
     * Perform annotation type
     *
     * @return {@link PersistenceExecTypeEnum}
     */
    PersistenceExecTypeEnum value();

    /**
     * The processing of execution annotation corresponding interface implementation class type.
     *
     * @return Clazz of {@link CachePersistenceSolver}.
     */
    Class<? extends CachePersistenceSolver> shouldSolver() default BytesCachePersistenceSolver.class;

    /**
     * Perform enum within {@link ValueExpectations}.
     *
     * @return result value
     */
    ValueExpectations expectValue() default ValueExpectations.NULL;


    @SuppressWarnings("rawtypes")
    enum ValueExpectations implements Predicate<Object> {

        NULL(null),

        NOT_NULL(Objects::nonNull),

        LONG_NO_ZERO(v -> {
            if (!NOT_NULL.predicate.test(v) || !(v instanceof Long)) {
                return false;
            }
            return (Long) v > 0L;
        }),

        NOT_EMPTY(v -> {
            if (!NOT_NULL.predicate.test(v)) {
                return false;
            }
            if (v instanceof Collection) {
                return !((Collection) v).isEmpty();
            } else if (v instanceof Map) {
                return !((Map) v).isEmpty();
            }
            return false;
        }),

        REALLY(v -> {
            if (!NOT_NULL.predicate.test(v) || !(v instanceof Boolean)) {
                return false;
            }
            return ((Boolean) v);
        });

        private final Predicate<Object> predicate;

        @Override
        public boolean test(Object o) {
            Predicate<Object> pre = this.predicate;
            if (pre == null) {
                return true;
            }
            return pre.test(o);
        }

        ValueExpectations(Predicate<Object> predicate) {
            this.predicate = predicate;
        }
    }
}
