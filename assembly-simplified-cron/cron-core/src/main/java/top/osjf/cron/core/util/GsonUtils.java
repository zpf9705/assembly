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


package top.osjf.cron.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A simple JSON serialization utility class for {@link Gson}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class GsonUtils {
    static Gson defatultGson;

    static {
        defatultGson = new GsonBuilder().create();
    }

    /**
     * Serialize the given object into a JSON string.
     *
     * @param obj the object to be serialized as JSON.
     * @return Provide the JSON string after object serialization.
     */
    public static String toJson(Object obj) {
        return toJson(null, obj);
    }

    /**
     * Serialize the given object into a JSON string.
     *
     * <p>Users can provide a {@link Gson} to support this method function.
     *
     * @param gson the provider {@code Gson}.
     * @param obj  the object to be serialized as JSON.
     * @return Provide the JSON string after object serialization.
     */
    public static String toJson(Gson gson, Object obj) {
        if (obj == null) {
            return "";
        }
        return getAvailableGson(gson).toJson(obj);
    }

    /**
     * Deserialize the given JSON string into an object of a specified type.
     *
     * @param json the json data awaiting entity conversion.
     * @param type the convert type of class object.
     * @param <T>  the convert generic of class object.
     * @return Convert JSON string to an instance of the specified type.
     */
    public static <T> T fromJson(String json, Class<T> type) {
        return fromJson(null, json, type);
    }

    /**
     * Deserialize the given JSON string into an object of a specified type.
     *
     * <p>Users can provide a {@link Gson} to support this method function.
     *
     * @param gson the provider {@code Gson}.
     * @param json the json data awaiting entity conversion.
     * @param type the convert type of class object.
     * @param <T>  the convert generic of class object.
     * @return Convert JSON string to an instance of the specified type.
     */
    public static <T> T fromJson(Gson gson, String json, Class<T> type) {
        if (json == null || type == null) {
            return null;
        }
        return getAvailableGson(gson).fromJson(json, type);
    }

    private static Gson getAvailableGson(Gson gson) {
        return gson != null ? gson : defatultGson;
    }
}
