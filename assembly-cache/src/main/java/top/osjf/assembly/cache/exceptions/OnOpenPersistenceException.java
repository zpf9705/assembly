package top.osjf.assembly.cache.exceptions;

/**
 * No open cache persistent exception.
 *
 * @author zpf
 * @since 1.0.0
 */
public class OnOpenPersistenceException extends IllegalStateException {

    private static final long serialVersionUID = -8577175905882006486L;

    public OnOpenPersistenceException() {
        super("The cache persistence button is not enabled.");
    }
}
