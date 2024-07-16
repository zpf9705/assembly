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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Fastjson2 utils from {@link JSON}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class JSONUtil {

    /**
     * (Comments from {@link JSON})
     * Serializes the specified object to the json string
     *
     * @param <T> Object types.
     * @param obj the specified object will be serialized
     * @return {@link String} that is not null
     * @throws JSONException If a serialization error occurs
     */
    public static <T> String toJSONString(T obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * (Comments from {@link JSON})
     * Parses the json string as a {@link JSONObject}. Returns {@code null}
     * if received {@link String} is {@code null} or empty or its content is null.
     *
     * @param jsonStr the specified string to be parsed
     * @return {@link JSONObject} or {@code null}
     * @throws JSONException If a parsing error occurs
     */
    public static JSONObject parseObject(String jsonStr) {
        return JSON.parseObject(jsonStr);
    }

    /**
     * Parses the json string as a {@link T} with a unKnow type.
     *
     * @param jsonStr      the specified string to be parsed
     * @param requiredType the specified type object.
     * @param <T>          Object type.
     * @return {@link T} or {@code null}
     * @throws JSONException If a parsing error occurs
     * @see #parseObject(String, Type)
     * @see #parseObject(String, Class)
     * @since 1.0.9
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseObject(String jsonStr, Object requiredType) {
        if (requiredType == null) {
            return null;
        }
        T result;
        if (requiredType instanceof Class) {
            result = parseObject(jsonStr, (Class<T>) requiredType);
        } else if (requiredType instanceof Type) {
            result = parseObject(jsonStr, (Type) requiredType);
        } else {
            result = null;
        }
        return result;
    }

    /**
     * (Comments from {@link JSON})
     * Parses the json string as {@link T}. Returns
     * {@code null} if received {@link String} is {@code null} or empty.
     *
     * @param jsonStr      the specified string to be parsed
     * @param requiredType the specified class of {@link T}
     * @param <T>          required Type.
     * @return {@link T} or {@code null}
     * @throws JSONException If a parsing error occurs
     * @since 1.0.9
     */
    public static <T> T parseObject(String jsonStr, Class<T> requiredType) {
        return JSON.parseObject(jsonStr, requiredType);
    }

    /**
     * (Comments from {@link JSON})
     * Parses the json string as {@link T}. Returns
     * {@code null} if received {@link String} is {@code null} or empty.
     *
     * @param jsonStr      the specified string to be parsed
     * @param requiredType the specified actual type of {@link T}
     * @param <T>          required Type.
     * @return {@link T} or {@code null}
     * @throws JSONException If a parsing error occurs
     * @since 1.0.9
     */
    public static <T> T parseObject(String jsonStr, Type requiredType) {
        return JSON.parseObject(jsonStr, requiredType);
    }

    /**
     * Parses the json array string as a {@link List} with a unKnow type.
     *
     * @param jsonStr      the specified string to be parsed
     * @param requiredType the specified type object.
     * @param <T>          Object type.
     * @return {@link List} or {@code null}
     * @throws JSONException If a parsing error occurs
     * @see #parseArray(String, Type)
     * @see #parseArray(String, Class)
     * @since 1.0.9
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> parseArray(String jsonStr, Object requiredType) {
        if (requiredType == null) {
            return null;
        }
        List<T> result;
        if (requiredType instanceof Class) {
            result = parseArray(jsonStr, (Class<T>) requiredType);
        } else if (requiredType instanceof Type) {
            result = parseArray(jsonStr, (Type) requiredType);
        } else {
            result = null;
        }
        return result;
    }

    /**
     * (Comments from {@link JSON})
     * Parses the json string as a list of {@link T}. Returns
     * {@code null} if received {@link String} is {@code null} or empty.
     *
     * @param jsonStr      the specified string to be parsed
     * @param requiredType the specified actual class of {@link T}
     * @param <T>          required Type.
     * @return {@link List} or {@code null}
     * @throws JSONException If a parsing error occurs
     * @since 1.0.9
     */
    public static <T> List<T> parseArray(String jsonStr, Class<T> requiredType) {
        return JSON.parseArray(jsonStr, requiredType);
    }

    /**
     * (Comments from {@link JSON})
     * Parses the json string as a list of {@link T}. Returns
     * {@code null} if received {@link String} is {@code null} or empty.
     *
     * @param jsonStr      the specified string to be parsed
     * @param requiredType the specified actual type of {@link T}
     * @param <T>          required Type.
     * @return {@link List} or {@code null}
     * @throws JSONException If a parsing error occurs
     * @since 1.0.9
     */
    public static <T> List<T> parseArray(String jsonStr, Type requiredType) {
        return JSON.parseArray(jsonStr, requiredType);
    }

    /**
     * (Comments from {@link JSON})
     * Represents a generic type {@code T}. Java doesn't yet provide a way to
     * represent generic types, so this class does. Forces clients to create a
     * subclass of this class which enables retrieval the type information even at runtime.
     * <p>
     * This syntax cannot be used to create type literals that have wildcard
     * parameters, such as {@code Class<T>} or {@code List<? extends CharSequence>}.
     * <p>
     * For example, to create a type literal for {@code List<String>}, you can
     * create an empty anonymous inner class:
     * <pre>{@code
     * TypeReference<List<String>> typeReference = new TypeReference<List<String>>(){};
     * }</pre>
     * For example, use it quickly
     * <pre>{@code String text = "{\"id\":1,\"name\":\"kraity\"}";
     * User user = new TypeReference<User>(){}.parseObject(text);
     * }</pre>
     *
     * @see TypeReference
     */
    public static class TypeReferences<T> extends TypeReference<T> {
    }

    /**
     * Verify that the json string is a legal JsonObject or JsonArray.
     *
     * @param jsonStr the specified string to be checked.
     * @return {@code true} or {@code false}.
     */
    //Standard JSON object format or JSON array format.
    public static boolean isValidObjectOrArray(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return false;
        }
        return isValidObject(jsonStr) || isValidArray(jsonStr);
    }

    /**
     * (Comments from {@link JSON})
     * Verify that the json string is a legal JsonObject
     *
     * @param jsonStr the specified string will be validated
     * @return {@code true} or {@code false}
     */
    //The standard JSON object format does not include values.
    public static boolean isValidObject(String jsonStr) {
        return JSON.isValidObject(jsonStr);
    }

    /**
     * (Comments from {@link JSON})
     * Verify the {@link String} is JSON Array
     *
     * @param jsonStr the {@link String} to validate
     * @return {@code true} or {@code false}
     */
    //Standard JSON array format.
    public static boolean isValidArray(String jsonStr) {
        return JSON.isValidArray(jsonStr);
    }

    /**
     * (Comments from {@link JSON})
     * Parses the json string as {@link T}. Returns
     * {@code null} if received {@link String} is {@code null} or empty.
     *
     * @param str        the specified string to be parsed
     * @param references the specified actual type
     * @param <T>        required Type.
     * @return {@link T} or {@code null}
     * @throws JSONException If a parsing error occurs
     */
    public static <T> T toObject(String str, TypeReferences<T> references) {
        return JSON.parseObject(str, references);
    }

    /**
     * Parses an empty {@link T} with unKnow type.
     *
     * @param <T>  required Type.
     * @param type the specified type object.
     * @return empty {@link T}.
     * @see #toEmptyObj(Class)
     * @see #toEmptyObj(Type)
     */
    public static <T> T toEmptyObj(Object type) {
        return parseObject("{}", type);
    }

    /**
     * Parses an empty {@link T} with specified Class.
     *
     * @param <T>   required Type.
     * @param clazz the specified clazz
     * @return empty {@link T}.
     * @since 1.0.9
     */
    public static <T> T toEmptyObj(Class<T> clazz) {
        return JSON.parseObject("{}", clazz);
    }

    /**
     * Parses an empty {@link T} with specified actual type.
     *
     * @param <T>  required Type.
     * @param type the specified actual type
     * @return empty {@link T}.
     * @since 1.0.9
     */
    public static <T> T toEmptyObj(Type type) {
        return JSON.parseObject("{}", type);
    }

    /**
     * Retrieve a {@link Map} from the JSON array, which inherits
     * the map itself, so return the JSON object itself.
     *
     * @param jsonStr the specified string to be parsed
     * @return extends of {@link JSON} in {@link java.util.LinkedHashMap}.
     */
    public static Map<String, Object> getInnerMapByJsonStr(String jsonStr) {
        if (isValidObject(jsonStr)) {
            return parseObject(jsonStr);
        }
        return null;
    }
}
