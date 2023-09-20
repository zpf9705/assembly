package top.osjf.assembly.cache.command;

import top.osjf.assembly.cache.core.persistence.PersistenceExec;
import top.osjf.assembly.cache.core.persistence.PersistenceExecTypeEnum;
import top.osjf.assembly.util.annotation.CanNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Some operational methods for constructing domains, including element addition, atomic operations, and retrieval.
 * <p>
 * Some involve cache persistence operations, which are uniformly processed in the proxy method
 * {@link top.osjf.assembly.cache.core.ExpiryInvocationTrace#invokeSubsequent(Object, Annotation, Object[])}
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireStringCommands {

    /**
     * Set {@code value} for {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    @PersistenceExec(value = PersistenceExecTypeEnum.SET, expectValue = PersistenceExec.ValueExpectations.REALLY)
    Boolean set(byte[] key, byte[] value);

    /**
     * Set the {@code value} and expiration {@code timeout} for {@code key}.
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration the key expiration timeout.
     * @param unit     must not be {@literal null}.
     * @return setE result
     */
    @PersistenceExec(value = PersistenceExecTypeEnum.SET, expectValue = PersistenceExec.ValueExpectations.REALLY)
    Boolean setE(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Set {@code value} for {@code key}, only if {@code key} does not exist.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    @PersistenceExec(value = PersistenceExecTypeEnum.SET, expectValue = PersistenceExec.ValueExpectations.REALLY)
    Boolean setNX(byte[] key, byte[] value);

    /**
     * Set {@code value} for {@code key}, only if {@code key} does not exist.
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration the key expiration timeout.
     * @param unit     must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    @PersistenceExec(value = PersistenceExecTypeEnum.SET, expectValue = PersistenceExec.ValueExpectations.REALLY)
    Boolean setEX(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    byte[] get(byte[] key);

    /**
     * Get Similar keys of {@code key}.
     *
     * @param rawKey must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    List<byte[]> getSimilarKeys(byte[] rawKey);

    /**
     * Set {@code value} of {@code key} and return its old value.
     *
     * @param key      must not be {@literal null}.
     * @param newValue must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    @PersistenceExec(value = PersistenceExecTypeEnum.REPLACE_VALUE,
            expectValue = PersistenceExec.ValueExpectations.NOT_NULL)
    byte[] getAndSet(byte[] key, byte[] newValue);
}
