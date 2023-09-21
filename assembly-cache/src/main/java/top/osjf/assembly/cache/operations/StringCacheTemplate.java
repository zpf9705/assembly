package top.osjf.assembly.cache.operations;

import top.osjf.assembly.cache.factory.CacheFactory;
import top.osjf.assembly.cache.serializer.StringPairSerializer;

/**
 * Both key and value range operations are limited to {@link String} types.
 *
 * @author zpf
 * @since 1.0.0
 */
public class StringCacheTemplate extends CacheTemplate<String, String> {

    private static final long serialVersionUID = 338557759999515451L;

    /**
     * Constructs a new <code>StringExpireTemplate</code> instance.
     * and {@link #afterPropertiesSet()} still need to be called.
     */
    public StringCacheTemplate() {
        this.setKeySerializer(new StringPairSerializer());
        this.setValueSerializer(new StringPairSerializer());
    }

    /**
     * Constructs a new <code>StringExpireTemplate</code> instance ready to be used.
     *
     * @param factory Helper factory for creating new connections
     */
    public StringCacheTemplate(CacheFactory factory) {
        super();
        this.setCacheFactory(factory);
        afterPropertiesSet();
    }
}
