package top.osjf.assembly.cache.help;

import top.osjf.assembly.cache.command.ExpireCommands;
import top.osjf.assembly.cache.command.ExpireKeyCommands;
import top.osjf.assembly.cache.command.ExpireStringCommands;

/**
 * To simulate expire client helper abstract way, carry out a redis - spring encapsulation of learning
 * Here will command the classification, respectively for the implementation of the cache to better operation
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireHelper extends ExpireCommands, AutoCloseable {

    /**
     * Get {@link ExpireStringCommands}.
     *
     * @return never {@literal null}.
     */
    default ExpireStringCommands stringCommands() {
        return this;
    }

    /**
     * Get {@link ExpireKeyCommands}.
     *
     * @return never {@literal null}.
     */
    default ExpireKeyCommands keyCommands() {
        return this;
    }
}
