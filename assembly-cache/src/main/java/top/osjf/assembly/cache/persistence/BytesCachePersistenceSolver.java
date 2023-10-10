package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.util.lang.Asserts;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The key/value of the byte array type implements the operation of caching persistent files.
 *
 * @author zpf
 * @since 1.0.0
 */
public class BytesCachePersistenceSolver implements CachePersistenceSolver<byte[], byte[]> {

    @Override
    public void putPersistence(@NotNull byte[] key, @NotNull byte[] value,
                               @CanNull Long duration,
                               @CanNull TimeUnit timeUnit) {

        run(() -> {
            ByteCachePersistence put =
                    ByteCachePersistence
                            .ofSetBytes(Entry.of(key, value, duration, timeUnit));
            //If repeated direct coverage
            put.write();
        }, "BytesCachePersistenceSolver::putPersistence");
    }

    @Override
    public void replaceValuePersistence(@NotNull byte[] key, @NotNull byte[] newValue) {
        run(() -> {
            ByteCachePersistence replace = ByteCachePersistence.ofGetBytes(key);
            Asserts.isTrue(replace.persistenceExist(), "persistence no exist");
            replace.replacePersistence(newValue);
        }, "BytesCachePersistenceSolver::replacePersistence");
    }

    @Override
    public void replaceDurationPersistence(@NotNull byte[] key, @NotNull Long duration,
                                           @NotNull TimeUnit timeUnit) {
        run(() -> {
            ByteCachePersistence replaceDuration = ByteCachePersistence.ofGetBytes(key);
            Asserts.isTrue(replaceDuration.persistenceExist(), "persistence no exist");
            replaceDuration.setExpirationPersistence(duration, timeUnit);
        }, "BytesCachePersistenceSolver::setEPersistence");
    }

    @Override
    public void restDurationPersistence(@NotNull byte[] key) {
        run(() -> {
            ByteCachePersistence reset = ByteCachePersistence.ofGetBytes(key);
            Asserts.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "BytesCachePersistenceSolver::restPersistence");
    }

    @Override
    public void removePersistenceWithKey(@NotNull byte[] key) {
        run(() -> {
            ByteCachePersistence remove = ByteCachePersistence.ofGetBytes(key);
            Asserts.isTrue(remove.persistenceExist(), "Persistence no exist, no repeat del");
            remove.removePersistence();
        }, "BytesCachePersistenceSolver::removePersistenceWithKey");
    }

    @Override
    public void removeSimilarKeyPersistence(@NotNull byte[] key) {
        run(() -> {
            Asserts.notNull(key, "key no be null");
            List<ByteCachePersistence> similar = ByteCachePersistence.ofGetSimilarBytes(key);
            Asserts.notEmpty(similar,
                    "No found key [" + Arrays.toString(key) + "] similar persistence");
            similar.forEach(s -> {
                if (s.persistenceExist()) {
                    s.removePersistence();
                }
            });
        }, "BytesCachePersistenceSolver::removePersistence");
    }
}
