package top.osjf.assembly.cache.operations;

import top.osjf.assembly.cache.factory.CacheFactory;
import top.osjf.assembly.cache.serializer.PairSerializer;
import top.osjf.assembly.cache.serializer.SerializerAdapter;
import top.osjf.assembly.cache.serializer.StringPairSerializer;

/**
 * Value range operations are limited to {@link Object} types.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
public class ValueObjectedCacheTemplate extends CacheTemplate<String, Object> {

    private static final long serialVersionUID = 2261336868753642232L;

    public ValueObjectedCacheTemplate() {
        this.setKeySerializer(new StringPairSerializer());
        this.setValueSerializer(new SerializerAdapter<>(Object.class));
    }

    public ValueObjectedCacheTemplate(CacheFactory factory) {
        super(factory);
        this.setKeySerializer(new StringPairSerializer());
        this.setValueSerializer(new SerializerAdapter<>(Object.class));
    }

    public ValueObjectedCacheTemplate(CacheFactory cacheFactory,
                                      PairSerializer<String> keySerialize,
                                      PairSerializer<Object> valueSerialize) {
        super(cacheFactory, keySerialize, valueSerialize);
    }
}
