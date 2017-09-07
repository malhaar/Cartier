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
package com.lambdaworks.redis;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;
import com.lambdaworks.redis.cluster.api.async.AsyncNodeSelection;

/**
 * @author Mark Paluch
 */
public class AllTheAPIsTest {

    private static RedisClient redisClient = DefaultRedisClient.get();
    private static RedisClusterClient clusterClient;
    private static int clusterPort;

    @BeforeClass
    public static void beforeClass() throws Exception {
        clusterPort = TestSettings.port(900);
        clusterClient = RedisClusterClient.create(RedisURI.Builder.redis(TestSettings.host(), clusterPort).build());
    }

    @BeforeClass
    public static void afterClass() throws Exception {
        if (clusterClient != null) {
            FastShutdown.shutdown(clusterClient);
        }
    }

    // Standalone
    @Test
    public void standaloneSync() throws Exception {
        redisClient.connect().close();
    }

    @Test
    public void standaloneAsync() throws Exception {
        redisClient.connect().async().close();
    }

    @Test
    public void standaloneReactive() throws Exception {
        redisClient.connect().reactive().close();
    }

    @Test
    public void standaloneStateful() throws Exception {
        redisClient.connect().close();
    }

    @Test
    public void deprecatedStandaloneAsync() throws Exception {
        redisClient.connectAsync().close();
    }

    @Test
    public void deprecatedStandaloneReactive() throws Exception {
        redisClient.connectAsync().getStatefulConnection().reactive().close();
    }

    @Test
    public void deprecatedStandaloneStateful() throws Exception {
        redisClient.connectAsync().getStatefulConnection().close();
    }

    // PubSub
    @Test
    public void pubsubSync() throws Exception {
        redisClient.connectPubSub().close();
    }

    @Test
    public void pubsubAsync() throws Exception {
        redisClient.connectPubSub().close();
    }

    @Test
    public void pubsubReactive() throws Exception {
        redisClient.connectPubSub().close();
    }

    @Test
    public void pubsubStateful() throws Exception {
        redisClient.connectPubSub().close();
    }

    // Sentinel
    @Test
    public void sentinelSync() throws Exception {
        redisClient.connectSentinel().sync().close();
    }

    @Test
    public void sentinelAsync() throws Exception {
        redisClient.connectSentinel().async().close();
    }

    @Test
    public void sentinelReactive() throws Exception {
        redisClient.connectSentinel().reactive().close();
    }

    @Test
    public void sentinelStateful() throws Exception {
        redisClient.connectSentinel().close();
    }

    @Test
    public void deprecatedSentinelSync() throws Exception {
        redisClient.connectSentinelAsync().getStatefulConnection().sync().close();
    }

    @Test
    public void deprecatedSentinelAsync() throws Exception {
        redisClient.connectSentinelAsync().getStatefulConnection().async().close();
    }

    @Test
    public void deprecatedSentinelReactive() throws Exception {
        redisClient.connectSentinelAsync().getStatefulConnection().reactive().close();
    }

    @Test
    public void deprecatedSentinelStateful() throws Exception {
        redisClient.connectSentinelAsync().getStatefulConnection().close();
    }

    // Pool
    @Test
    public void poolSync() throws Exception {
        redisClient.pool().close();
    }

    @Test
    public void poolAsync() throws Exception {
        redisClient.asyncPool().close();
    }

    // Cluster
    @Test
    public void clusterSync() throws Exception {
        clusterClient.connect().sync().close();
    }

    @Test
    public void clusterAsync() throws Exception {
        clusterClient.connect().async().close();
    }

    @Test
    public void clusterReactive() throws Exception {
        clusterClient.connect().reactive().close();
    }

    @Test
    public void clusterStateful() throws Exception {
        clusterClient.connect().close();
    }

    @Test
    public void clusterPubSubSync() throws Exception {
        clusterClient.connectPubSub().sync().close();
    }

    @Test
    public void clusterPubSubAsync() throws Exception {
        clusterClient.connectPubSub().async().close();
    }

    @Test
    public void clusterPubSubReactive() throws Exception {
        clusterClient.connectPubSub().reactive().close();
    }

    @Test
    public void clusterPubSubStateful() throws Exception {
        clusterClient.connectPubSub().close();
    }

    @Test
    public void deprecatedClusterSync() throws Exception {
        clusterClient.connectCluster().getStatefulConnection().sync().close();
    }

    @Test
    public void deprecatedClusterAsync() throws Exception {
        clusterClient.connectCluster().getStatefulConnection().async().close();
    }

    @Test
    public void deprecatedClusterReactive() throws Exception {
        clusterClient.connectCluster().getStatefulConnection().reactive().close();
    }

    @Test
    public void deprecatedClusterStateful() throws Exception {
        clusterClient.connectCluster().getStatefulConnection().close();
    }

    // Advanced Cluster
    @Test
    public void advancedClusterSync() throws Exception {
        StatefulRedisClusterConnection<String, String> statefulConnection = clusterClient.connectCluster()
                .getStatefulConnection();
        RedisURI uri = clusterClient.getPartitions().getPartition(0).getUri();
        statefulConnection.getConnection(uri.getHost(), uri.getPort()).sync();
        statefulConnection.close();
    }

    @Test
    public void advancedClusterAsync() throws Exception {
        StatefulRedisClusterConnection<String, String> statefulConnection = clusterClient.connectCluster()
                .getStatefulConnection();
        RedisURI uri = clusterClient.getPartitions().getPartition(0).getUri();
        statefulConnection.getConnection(uri.getHost(), uri.getPort()).sync();
        statefulConnection.close();
    }

    @Test
    public void advancedClusterReactive() throws Exception {
        StatefulRedisClusterConnection<String, String> statefulConnection = clusterClient.connectCluster()
                .getStatefulConnection();
        RedisURI uri = clusterClient.getPartitions().getPartition(0).getUri();
        statefulConnection.getConnection(uri.getHost(), uri.getPort()).reactive();
        statefulConnection.close();
    }

    @Test
    public void advancedClusterStateful() throws Exception {
        clusterClient.connect().close();
    }

    @Test
    public void deprecatedAvancedClusterStateful() throws Exception {
        clusterClient.connectCluster().getStatefulConnection().close();
    }

    // Cluster node selection
    @Test
    public void nodeSelectionClusterAsync() throws Exception {
        StatefulRedisClusterConnection<String, String> statefulConnection = clusterClient.connect();
        AsyncNodeSelection<String, String> masters = statefulConnection.async().masters();
        statefulConnection.close();
    }

}
