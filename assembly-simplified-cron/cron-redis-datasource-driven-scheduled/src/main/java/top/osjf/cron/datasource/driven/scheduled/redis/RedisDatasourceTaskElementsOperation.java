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


package top.osjf.cron.datasource.driven.scheduled.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SslOptions;
import io.lettuce.core.api.sync.BaseRedisCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.util.AssertUtils;
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.datasource.driven.scheduled.redis.config.RedisConnectionConfig;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigFormat;
import top.osjf.cron.datasource.driven.scheduled.serialization.remote.RemoteDatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.serialization.remote.RemoteListener;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: This file has been copied and slightly modified from {com.alibaba.csp.sentinel.datasource.redis}.
 * <h2>Redis Datasource Task Elements Operation</h2>
 *
 * This class is a concrete implementation of a remote configuration data source,
 * built on top of Redis's high-performance read/write capabilities and Pub/Sub real-time
 * messaging mechanism. It enables dynamic loading, listening, and publishing of rule or
 * configuration data with low latency and strong consistency.
 *
 * <p>The primary design objectives are:</p>
 *
 * <ul>
 *   <li><b>Real-time Updates</b>: Subscribes to a designated Redis Pub/Sub channel to detect
 *       configuration changes immediately and trigger hot-reload events.</li>
 *   <li><b>Deployment Flexibility</b>: Supports multiple Redis deployment modes including
 *       standalone, Sentinel (for high availability), and Redis Cluster (for horizontal scaling).</li>
 *   <li><b>Security Support</b>: Provides full SSL/TLS encryption support with compatibility for
 *       both JKS and PEM certificate formats, meeting production security requirements.</li>
 *   <li><b>Extensibility</b>: Extends {@link RemoteDatasourceTaskElementsOperation}, adhering to a unified
 *       datasource abstraction contract. Can be seamlessly integrated into systems requiring
 *       runtime reconfiguration, such as rule engines, policy managers, or distributed gateways.</li>
 * </ul>
 *
 * <h3>Working Principle</h3>
 * <p>During initialization, the class automatically selects the appropriate client based on
 * the provided {@link RedisConnectionConfig}:
 * <ul>
 *   <li>If cluster nodes are configured, it creates a {@link RedisClusterClient}.</li>
 *   <li>Otherwise, it uses a {@link RedisClient} for standalone or Sentinel mode connections.</li>
 * </ul>
 *
 * Additionally, it sets up a "lazy-loaded" subscription via {@link #setLazyListener(java.util.function.Supplier)} —
 * meaning the actual Pub/Sub connection and subscription occur only when an external system
 * explicitly requests listening (e.g., during startup of a configuration watcher).
 * This lazy initialization strategy reduces resource consumption and improves application startup time.
 *
 * <h3>Key Method Overview</h3>
 * <table border="1" cellpadding="8" summary="Description of key methods in RedisDatasourceTaskElementsOperation">
 *   <tr>
 *     <th>Method</th>
 *     <th>Purpose</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #getRemoteConfigInfo()}</td>
 *     <td>Retrieves the current configuration value associated with {@code ruleKey} from Redis (via GET)</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #publishConfig(String)}</td>
 *     <td>Publishes new configuration data to Redis (via SET), typically used to broadcast updates</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #subscribeFromChannel(String)}</td>
 *     <td>Establishes a Pub/Sub connection and subscribes to a specific channel; incoming messages
 *         are dispatched through {@link DelegatingRedisPubSubListener}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #close()}</td>
 *     <td>Gracefully shuts down the Redis client and releases all underlying resources</td>
 *   </tr>
 * </table>
 *
 * <h3>Typical Use Cases</h3>
 * <ul>
 *   <li>Dynamic rule loading in rule engines (e.g., Drools, Easy Rules)</li>
 *   <li>Hot-reloading of distributed configurations in microservices</li>
 *   <li>Real-time policy or permission updates in access control systems</li>
 *   <li>Centralized rate-limiting or circuit-breaker configuration management</li>
 * </ul>
 * <h3>Example Usage</h3>
 * <pre>{@code
 * RedisConnectionConfig config = new RedisConnectionConfig();
 * config.setHost("localhost");
 * config.setPort(6379);
 * config.setTimeout(5000);
 *
 * RedisDatasourceTaskElementsOperation redisSource =
 *     new RedisDatasourceTaskElementsOperation(config, "rule:flow", "channel:rule-update", ConfigFormat.JSON);
 *
 * // Retrieve current configuration
 * String currentConfig = redisSource.getRemoteConfigInfo();
 *
 * // Start listening (triggers lazy subscription)
 * redisSource.notifyMainTaskInfoNotProvidedAndNoDefaultUsed();
 *
 * // Publish new config (other instances will receive notification)
 * redisSource.publishConfig("{\"rateLimit\": 1000}");
 *
 * // Clean up resources
 * redisSource.close();
 * }</pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class RedisDatasourceTaskElementsOperation extends RemoteDatasourceTaskElementsOperation {

    private final RedisClient redisClient;

    private final RedisClusterClient redisClusterClient;

    private final String ruleKey;

    private final String channel;

    /**
     * Initializes a Redis-based datasource with connection config, data key, subscription channel,
     * and configuration format. Sets up either standalone/sentinel or cluster client based on config.
     *
     * @param connectionConfig Redis connection configuration, must not be null.
     * @param ruleKey          The key used to store data in Redis, must not be blank.
     * @param channel          The channel name for subscribing to update notifications via Pub/Sub, must not be blank.
     * @param configFormat     Format of the configuration (e.g., JSON, YAML), must not be null.
     */
    public RedisDatasourceTaskElementsOperation(RedisConnectionConfig connectionConfig, String ruleKey,
                                                String channel, ConfigFormat configFormat) {
        super(configFormat);
        AssertUtils.assertNotNull(connectionConfig, "Redis connection config can not be null");
        AssertUtils.assertNotBlank(ruleKey, "Redis ruleKey can not be empty");
        AssertUtils.assertNotBlank(channel, "Redis subscribe channel can not be empty");
        if (connectionConfig.getRedisClusters().isEmpty()) {
            this.redisClient = getRedisClient(connectionConfig);
            this.redisClusterClient = null;
        } else {
            this.redisClusterClient = getRedisClusterClient(connectionConfig);
            this.redisClient = null;
        }
        this.ruleKey = ruleKey;
        this.channel = channel;
        setLazyListener(() -> subscribeFromChannel(channel));
    }

    /**
     * init SslOptions, support jks or pem format
     *
     * @param connectionConfig Redis connection config
     * @return a new SslOptions
     */
    private SslOptions initSslOptions(RedisConnectionConfig connectionConfig) {
        if (!connectionConfig.isSslEnable()){
            return null;
        }

        SslOptions.Builder sslOptionsBuilder = SslOptions.builder();

        if (connectionConfig.getTrustedCertificatesPath() != null){
            if (connectionConfig.getTrustedCertificatesPath().endsWith(".jks")){
                // if the value is end with .jks，think it is java key store format，to invoke truststore method
                sslOptionsBuilder.truststore(
                        new File(connectionConfig.getTrustedCertificatesPath()),
                        connectionConfig.getTrustedCertificatesJksPassword()
                );
            } else {
                // if the value is not end with .jks，think it is pem format，to invoke trustManager method
                sslOptionsBuilder.trustManager(new File(connectionConfig.getTrustedCertificatesPath()));
            }
        }

        if (connectionConfig.getKeyCertChainFilePath() != null || connectionConfig.getKeyFilePath() != null) {
            if (connectionConfig.getKeyFilePath().endsWith(".jks")){
                sslOptionsBuilder.keystore(
                        new File(connectionConfig.getKeyCertChainFilePath()),
                        connectionConfig.getKeyFilePassword() == null ? null : connectionConfig.getKeyFilePassword().toCharArray()
                );
            } else {
                sslOptionsBuilder.keyManager(
                        new File(connectionConfig.getKeyCertChainFilePath()),
                        new File(connectionConfig.getKeyFilePath()),
                        connectionConfig.getKeyFilePassword() == null ? null : connectionConfig.getKeyFilePassword().toCharArray()
                );
            }
        }
        return sslOptionsBuilder.build();
    }

    /**
     * Build Redis client fromm {@code RedisConnectionConfig}.
     *
     * @return a new {@link RedisClient}
     */
    private RedisClient getRedisClient(RedisConnectionConfig connectionConfig) {
        RedisClient redisClient;
        if (connectionConfig.getRedisSentinels().isEmpty()) {
            logger.info("[RedisDataSource] Creating stand-alone mode Redis client");
            redisClient = getRedisStandaloneClient(connectionConfig);
        } else {
            logger.info("[RedisDataSource] Creating Redis Sentinel mode Redis client");
            redisClient = getRedisSentinelClient(connectionConfig);
        }
        SslOptions sslOptions = initSslOptions(connectionConfig);
        if (sslOptions != null){
            redisClient.setOptions(
                    ClusterClientOptions.builder().sslOptions(sslOptions).build()
            );
        }
        return redisClient;
    }

    private RedisClusterClient getRedisClusterClient(RedisConnectionConfig connectionConfig) {
        char[] password = connectionConfig.getPassword();
        String clientName = connectionConfig.getClientName();

        //If any uri is successful for connection, the others are not tried anymore
        List<RedisURI> redisUris = new ArrayList<>();
        for (RedisConnectionConfig config : connectionConfig.getRedisClusters()) {
            RedisURI.Builder clusterRedisUriBuilder = RedisURI.builder();
            clusterRedisUriBuilder.withHost(config.getHost())
                    .withPort(config.getPort())
                    .withSsl(config.isSslEnable())
                    .withTimeout(Duration.ofMillis(connectionConfig.getTimeout()));
            //All redis nodes must have same password
            if (password != null) {
                clusterRedisUriBuilder.withPassword(connectionConfig.getPassword());
            }
            redisUris.add(clusterRedisUriBuilder.build());
        }
        RedisClusterClient redisClusterClient =  RedisClusterClient.create(redisUris);
        SslOptions sslOptions = initSslOptions(connectionConfig);
        if (sslOptions != null){
            redisClusterClient.setOptions(
                    ClusterClientOptions.builder().sslOptions(sslOptions).build()
            );
        }
        return redisClusterClient;
    }


    private RedisClient getRedisStandaloneClient(RedisConnectionConfig connectionConfig) {
        char[] password = connectionConfig.getPassword();
        String clientName = connectionConfig.getClientName();
        RedisURI.Builder redisUriBuilder = RedisURI.builder();
        redisUriBuilder.withHost(connectionConfig.getHost())
                .withPort(connectionConfig.getPort())
                .withDatabase(connectionConfig.getDatabase())
                .withSsl(connectionConfig.isSslEnable())
                .withTimeout(Duration.ofMillis(connectionConfig.getTimeout()));
        if (password != null) {
            redisUriBuilder.withPassword(connectionConfig.getPassword());
        }
        if (!StringUtils.isBlank(clientName)) {
            redisUriBuilder.withClientName(clientName);
        }
        return RedisClient.create(redisUriBuilder.build());
    }

    private RedisClient getRedisSentinelClient(RedisConnectionConfig connectionConfig) {
        char[] password = connectionConfig.getPassword();
        String clientName = connectionConfig.getClientName();
        RedisURI.Builder sentinelRedisUriBuilder = RedisURI.builder();
        for (RedisConnectionConfig config : connectionConfig.getRedisSentinels()) {
            sentinelRedisUriBuilder.withSentinel(config.getHost(), config.getPort());
        }
        if (password != null) {
            sentinelRedisUriBuilder.withPassword(connectionConfig.getPassword());
        }
        if (!StringUtils.isBlank(clientName)) {
            sentinelRedisUriBuilder.withClientName(clientName);
        }
        sentinelRedisUriBuilder.withSentinelMasterId(connectionConfig.getRedisSentinelMasterId())
                .withSsl(connectionConfig.isSslEnable())
                .withTimeout(Duration.ofMillis(connectionConfig.getTimeout()));
        return RedisClient.create(sentinelRedisUriBuilder.build());
    }

    private DelegatingRedisPubSubListener subscribeFromChannel(String channel) {
        DelegatingRedisPubSubListener adapterListener = new DelegatingRedisPubSubListener(this);
        if (redisClient != null) {
            StatefulRedisPubSubConnection<String, String> pubSubConnection = redisClient.connectPubSub();
            pubSubConnection.addListener(adapterListener);
            RedisPubSubCommands<String, String> sync = pubSubConnection.sync();
            sync.subscribe(channel);
        } else {
            StatefulRedisClusterPubSubConnection<String, String> pubSubConnection = redisClusterClient.connectPubSub();
            pubSubConnection.addListener(adapterListener);
            RedisPubSubCommands<String, String> sync = pubSubConnection.sync();
            sync.subscribe(channel);
        }
        return adapterListener;
    }

    @Override
    @NotNull
    protected String getRemoteConfigInfo() {
        checkInitialized();

        if (redisClient != null) {
            RedisCommands<String, String> stringRedisCommands = redisClient.connect().sync();
            return stringRedisCommands.get(ruleKey);
        } else {
            RedisAdvancedClusterCommands<String, String> stringRedisCommands = redisClusterClient.connect().sync();
            return stringRedisCommands.get(ruleKey);
        }
    }

    @Override
    protected void publishConfig(@NotNull String configInfo) {
        checkInitialized();

        BaseRedisCommands<String,String> baseRedisCommands;

        if (redisClient != null) {
            RedisCommands<String, String> stringRedisCommands = redisClient.connect().sync();
            stringRedisCommands.set(ruleKey, configInfo);
            baseRedisCommands = stringRedisCommands;
        } else {
            RedisAdvancedClusterCommands<String, String> stringClusterRedisCommands = redisClusterClient.connect().sync();
            stringClusterRedisCommands.set(ruleKey, configInfo);
            baseRedisCommands = stringClusterRedisCommands;
        }

        baseRedisCommands.publish(channel, configInfo);
    }

    private void checkInitialized() {
        if (this.redisClient == null && this.redisClusterClient == null) {
            throw new IllegalStateException("Redis client or Redis Cluster client has not been initialized or error occurred");
        }
    }

    @Override
    public void close() {
        if (redisClient != null) {
            redisClient.shutdown();
        } else {
            redisClusterClient.shutdown();
        }
    }

    /**
     * <h2>Delegating Redis Pub/Sub Listener</h2>
     *
     * An inner static class that bridges Lettuce's Redis Pub/Sub events with the system's
     * dynamic configuration update mechanism. It implements {@link RedisPubSubListener}
     * to listen for string-based messages and delegates received configuration updates
     * to the outer operation class ({@link RedisDatasourceTaskElementsOperation}) for hot-reload.
     *
     * <p><b>Key Responsibilities:</b></p>
     * <ul>
     *   <li>Listens to configuration change messages on a specific channel;</li>
     *   <li>Logs incoming messages and triggers the refresh pipeline;</li>
     *   <li>Acts as an adapter layer, decoupling Lettuce's low-level communication from business logic.</li>
     * </ul>
     *
     * <p>Note: Only simple subscription (SUBSCRIBE) is handled; pattern-based subscription (PSUBSCRIBE)
     *       callbacks are left unimplemented.</p>
     *
     * <p><b>Typical Flow:</b></p>
     * <pre>
     * Redis Set and Publish → message(channel, message) → refresh(message) → Configuration Reload
     * </pre>
     * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
     * @since 3.0.2
     */
    private static class DelegatingRedisPubSubListener extends RemoteListener implements RedisPubSubListener<String, String> {

        public DelegatingRedisPubSubListener(RedisDatasourceTaskElementsOperation remoteOperation) {
            super(remoteOperation);
        }

        @Override
        public void message(String channel, String message) {
            logger.info("[DelegatingRedisPubSubListener] New property value received for channel {}: {}", channel, message);
            refresh(message);
        }

        @Override
        public void message(String pattern, String channel, String message) {

        }

        @Override
        public void subscribed(String channel, long count) {

        }

        @Override
        public void psubscribed(String pattern, long count) {

        }

        @Override
        public void unsubscribed(String channel, long count) {

        }

        @Override
        public void punsubscribed(String pattern, long count) {

        }
    }
}
