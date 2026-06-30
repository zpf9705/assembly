/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.core.micrometer;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.search.MeterNotFoundException;
import io.micrometer.core.instrument.search.RequiredSearch;
import io.micrometer.core.instrument.search.Search;
import top.osjf.commons.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

/**
 * {@code MeterRegistry} static proxy delegation class encapsulates the common operations of
 * Micrometer indicator registry, providing a global static unified entrance, facilitating
 * quick collection and monitoring of indicators at various locations within the project,
 * and managing {@code MeterRegistry} instances uniformly.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class MeterRegistryDelegation {

    public static MeterRegistry meterRegistry = Metrics.globalRegistry;

    /**
     * Initialize the global MeterRegistry instance and replace the default global registry.
     * @param meterRegistry the custom indicator registry instance.
     */
    public static void initMeterRegistry(MeterRegistry meterRegistry) {
        MeterRegistryDelegation.meterRegistry = meterRegistry;
    }

    /**
     * @return The set of registered meters.
     */
    public static List<Meter> getMeters() {
        return meterRegistry.getMeters();
    }

    /**
     * Iterate over each meter in the registry.
     *
     * @param consumer Consumer of each meter during iteration.
     */
    public static void forEachMeter(Consumer<? super Meter> consumer) {
        meterRegistry.forEachMeter(consumer);
    }

    /**
     * @return A configuration object used to change the behavior of this registry.
     */
    public static MeterRegistry.Config config() {
        return meterRegistry.config();
    }

    /**
     * Initiate a search beginning with a metric name. If constraints added in the search are not satisfied, the search
     * will return {@code null}.
     *
     * @param name The meter name to locate.
     * @return A new search.
     */
    public static Search find(String name) {
        return meterRegistry.find(name);
    }

    /**
     * Initiate a search beginning with a metric name. All constraints added in the search must be satisfied or
     * an {@link MeterNotFoundException} is thrown.
     *
     * @param name The meter name to locate.
     * @return A new search.
     */
    public static RequiredSearch get(String name) {
        return meterRegistry.get(name);
    }

    /**
     * Tracks a monotonically increasing value.
     *
     * @param name The base metric name
     * @param tags Sequence of dimensions for breaking down the name.
     * @return A new or existing counter.
     */
    public static Counter counter(String name, Iterable<Tag> tags) {
        return meterRegistry.counter(name, tags);
    }

    /**
     * Tracks a monotonically increasing value.
     *
     * @param name The base metric name
     * @param tags MUST be an even number of arguments representing key/value pairs of tags.
     * @return A new or existing counter.
     */
    public static Counter counter(String name, String... tags) {
        return meterRegistry.counter(name, tags);
    }

    /**
     * Measures the distribution of samples.
     *
     * @param name The base metric name
     * @param tags Sequence of dimensions for breaking down the name.
     * @return A new or existing distribution summary.
     */
    public static DistributionSummary summary(String name, Iterable<Tag> tags) {
        return meterRegistry.summary(name, tags);
    }

    /**
     * Measures the distribution of samples.
     *
     * @param name The base metric name
     * @param tags MUST be an even number of arguments representing key/value pairs of tags.
     * @return A new or existing distribution summary.
     */
    public static DistributionSummary summary(String name, String... tags) {
        return meterRegistry.summary(name, tags);
    }

    /**
     * Measures the time taken for short tasks and the count of these tasks.
     *
     * @param name The base metric name
     * @param tags Sequence of dimensions for breaking down the name.
     * @return A new or existing timer.
     */
    public static Timer timer(String name, Iterable<Tag> tags) {
        return meterRegistry.timer(name, tags);
    }

    /**
     * Measures the time taken for short tasks and the count of these tasks.
     *
     * @param name The base metric name
     * @param tags MUST be an even number of arguments representing key/value pairs of tags.
     * @return A new or existing timer.
     */
    public static Timer timer(String name, String... tags) {
        return meterRegistry.timer(name, tags);
    }

    /**
     * Access to less frequently used meter types and patterns.
     *
     * @return Access to additional meter types and patterns.
     */
    public static MeterRegistry.More more() {
        return meterRegistry.more();
    }

    /**
     * Register a gauge that reports the value of the object after the function
     * {@code valueFunction} is applied. The registration will keep a weak reference to the object so it will
     * not prevent garbage collection. Applying {@code valueFunction} on the object should be thread safe.
     *
     * @param name          Name of the gauge being registered.
     * @param tags          Sequence of dimensions for breaking down the name.
     * @param stateObject   State object used to compute a value.
     * @param valueFunction Function that produces an instantaneous gauge value from the state object.
     * @param <T>           The type of the state object from which the gauge value is extracted.
     * @return The state object that was passed in so the registration can be done as part of an assignment
     * statement.
     */
    @Nullable
    public static <T> T gauge(String name, Iterable<Tag> tags, @Nullable T stateObject, ToDoubleFunction<T> valueFunction) {
        return meterRegistry.gauge(name, tags, stateObject, valueFunction);
    }

    /**
     * Register a gauge that reports the value of the {@link Number}.
     *
     * @param name   Name of the gauge being registered.
     * @param tags   Sequence of dimensions for breaking down the name.
     * @param number Thread-safe implementation of {@link Number} used to access the value.
     * @param <T>    The type of the number from which the gauge value is extracted.
     * @return The number that was passed in so the registration can be done as part of an assignment
     * statement.
     */
    @Nullable
    public static <T extends Number> T gauge(String name, Iterable<Tag> tags, T number) {
        return meterRegistry.gauge(name, tags, number);
    }

    /**
     * Register a gauge that reports the value of the {@link Number}.
     *
     * @param name   Name of the gauge being registered.
     * @param number Thread-safe implementation of {@link Number} used to access the value.
     * @param <T>    The type of the state object from which the gauge value is extracted.
     * @return The number that was passed in so the registration can be done as part of an assignment
     * statement.
     */
    @Nullable
    public static <T extends Number> T gauge(String name, T number) {
        return meterRegistry.gauge(name, number);
    }

    /**
     * Register a gauge that reports the value of the object.
     *
     * @param name          Name of the gauge being registered.
     * @param stateObject   State object used to compute a value.
     * @param valueFunction Function that produces an instantaneous gauge value from the state object.
     * @param <T>           The type of the state object from which the gauge value is extracted.
     * @return The state object that was passed in so the registration can be done as part of an assignment
     * statement.
     */
    @Nullable
    public static <T> T gauge(String name, T stateObject, ToDoubleFunction<T> valueFunction) {
        return meterRegistry.gauge(name, stateObject, valueFunction);
    }

    /**
     * Register a gauge that reports the size of the {@link Collection}. The registration
     * will keep a weak reference to the collection so it will not prevent garbage collection.
     * The collection implementation used should be thread safe. Note that calling
     * {@link Collection#size()} can be expensive for some collection implementations
     * and should be considered before registering.
     *
     * @param name       Name of the gauge being registered.
     * @param tags       Sequence of dimensions for breaking down the name.
     * @param collection Thread-safe implementation of {@link Collection} used to access the value.
     * @param <T>        The type of the state object from which the gauge value is extracted.
     * @return The Collection that was passed in so the registration can be done as part of an assignment
     * statement.
     */
    @Nullable
    public static <T extends Collection<?>> T gaugeCollectionSize(String name, Iterable<Tag> tags, T collection) {
        return meterRegistry.gaugeCollectionSize(name, tags, collection);
    }

    /**
     * Register a gauge that reports the size of the {@link Map}. The registration
     * will keep a weak reference to the collection so it will not prevent garbage collection.
     * The collection implementation used should be thread safe. Note that calling
     * {@link Map#size()} can be expensive for some collection implementations
     * and should be considered before registering.
     *
     * @param name Name of the gauge being registered.
     * @param tags Sequence of dimensions for breaking down the name.
     * @param map  Thread-safe implementation of {@link Map} used to access the value.
     * @param <T>  The type of the state object from which the gauge value is extracted.
     * @return The Map that was passed in so the registration can be done as part of an assignment
     * statement.
     */
    @Nullable
    public static <T extends Map<?, ?>> T gaugeMapSize(String name, Iterable<Tag> tags, T map) {
        return meterRegistry.gaugeMapSize(name, tags, map);
    }

    /**
     * Remove a {@link Meter} from this {@link MeterRegistry registry}. This is expected to be a {@link Meter} with
     * the same {@link Meter.Id} returned when registering a meter - which will have {@link MeterFilter}s applied to it.
     *
     * @param meter The meter to remove
     * @return The removed meter, or null if the provided meter is not currently registered.
     */
    @Nullable
    public static Meter remove(Meter meter) {
        return meterRegistry.remove(meter);
    }

    /**
     * Remove a {@link Meter} from this {@link MeterRegistry registry} based on its {@link Meter.Id}
     * before applying this registry's {@link MeterFilter}s to the given {@link Meter.Id}.
     *
     * @param preFilterId the id of the meter to remove
     * @return The removed meter, or null if the meter is not found
     */
    @Nullable
    public static Meter removeByPreFilterId(Meter.Id preFilterId) {
        return meterRegistry.removeByPreFilterId(preFilterId);
    }

    /**
     * Remove a {@link Meter} from this {@link MeterRegistry registry} based the given {@link Meter.Id} as-is. The registry's
     * {@link MeterFilter}s will not be applied to it. You can use the {@link Meter.Id} of the {@link Meter} returned
     * when registering a meter, since that will have {@link MeterFilter}s already applied to it.
     *
     * @param mappedId The id of the meter to remove
     * @return The removed meter, or null if no meter matched the provided id.
     */
    @Nullable
    public static Meter remove(Meter.Id mappedId) {
        return meterRegistry.remove(mappedId);
    }

    /**
     * Clear all meters.
     */
    public static void clear() {
        meterRegistry.clear();
    }

    /**
     * Closes this registry, releasing any resources in the process. Once closed, this registry will no longer
     * accept new meters and any publishing activity will cease.
     */
    public static void close() {
        meterRegistry.close();
    }

    /**
     * If the registry is closed, it will no longer accept new meters and any publishing activity will cease.
     *
     * @return {@code true} if this registry is closed.
     */
    public static boolean isClosed() {
        return meterRegistry.isClosed();
    }
}
