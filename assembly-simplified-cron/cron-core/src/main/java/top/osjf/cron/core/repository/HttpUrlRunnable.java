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


package top.osjf.cron.core.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.commons.util.Assert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Runnable implementation for executing HTTP callback tasks in cron scheduler.
 * <p>
 * Only support GET request, used to actively trigger external HTTP interface when cron task arrives.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class HttpUrlRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(HttpUrlRunnable.class);

    /** Supported http method, only GET for current version */
    private static final String ONLY_SUPPORT_HTTP_METHOD = "GET";

    /** Http header name constant for user-agent */
    private static final String HEADER_OF_USER_AGENT = "User-Agent";

    /** Http user agent header value */
    private static final String USER_AGENT = "cron-scheduler/3.0.2; (HttpUrlTask)";

    /** Default request connect timeout, milliseconds */
    private static final int CONNECT_TIMEOUT = 5000;

    /** Default request read timeout, milliseconds */
    private static final int READ_TIMEOUT = 8000;

    /** Target request url */
    private final String url;

    /** Request connect timeout (millisecond)  */
    private final int connectTimeout;

    /** Request read timeout (millisecond)  */
    private final int readTimeout;

    /**
     * Create {@link HttpUrlRunnable} with default timeout config.
     * @param url target request url
     */
    public HttpUrlRunnable(String url) {
        this(url, CONNECT_TIMEOUT, READ_TIMEOUT);
    }

    /**
     * Create {@link HttpUrlRunnable} with custom timeout config.
     *
     * @param url             target request url
     * @param connectTimeout  connect timeout in milliseconds
     * @param readTimeout     response read timeout in milliseconds
     * @throws IllegalArgumentException if url blank, invalid http/https protocol or timeout less
     * than or equal zero
     */
    public HttpUrlRunnable(String url, int connectTimeout, int readTimeout) {

        Assert.hasText(url, "Url must not be null or blank");
        String trimUrl = url.trim().toLowerCase();
        Assert.isTrue(trimUrl.startsWith("http://") || trimUrl.startsWith("https://"),
                "Url must start with http:// or https://");
        Assert.isTrue(connectTimeout > 0, "Connect timeout must greater than 0");
        Assert.isTrue(readTimeout > 0, "Read timeout must greater than 0");

        this.url = url;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * <strong>WARN: This built-in HTTP task will print the complete HTTP response body to the log,
     * it is strongly recommended that the URL accessed during task execution be lightweight and
     * return a URL that returns short content (such as boolean values, simple status text).
     * Avoid returning large HTML, JSON, or text addresses, as this may result in overSized logs
     * and additional memory overhead.</strong>
     */
    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL targetUrl = new URL(url);
            connection = (HttpURLConnection) targetUrl.openConnection();

            // Request configuration
            connection.setRequestMethod(ONLY_SUPPORT_HTTP_METHOD);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestProperty(HEADER_OF_USER_AGENT, USER_AGENT);
            // GET without request body, doOutput keep default false
            connection.setDoOutput(false);

            int responseCode = connection.getResponseCode();
            if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                // ❌ Read error stream
                InputStream errStream = connection.getErrorStream();
                reader = (errStream != null)
                        ? new BufferedReader(new InputStreamReader(errStream, StandardCharsets.UTF_8))
                        : new BufferedReader(new StringReader(""));
            } else {
                // ✅  Read successful response
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            }

            // Read response content
            StringBuilder responseSb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseSb.append(line);
            }

            logger.info("[HttpUrlRunnable] Request completed, url=[{}], code=[{}], response=[{}]",
                    url, responseCode, responseSb);
        }
        catch (IOException e) {
            logger.error("[HttpUrlRunnable] Request failed, url=[{}], message=[{}]", url, e.getMessage(), e);
        }
        finally {
            // Close resource
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
