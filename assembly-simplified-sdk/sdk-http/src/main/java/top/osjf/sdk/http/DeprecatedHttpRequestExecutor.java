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

package top.osjf.sdk.http;

import top.osjf.sdk.core.util.SynchronizedWeakHashMap;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * The abstract implementation of outdated methods of {@link HttpRequestExecutor}, only kept unused.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class DeprecatedHttpRequestExecutor implements HttpRequestExecutor {
    private Class<?> toolClass;
    private static final Map<String, Method> cache = new SynchronizedWeakHashMap<>();
    public Class<?> getToolClass() {
        if (toolClass == null){
            toolClass = toolClass();
        }
        return toolClass;
    }
    protected abstract Class<?> toolClass();
    private String invokeStatic(String methodName, String url, Map<String, String> headers, Object param, boolean montage)
            throws Exception {
        Class<?> clazz = getToolClass();
        Method method = cache.computeIfAbsent(clazz.getName() + methodName, s -> {
            try {
                return clazz.getMethod(methodName, String.class, Map.class, Object.class, boolean.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
        return (String) method.invoke(null, url, headers, param, montage);
    }

    @Override
    public String get(String url, Map<String, String> headers, Object param, boolean montage) throws Exception {
        return invokeStatic("get", url, headers, param, montage);
    }

    @Override
    public String post(String url, Map<String, String> headers, Object param, boolean montage) throws Exception {
        return invokeStatic("post", url, headers, param, montage);
    }

    @Override
    public String put(String url, Map<String, String> headers, Object param, boolean montage) throws Exception {
        return invokeStatic("put", url, headers, param, montage);
    }

    @Override
    public String delete(String url, Map<String, String> headers, Object param, boolean montage) throws Exception {
        return invokeStatic("delete", url, headers, param, montage);
    }

    @Override
    public String trace(String url, Map<String, String> headers, Object param, boolean montage) throws Exception {
        return invokeStatic("trace", url, headers, param, montage);
    }

    @Override
    public String options(String url, Map<String, String> headers, Object param, boolean montage) throws Exception {
        return invokeStatic("get", url, headers, param, montage);
    }

    @Override
    public String head(String url, Map<String, String> headers, Object param, boolean montage) throws Exception {
        return invokeStatic("head", url, headers, param, montage);
    }

    @Override
    public String patch(String url, Map<String, String> headers, Object param, boolean montage) throws Exception {
        return invokeStatic("patch", url, headers, param, montage);
    }
}
