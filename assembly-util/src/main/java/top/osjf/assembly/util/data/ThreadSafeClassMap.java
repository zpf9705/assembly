package top.osjf.assembly.util.data;

import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.ConvertUtils;

import java.util.Hashtable;
import java.util.Map;

/**
 * A related implementation class of {@link ClassMap} inherits thread safe {@link Hashtable},
 * extends and rewrites methods, and ensures that the extension method is also thread safe.
 *
 * @param <K> the type of keys maintained by this map.
 * @param <V> the type of mapped values.
 * @author zpf
 * @since 1.0.2
 */
@Deprecated
public class ThreadSafeClassMap<K, V> extends Hashtable<K, V> implements ClassMap<K, V> {

    private static final long serialVersionUID = 5009068502167306533L;

    public ThreadSafeClassMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ThreadSafeClassMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ThreadSafeClassMap() {
    }

    public ThreadSafeClassMap(Map<? extends K, ? extends V> t) {
        super(t);
    }

    @Override
    public synchronized void mergeMaps(Map<? extends K, ? extends V>... maps) {
        if (ArrayUtils.isEmpty(maps)) {
            return;
        }
        for (Map<? extends K, ? extends V> map : maps) {
            this.putAll(map);
        }
    }

    @Override
    public synchronized <T> T getValueOnClass(K key, Class<T> clazz) {
        V v;
        if (key == null || clazz == null || (v = this.get(key)) == null) {
            return null;
        }
        return ConvertUtils.convert(clazz, v);
    }
}
