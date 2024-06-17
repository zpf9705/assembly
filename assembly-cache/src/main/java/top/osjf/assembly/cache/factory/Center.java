package top.osjf.assembly.cache.factory;

/**
 * This interface is the core interface for implementing caching methods
 * and subsequent cache recovery and deletion of persistent files.
 *
 * <p>Inherited the help center (i.e. cache operation entity) {@link HelpCenter}
 * and recovery center (i.e. implementation of subsequent cache recovery
 * methods) {@link ReloadCenter}.
 *
 * @param <C> The type of help center.
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author zpf
 * @since 1.0.0
 */
public interface Center<C, K, V> extends HelpCenter<C>, ReloadCenter<K, V> {
}
