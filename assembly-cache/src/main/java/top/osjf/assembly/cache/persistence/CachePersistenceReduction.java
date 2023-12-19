package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.cache.exceptions.CachePersistenceException;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.io.File;

/**
 * The cache file recovery method defines an interface that can parse paths, files, and strings.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface CachePersistenceReduction {

    /**
     * Restore cache files based on the provided file path.
     *
     * @param path The disk path for cache persistence.
     * @throws CachePersistenceException Cache persistence exception.
     */
    void reductionUsePath(@CanNull String path) throws CachePersistenceException;

    /**
     * Restore cache files based on the provided files.
     *
     * @param file Persistence file
     * @throws CachePersistenceException Cache persistence exception.
     */
    void reductionUseFile(@NotNull File file) throws CachePersistenceException;

    /**
     * Restore the cache file based on the provided string.
     *
     * @param builder The string of the encapsulation type.
     * @throws CachePersistenceException Cache persistence exception.
     */
    void reductionUseString(@NotNull StringBuilder builder) throws CachePersistenceException;
}
