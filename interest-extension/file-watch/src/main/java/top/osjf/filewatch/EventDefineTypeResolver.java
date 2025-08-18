/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.filewatch;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@code WatchEvent&lt;Path&gt;} defined resolver util class.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
final class EventDefineTypeResolver {

    /** Cache of {@link #resolveEvent} result */
    private static final ConcurrentMap<Class<? extends FileWatchListener>, Class<? extends WatchEvent<Path>>>
            defineEventMapping = new ConcurrentHashMap(16);

    /**
     * Resolve the {@code FileWatchListener} generic of {@code WatchEvent&lt;Path&gt;} and
     * instantiated it to return.
     * @param listener       the {@code FileWatchListener} to resolve.
     * @param registeredPath the registered path to construct instantiation。
     * @param rawEvent       the raw {@code WatchEvent&lt;Path&gt;}.
     * @return Result of define {@code WatchEvent&lt;Path&gt;}.
     * @throws FileWatchException if define {@code WatchEvent&lt;Path&gt;} instantiate failed.
     */
    public static WatchEvent<Path> resolveEvent(FileWatchListener listener, Path registeredPath,
                                                 WatchEvent<Path> rawEvent) {
        if (listener instanceof AmpleFileWatchListener) {
            return new AmapleWatchEvent(registeredPath, rawEvent);
        }

        Class<? extends WatchEvent<Path>> clazz = defineEventMapping.computeIfAbsent(listener.getClass(),
                EventDefineTypeResolver::deepGetDefineEventClass);

        if (clazz == DefaultWatchEvent.class) {
            return rawEvent;
        }

        return instantiateDefineEvent(clazz, registeredPath, rawEvent);
    }

    /** The default {@code WatchEvent&lt;Path&gt;} is used to directly return the source {@link WatchEvent} by default. */
    interface DefaultWatchEvent extends WatchEvent<Path> { }

    private static final Class<? extends WatchEvent<Path>> CLASS = DefaultWatchEvent.class;

    private static Class<? extends WatchEvent<Path>> deepGetDefineEventClass(Class<? extends FileWatchListener> clazz) {
        List<Type> genericTypes = new ArrayList<>(Arrays.asList(clazz.getGenericInterfaces()));
        genericTypes.add(clazz.getGenericSuperclass());

        for (Type genericType : genericTypes) {

            if (genericType instanceof Class) {
                Class c = (Class) genericType;
                if (FileWatchListener.class.isAssignableFrom(c)) {
                    return deepGetDefineEventClass(c);
                }
            }
            else if (genericType instanceof ParameterizedType) {
                return (Class<? extends WatchEvent<Path>>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
            }
        }

        return CLASS;
    }

    private static WatchEvent<Path> instantiateDefineEvent(Class<? extends WatchEvent<Path>> defineEventClass,
                                              Path registeredPath, WatchEvent<Path> rawEvent) {
        Constructor<? extends WatchEvent<Path>> constructor;
        try {
            constructor
                    = defineEventClass.getConstructor(Path.class, WatchEvent.class);
            return constructor.newInstance(registeredPath, rawEvent);
        }
        catch (NoSuchMethodException e) {
            try {
                constructor = defineEventClass.getConstructor(WatchEvent.class);
                return constructor.newInstance(rawEvent);
            }
            catch (NoSuchMethodException ex) {
                throw new FileWatchException("Missing constructor (Path.class, WatchEvent.class) or " +
                        "(WatchEvent.class) in " + defineEventClass.getName(), ex);
            }
            catch (Exception ex) {
                throw new FileWatchException("Failed to instantiate " + defineEventClass.getName() + " by constructor" +
                        " (WatchEvent.class)", ex);
            }
        }
        catch (Exception ex) {
            throw new FileWatchException("Failed to instantiate " + defineEventClass.getName() + " by constructor" +
                    " (Path.class, WatchEvent.class)", ex);
        }
    }
}
