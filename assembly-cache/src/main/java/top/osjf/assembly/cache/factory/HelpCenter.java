package top.osjf.assembly.cache.factory;

/**
 * Interface indicating a component that contains and exposes
 * an {@link HelpCenter} reference.
 * <p>Obtain a fully configured cache framework component based
 * on this interface and place it in its execution class to be
 * called at runtime.</p>
 * @param <T> The type of help center.
 * @author zpf
 * @since 1.0.0
 */
public interface HelpCenter<T> {

    /**
     * @return A {@link HelpCenter} associated with this caching framework.
     */
    T getHelpCenter();
}
