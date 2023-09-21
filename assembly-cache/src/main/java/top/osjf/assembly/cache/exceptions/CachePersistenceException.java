package top.osjf.assembly.cache.exceptions;

/**
 * Exception related to cache persistence.
 *
 * @author zpf
 * @since 1.0.0
 */
public class CachePersistenceException extends IllegalArgumentException {

    private static final long serialVersionUID = 7054751876464142577L;

    /**
     * Create an expiration specified exception without parameters
     */
    public CachePersistenceException() {
        super("An unknown error occurred in cache persistence.");
    }

    /**
     * Create an expiration exception based on a specific information
     *
     * @param message A specific exception message
     */
    public CachePersistenceException(String message) {
        super(message);
    }
}
