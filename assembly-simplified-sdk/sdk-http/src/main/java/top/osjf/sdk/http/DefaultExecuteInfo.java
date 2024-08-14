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

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * The default implementation class for the HTTP execution result
 * collection interface {@link HttpResultSolver.ExecuteInfo}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class DefaultExecuteInfo implements HttpResultSolver.ExecuteInfo, Serializable {

    private static final long serialVersionUID = -3011908612537080908L;

    private final long spendTotalTimeMillis;

    private final Throwable error;

    private final HttpRequest<?> httpRequest;

    private final String response;

    public DefaultExecuteInfo(long spendTotalTimeMillis, Throwable error, HttpRequest<?> httpRequest, String response) {
        this.spendTotalTimeMillis = spendTotalTimeMillis;
        this.error = error;
        this.httpRequest = httpRequest;
        this.response = response;
    }

    @Override
    public long getSpendTotalTimeMillis() {
        return spendTotalTimeMillis;
    }

    @Override
    public Supplier<Boolean> noHappenError() {
        return () -> error == null;
    }

    @Override
    public HttpRequest<?> getHttpRequest() {
        return httpRequest;
    }

    @Override
    public String getResponse() {
        return response;
    }

    @Override
    public String getErrorMessage() {
        return error == null ? null : error.getMessage();
    }
}
