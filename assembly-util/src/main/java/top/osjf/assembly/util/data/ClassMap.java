package top.osjf.assembly.util.data;

import java.util.Map;


/**
 * Expand some methods related to {@link Class} operations.
 * <p>Continuously expanding.</p>
 *
 * @param <K> the type of keys maintained by this map.
 * @param <V> the type of mapped values.
 * @author zpf
 * @since 1.0.2
 */
@Deprecated
public interface ClassMap<K, V> extends Map<K, V> {

    /**
     * Merge the map array into this map.
     *
     * @param maps Multiple maps.
     */
    void mergeMaps(Map<? extends K, ? extends V>... maps);

    /**
     * Obtain value based on the key and convert it to the corresponding type.
     *
     * @param key   key with which the specified value is to be associated.
     * @param clazz The type class object that needs to convert value to.
     * @param <T>   Value converts object generics.
     * @return The converted object value.
     */
    <T> T getValueOnClass(K key, Class<T> clazz);
}
