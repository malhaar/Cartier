/*
 * Copyright 2011-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lambdaworks.redis;

import static com.lambdaworks.redis.RedisURI.Builder.redis;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.lambdaworks.redis.api.StatefulConnection;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.codec.Utf8StringCodec;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import com.lambdaworks.redis.sentinel.api.StatefulRedisSentinelConnection;

/**
 * @author Mark Paluch
 * @author Jongyeol Choi
 */
public class RedisClientConnectionTest extends AbstractRedisClientTest {

    public static final Utf8StringCodec CODEC = new Utf8StringCodec();
    public static final int EXPECTED_TIMEOUT = 500;
    public static final TimeUnit EXPECTED_TIME_UNIT = TimeUnit.MILLISECONDS;

    @Before
    public void before() throws Exception {
        client.setDefaultTimeout(EXPECTED_TIMEOUT, EXPECTED_TIME_UNIT);
    }

    /*
     * Pool/Sync
     */
    @Test
    public void poolClientUri() throws Exception {
        client.pool().close();
    }

    @Test
    public void poolClientUriConfig() throws Exception {
        client.pool(1, 1).close();
    }

    @Test
    public void poolCodecClientUriConfig() throws Exception {
        client.pool(CODEC, 1, 1).close();
    }

    /*
     * Pool/Async
     */
    @Test
    public void asyncPoolClientUri() throws Exception {
        client.asyncPool().close();
    }

    @Test
    public void asyncPoolClientUriConfig() throws Exception {
        client.asyncPool(1, 1).close();
    }

    @Test
    public void asyncPoolCodecClientUriConfig() throws Exception {
        client.asyncPool(CODEC, 1, 1).close();
    }

    /*
     * Standalone/Stateful
     */
    @Test
    public void connectClientUri() throws Exception {

        StatefulRedisConnection<String, String> connection = client.connect();
        assertTimeout(connection, EXPECTED_TIMEOUT, EXPECTED_TIME_UNIT);
        connection.close();
    }

    @Test
    public void connectCodecClientUri() throws Exception {
        StatefulRedisConnection<String, String> connection = client.connect(CODEC);
        assertTimeout(connection, EXPECTED_TIMEOUT, EXPECTED_TIME_UNIT);
        connection.close();
    }

