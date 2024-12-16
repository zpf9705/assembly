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

package top.osjf.sdk.http.process;

import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.JSONUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@code JsonSequenceHttpRequestParams} is an abstract class that extends
 * from {@code AbstractHttpRequestParams} and is used to handle HTTP request
 * parameters that need to be serialized into JSON format.
 * <p>
 * <strong>The code usage example is as follows:</strong>
 * </p>
 * <pre>
 * {@code
 * public class ExampleJsonHttpRequestParams extends JsonSerialHttpRequestParams<...//Omitted here> {
 *     public HttpSdkEnum matchSdk() {
 *         ... //Omitted here.
 *     }
 *     public Object getParam() {
 *         return "Json" or new ExampleObj() or new HashMap<>();
 *     }
 * }}
 * </pre>
 *
 * @param <R> Subclass generic type of {@code AbstractHttpResponse}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class JsonSerialHttpRequestParams<R extends AbstractHttpResponse> extends AbstractHttpRequestParams<R> {

    private static final long serialVersionUID = -2384526879073656928L;

    /**
     * {@inheritDoc}
     * Return a JSON string parameter.
     *
     * @return a nullable json string.
     */
    @Override
    @Nullable
    public final String getRequestParam() {
        String json = null;
        Object param = getParam();
        if (param != null) {
            if (param instanceof String) {
                String paramStr = (String) param;
                if (JSONUtil.isValidObjectOrArray(paramStr)) {
                    json = paramStr;
                }
            } else json = JSONUtil.toJSONString(param);
        }
        return json;
    }

    /**
     * When the provided parameter {@link #getParam()} is not empty,
     * add {@code application/json} to the context of the request header.
     *
     * @return {@inheritDoc}
     */
    @Override
    public final Map<String, Object> getHeadMap() {
        Map<String, Object> jsonHeaders = null;
        if (getParam() != null) {
            jsonHeaders = new LinkedHashMap<>();
            jsonHeaders.put("Content-Type", "application/json");
        }
        return resolveAdditionalHeaders(jsonHeaders);
    }

    /**
     * Returns custom parameters that need to be serialized by JSON.
     * <p>
     * When the returned string is a JSON string, it will be used directly.
     * <p>
     * If it is of another type, an attempt will be made to perform JSON
     * serialization conversion.
     *
     * @return custom parameters,JSON string or other JSON serializable objects.
     */
    @Nullable
    public abstract Object getParam();
}
