package top.osjf.assembly.cache.factory;

import top.osjf.assembly.cache.command.CacheCommands;
import top.osjf.assembly.cache.command.CacheKeyCommands;
import top.osjf.assembly.cache.command.CachePairCommands;

/**
 * As the name suggests, it is a cache executor interface that
 * provides the implementation of cache methods.
 * <p>It inherits the relevant instruction methods defined by the
 * cache, and publicizes it with the data type in the form of a
 * unified {@link Byte} array of data, commonly known as serialization. <br>
 * When taking out the data, the {@link Byte} array is deserialized
 * into real objects,which are the values of key and value.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface CacheExecutor extends CacheCommands {

    default CachePairCommands pairCommands() {
        return this;
    }

    default CacheKeyCommands keyCommands() {
        return this;
    }
}