    @Test
    public void connectOwnUri() throws Exception {
        RedisURI redisURI = redis(host, port).build();
        StatefulRedisConnection<String, String> connection = client.connect(redisURI);
        assertTimeout(connection, redisURI.getTimeout(), redisURI.getUnit());
        connection.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectMissingHostAndSocketUri() throws Exception {
        client.connect(new RedisURI());
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectSentinelMissingHostAndSocketUri() throws Exception {
        client.connect(invalidSentinel());
    }

    @Test
    public void connectCodecOwnUri() throws Exception {
        RedisURI redisURI = redis(host, port).build();
        StatefulRedisConnection<String, String> connection = client.connect(CODEC, redisURI);
        assertTimeout(connection, redisURI.getTimeout(), redisURI.getUnit());
        connection.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectCodecMissingHostAndSocketUri() throws Exception {
        client.connect(CODEC, new RedisURI());
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectcodecSentinelMissingHostAndSocketUri() throws Exception {
        client.connect(CODEC, invalidSentinel());
    }

    @Test(expected = TimeoutException.class)
    @Ignore("Non-deterministic behavior. Can cause a deadlock")
    public void shutdownSyncInRedisFutureTest() throws Exception {

        RedisClient redisClient = RedisClient.create();
        StatefulRedisConnection<String, String> connection = redisClient.connect(redis(host, port).build());

        CompletableFuture<String> f = connection.async().get("key1").whenComplete((result, e) -> {
            connection.close();
            redisClient.shutdown(0, 0, TimeUnit.SECONDS); // deadlock expected.
            }).toCompletableFuture();

        f.get(1, TimeUnit.SECONDS);
    }

    @Test
    public void shutdownAsyncInRedisFutureTest() throws Exception {

        RedisClient redisClient = RedisClient.create();
        StatefulRedisConnection<String, String> connection = redisClient.connect(redis(host, port).build());
        CompletableFuture<Void> f = connection.async().get("key1").thenCompose(result -> {
            connection.close();
            return redisClient.shutdownAsync(0, 0, TimeUnit.SECONDS);
        }).toCompletableFuture();

        f.get(5, TimeUnit.SECONDS);
    }

    /*
     * Deprecated: Standalone/Async
     */
    @Test
    public void connectAsyncClientUri() throws Exception {
        client.connectAsync().close();
    }

    @Test
    public void connectAsyncCodecClientUri() throws Exception {
        client.connectAsync(CODEC).close();
    }

    @Test
    public void connectAsyncOwnUri() throws Exception {
        client.connectAsync(redis(host, port).build()).close();
    }

    @Test
    public void connectAsyncCodecOwnUri() throws Exception {
        client.connectAsync(CODEC, redis(host, port).build()).close();
    }

    /*
     * Standalone/PubSub Stateful
     */
    @Test
    public void connectPubSubClientUri() throws Exception {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        assertTimeout(connection, EXPECTED_TIMEOUT, EXPECTED_TIME_UNIT);
        connection.close();
    }

    @Test
    public void connectPubSubCodecClientUri() throws Exception {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub(CODEC);
        assertTimeout(connection, EXPECTED_TIMEOUT, EXPECTED_TIME_UNIT);
        connection.close();
    }

    @Test
    public void connectPubSubOwnUri() throws Exception {
        RedisURI redisURI = redis(host, port).build();
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub(redisURI);
        assertTimeout(connection, redisURI.getTimeout(), redisURI.getUnit());
        connection.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectPubSubMissingHostAndSocketUri() throws Exception {
        client.connectPubSub(new RedisURI());
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectPubSubSentinelMissingHostAndSocketUri() throws Exception {
        client.connectPubSub(invalidSentinel());
    }

    @Test
    public void connectPubSubCodecOwnUri() throws Exception {
        RedisURI redisURI = redis(host, port).build();
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub(CODEC, redisURI);
        assertTimeout(connection, redisURI.getTimeout(), redisURI.getUnit());
        connection.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectPubSubCodecMissingHostAndSocketUri() throws Exception {
        client.connectPubSub(CODEC, new RedisURI());
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectPubSubCodecSentinelMissingHostAndSocketUri() throws Exception {
        client.connectPubSub(CODEC, invalidSentinel());
    }

    /*
     * Sentinel Stateful
     */
    @Test
    public void connectSentinelClientUri() throws Exception {
        StatefulRedisSentinelConnection<String, String> connection = client.connectSentinel();
        assertTimeout(connection, EXPECTED_TIMEOUT, EXPECTED_TIME_UNIT);
        connection.close();
    }

    @Test
    public void connectSentinelCodecClientUri() throws Exception {
        StatefulRedisSentinelConnection<String, String> connection = client.connectSentinel(CODEC);
        assertTimeout(connection, EXPECTED_TIMEOUT, EXPECTED_TIME_UNIT);
        connection.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectSentinelAndMissingHostAndSocketUri() throws Exception {
        client.connectSentinel(new RedisURI());
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectSentinelSentinelMissingHostAndSocketUri() throws Exception {
        client.connectSentinel(invalidSentinel());
    }

    @Test
    public void connectSentinelOwnUri() throws Exception {
        RedisURI redisURI = redis(host, port).build();
        StatefulRedisSentinelConnection<String, String> connection = client.connectSentinel(redisURI);
        assertTimeout(connection, redisURI.getTimeout(), redisURI.getUnit());
        connection.close();
    }

    @Test
    public void connectSentinelCodecOwnUri() throws Exception {
        RedisURI redisURI = redis(host, port).build();
        StatefulRedisSentinelConnection<String, String> connection = client.connectSentinel(CODEC, redisURI);
        assertTimeout(connection, redisURI.getTimeout(), redisURI.getUnit());
        connection.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectSentinelCodecMissingHostAndSocketUri() throws Exception {
        client.connectSentinel(CODEC, new RedisURI());
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectSentinelCodecSentinelMissingHostAndSocketUri() throws Exception {
        client.connectSentinel(CODEC, invalidSentinel());
    }

    /*
     * Deprecated: Sentinel/Async
     */
    @Test
    public void connectSentinelAsyncClientUri() throws Exception {
        client.connectSentinelAsync().close();
    }

    @Test
    public void connectSentinelAsyncCodecClientUri() throws Exception {
        client.connectSentinelAsync(CODEC).close();
    }

    @Test
    public void connectSentineAsynclOwnUri() throws Exception {
        client.connectSentinelAsync(redis(host, port).build()).close();
    }

    @Test
    public void connectSentinelAsyncCodecOwnUri() throws Exception {
        client.connectSentinelAsync(CODEC, redis(host, port).build()).close();
    }

    private RedisURI invalidSentinel() {
        RedisURI redisURI = new RedisURI();
        redisURI.getSentinels().add(new RedisURI());

        return redisURI;
    }

    private void assertTimeout(StatefulConnection<?, ?> connection, long expectedTimeout, TimeUnit expectedTimeUnit) {

        assertThat(ReflectionTestUtils.getField(connection, "timeout")).isEqualTo(expectedTimeout);
        assertThat(ReflectionTestUtils.getField(connection, "unit")).isEqualTo(expectedTimeUnit);
    }
}
