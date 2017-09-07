/*
 * Copyright 2011-2016 the original author or authors.
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
package com.lambdaworks.redis.sentinel;

import static com.lambdaworks.redis.TestSettings.hostAddr;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.lambdaworks.TestClientResources;
import com.lambdaworks.Wait;
import com.lambdaworks.redis.*;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.sentinel.api.async.RedisSentinelAsyncCommands;

/**
 * @author Mark Paluch
 */
public class SentinelCommandTest extends AbstractSentinelTest {

    @Rule
    public SentinelRule sentinelRule = new SentinelRule(sentinelClient, false, 26379, 26380);

    @BeforeClass
    public static void setupClient() {
        sentinelClient = RedisClient.create(TestClientResources.get(), RedisURI.Builder
                .sentinel(TestSettings.host(), MASTER_ID).build());
    }

    @Before
    public void openConnection() throws Exception {
        super.openConnection();

        try {
            sentinel.master(MASTER_ID);
        } catch (Exception e) {
            sentinelRule.monitor(MASTER_ID, hostAddr(), TestSettings.port(3), 1, true);
        }
    }

    @Test
    public void getMasterAddr() throws Exception {
        SocketAddress result = sentinel.getMasterAddrByName(MASTER_ID);
        InetSocketAddress socketAddress = (InetSocketAddress) result;
        assertThat(socketAddress.getHostName()).contains(TestSettings.host());
    }

    @Test
    public void getMasterAddrButNoMasterPresent() throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) sentinel.getMasterAddrByName("unknown");
        assertThat(socketAddress).isNull();
    }

    @Test
    public void getMasterAddrByName() throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) sentinel.getMasterAddrByName(MASTER_ID);
        assertThat(socketAddress.getPort()).isBetween(6479, 6485);
    }

    @Test
    public void masters() throws Exception {

        List<Map<String, String>> result = sentinel.masters();

        assertThat(result.size()).isGreaterThan(0);

        Map<String, String> map = result.get(0);
        assertThat(map.get("flags")).isNotNull();
        assertThat(map.get("config-epoch")).isNotNull();
        assertThat(map.get("port")).isNotNull();
    }

    @Test
    public void sentinelConnectWith() throws Exception {

        RedisClient client = RedisClient.create(TestClientResources.get(),
                RedisURI.Builder.sentinel(TestSettings.host(), 1234, MASTER_ID).withSentinel(TestSettings.host()).build());

        RedisSentinelAsyncCommands<String, String> sentinelConnection = client.connectSentinelAsync();
        assertThat(sentinelConnection.ping().get()).isEqualTo("PONG");

        sentinelConnection.close();

        RedisConnection<String, String> connection2 = client.connect().sync();
        assertThat(connection2.ping()).isEqualTo("PONG");
        connection2.quit();

        Wait.untilTrue(connection2::isOpen).waitOrTimeout();

        assertThat(connection2.ping()).isEqualTo("PONG");
        connection2.close();
        FastShutdown.shutdown(client);
    }

    @Test
    public void sentinelConnectWrongMaster() throws Exception {

        RedisClient client = RedisClient.create(TestClientResources.get(),
                RedisURI.Builder.sentinel(TestSettings.host(), 1234, "nonexistent").withSentinel(TestSettings.host()).build());
        try {
            client.connect();
            fail("missing RedisConnectionException");
        } catch (RedisConnectionException e) {
        }

        FastShutdown.shutdown(client);
    }

    @Test
    public void sentinelConnect() throws Exception {

        RedisClient client = RedisClient.create(TestClientResources.get(),
                RedisURI.Builder.redis(TestSettings.host(), TestSettings.port()).build());

        RedisSentinelAsyncCommands<String, String> connection = client.connectSentinelAsync();
        assertThat(connection.ping().get()).isEqualTo("PONG");

        connection.close();
        FastShutdown.shutdown(client);
    }

    @Test
    public void getMaster() throws Exception {

        Map<String, String> result = sentinel.master(MASTER_ID);
        assertThat(result.get("ip")).isEqualTo(hostAddr()); // !! IPv4/IPv6
        assertThat(result).containsKey("role-reported");
    }

    @Test
    public void role() throws Exception {

        RedisAsyncCommands<String, String> connection = sentinelClient.connect(RedisURI.Builder.redis(host, 26380).build())
                .async();
        try {

            RedisFuture<List<Object>> role = connection.role();
            List<Object> objects = role.get();

            assertThat(objects).hasSize(2);

            assertThat(objects.get(0)).isEqualTo("sentinel");
            assertThat(objects.get(1).toString()).isEqualTo("[" + MASTER_ID + "]");
        } finally {
            connection.close();
        }
    }

    @Test
    public void getSlaves() throws Exception {

        List<Map<String, String>> result = sentinel.slaves(MASTER_ID);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsKey("port");
    }

    @Test
    public void reset() throws Exception {

        Long result = sentinel.reset("other");
        assertThat(result.intValue()).isEqualTo(0);
    }

    @Test
    public void failover() throws Exception {

        try {
            sentinel.failover("other");
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("ERR No such master with that name");
        }
    }

    @Test
    public void monitor() throws Exception {

        try {
            sentinel.remove("mymaster2");
        } catch (Exception e) {
        }

        String result = sentinel.monitor("mymaster2", hostAddr(), 8989, 2);
        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void ping() throws Exception {

        String result = sentinel.ping();
        assertThat(result).isEqualTo("PONG");
    }

    @Test
    public void set() throws Exception {

        String result = sentinel.set(MASTER_ID, "down-after-milliseconds", "1000");
        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void connectToRedisUsingSentinel() throws Exception {
        RedisConnection<String, String> connect = sentinelClient.connect().sync();
        connect.ping();
        connect.close();
    }

    @Test
    public void connectToRedisUsingSentinelWithReconnect() throws Exception {
        RedisConnection<String, String> connect = sentinelClient.connect().sync();
        connect.ping();
        connect.quit();
        connect.ping();
        connect.close();
    }
}
