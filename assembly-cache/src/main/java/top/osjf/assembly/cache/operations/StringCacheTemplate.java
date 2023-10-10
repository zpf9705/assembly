package top.osjf.assembly.cache.operations;

import top.osjf.assembly.cache.factory.CacheFactory;
import top.osjf.assembly.cache.serializer.PairSerializer;
import top.osjf.assembly.cache.serializer.StringPairSerializer;

/**
 * Both key and value range operations are limited to {@link String} types.
 *
 * @author zpf
 * @since 1.0.0
 */
public class StringCacheTemplate extends CacheTemplate<String, String> {

    private static final long serialVersionUID = 338557759999515451L;

    public StringCacheTemplate() {
        this.setKeySerializer(new StringPairSerializer());
        this.setValueSerializer(new StringPairSerializer());
    }

    public StringCacheTemplate(CacheFactory factory) {
        super(factory);
        this.setKeySerializer(new StringPairSerializer());
        this.setValueSerializer(new StringPairSerializer());
    }

    public StringCacheTemplate(CacheFactory cacheFactory,
                               PairSerializer<String> keySerialize,
                               PairSerializer<String> valueSerialize) {
        super(cacheFactory, keySerialize, valueSerialize);
    }
}
