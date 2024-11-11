/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.osjf.sdk.core.util;

import com.google.common.collect.ForwardingMap;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A thread-safe implementation of WeakHashMap. It wraps a WeakHashMap and uses the
 * {@code Collections.synchronizedMap()} method to ensure that all accesses are synchronized,
 * thereby achieving thread safety. This class retains all the characteristics of WeakHashMap,
 * including the use of weak references to store keys, which allows the garbage collector to
 * reclaim them when they are no longer referenced by other strong references.
 * <p>
 * Note: While this method provides thread-safe access, if multiple threads modify this map
 * concurrently, additional synchronization measures may be required to ensure thread safety.
 * For example, if the map is modified during iteration (except through the iterators own remove
 * method), external synchronization must be performed.
 * <p>
 * {@code SynchronizedWeakHashMap} still requires manual synchronization during iterations,
 * Because the mapping returned by Collections. synchronizedMap is not "fully synchronized".
 * Especially, when iterating the collection, it is necessary to manually synchronize externally
 * (e.g. through synchronization blocks) to avoid possible concurrent modification exceptions.
 * <p>
 * For example, during iteration, synchronization is required as follows:
 * <pre>
 *     {@code
 *          synchronized(synchronizedWeakHashMap) {
 *                 Iterator<Map.Entry<K, V>> it = synchronizedWeakHashMap.entrySet().iterator();
 *                 while (it.hasNext()) {
 *                      Map.Entry<K, V> entry = it.next();
 *                      // solve entry
 *                  }
 *          }
 *     }
 * </pre>
 *
 * @see WeakHashMap
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */

public class SynchronizedWeakHashMap<K, V> extends ForwardingMap<K, V> implements Map<K, V> {

    private final Map<K, V> delegate;

    /**
     * Constructs a new, empty <tt>SynchronizedWeakHashMap</tt> with the default initial
     * capacity (16) and load factor (0.75).
     */
    public SynchronizedWeakHashMap() {
        this(new WeakHashMap<>());
    }

    /**
     * Constructs a new, empty <tt>SynchronizedWeakHashMap</tt> with the given initial
     * capacity and the default load factor (0.75).
     *
     * @param initialCapacity The initial capacity of the <tt>WeakHashMap</tt>
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public SynchronizedWeakHashMap(int initialCapacity) {
        this(new WeakHashMap<>(initialCapacity));
    }

    /**
     * Constructs a new <tt>SynchronizedWeakHashMap</tt> with the same mappings as the
     * specified map.  The <tt>WeakHashMap</tt> is created with the default
     * load factor (0.75) and an initial capacity sufficient to hold the
     * mappings in the specified map.
     *
     * @param m the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     */
    public SynchronizedWeakHashMap(Map<? extends K, ? extends V> m) {
        this(new WeakHashMap<>(m));
    }

    /**
     * Constructs a new, empty <tt>SynchronizedWeakHashMap</tt> with the given initial
     * capacity and the given load factor.
     *
     * @param initialCapacity The initial capacity of the <tt>WeakHashMap</tt>
     * @param loadFactor      The load factor of the <tt>WeakHashMap</tt>
     * @throws IllegalArgumentException if the initial capacity is negative,
     *                                  or if the load factor is nonpositive.
     */
    public SynchronizedWeakHashMap(int initialCapacity, float loadFactor) {
        this(new WeakHashMap<>(initialCapacity, loadFactor));
    }

    /**
     * @param weakHashMap
     */
    private SynchronizedWeakHashMap(WeakHashMap<K, V> weakHashMap) {
        this.delegate = Collections.synchronizedMap(weakHashMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected Map<K, V> delegate() {
        return delegate;
    }
}
