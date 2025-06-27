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


package top.osjf.optimize.idempotent.aspectj;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;

/**
 * Class of Json Serializer.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class JSONSerializer {
    /**
     * Serializes the specified object to the json string
     *
     * @param object the specified object will be serialized
     * @return {@link String} that is not null
     * @throws JSONException If a serialization error occurs
     */
    public String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }
}
