package top.osjf.assembly.util.spi;

import org.apache.commons.collections4.IteratorUtils;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Program interface loader tool, mainly used for the set of query interface implementation class.
 *
 * @author zpf
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class SpiLoads<T> {

    private static final Map<String, SpiLoads> LOAD_CACHE = new ConcurrentHashMap<>();

    private final ServiceLoader<T> load;

    private final List<T> subInstances;

    private final Class<T> loadClazz;

    private SpiLoads(Class<T> loadClazz) {
        this.loadClazz = loadClazz;
        this.load = ServiceLoader.load(this.loadClazz);
        this.subInstances = IteratorUtils.toList(this.load.iterator());
    }

    /**
     * Use {@link ServiceLoader} to make the program interface implementation class load.
     *
     * @param loadClazz To load the interface class.
     * @param <T>       load type.
     * @return this instance.
     */
    public static <T> SpiLoads<T> findSpi(@NotNull Class<T> loadClazz) {
        String name = loadClazz.getName();
        SpiLoads loads = LOAD_CACHE.get(name);
        if (loads == null) {
            synchronized (SpiLoads.class) {
                loads = LOAD_CACHE.get(name);
                if (loads == null) {
                    LOAD_CACHE.putIfAbsent(name, new SpiLoads(loadClazz));
                    loads = LOAD_CACHE.get(name);
                }
            }
        }
        return loads;
    }

    public ServiceLoader<T> getLoad() {
        return load;
    }

    public Class<T> getLoadClazz() {
        return loadClazz;
    }


    public List<T> getSubInstances() {
        return subInstances;
    }

    /**
     * To load all the program interface implementation class be a class map.
     *
     * @return {@link Iterator}.
     */
    public Map<Class<?>, T> withClassMap() {
        return this.subInstances.stream().collect(Collectors.toMap(T::getClass, Function.identity(), (k1, k2) -> k1));
    }

    /**
     * By class type for the specified type of interface implementation class type.
     *
     * @param subClass specified type.
     * @return Specified subtype.
     */
    public T getSpecifiedServiceBySubClass(Class<? extends T> subClass) {
        return findBySubPredict(t -> t.getClass() == subClass);
    }

    /**
     * By class type for the specified type of interface implementation class name.
     *
     * @param className specified type.
     * @return Specified subtype.
     */
    public T getSpecifiedServiceBySubClassName(String className) {
        return findBySubPredict(t -> t.getClass().getName().equals(className));
    }

    /**
     * Under conditions of assertions.
     *
     * @param predicate {@link Predicate}.
     * @return Result object.
     */
    private T findBySubPredict(Predicate<T> predicate) {
        for (T instance : this.subInstances) {
            if (predicate.test(instance)) {
                return instance;
            }
        }
        return null;
    }
}
