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
package com.lambdaworks.redis.cluster;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;
import com.lambdaworks.redis.cluster.api.sync.RedisClusterCommands;
import com.lambdaworks.redis.cluster.models.partitions.ClusterPartitionParser;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;

/**
 * @author Mark Paluch
 * @since 3.0
 */
public class ClusterTestUtil {

    /**
     * Retrieve the cluster node Id from the {@code connection}.
     * 
     * @param connection
     * @return
     */
    public static String getNodeId(RedisClusterCommands<?, ?> connection) {
        RedisClusterNode ownPartition = getOwnPartition(connection);
        if (ownPartition != null) {
            return ownPartition.getNodeId();
        }

        return null;
    }

    /**
     * Retrieve the {@link RedisClusterNode} from the {@code connection}.
     * 
     * @param connection
     * @return
     */
    public static RedisClusterNode getOwnPartition(RedisClusterCommands<?, ?> connection) {
        Partitions partitions = ClusterPartitionParser.parse(connection.clusterNodes());

        for (RedisClusterNode partition : partitions) {
            if (partition.getFlags().contains(RedisClusterNode.NodeFlag.MYSELF)) {
                return partition;
            }
        }
        return null;
    }

    /**
     * Flush databases of all cluster nodes.
     * 
     * @param connection the cluster connection
     */
    public static void flushDatabaseOfAllNodes(StatefulRedisClusterConnection<?, ?> connection) {
        for (RedisClusterNode node : connection.getPartitions()) {
            try {
                connection.getConnection(node.getNodeId()).sync().flushall();
                connection.getConnection(node.getNodeId()).sync().flushdb();
            } catch (Exception o_O) {
                // ignore
            }
        }
    }

    /**
     * Create an API wrapper which exposes the {@link RedisCommands} API by using internally a cluster connection.
     * 
     * @param connection
     * @return
     */
    public static RedisCommands<String, String> redisCommandsOverCluster(
            StatefulRedisClusterConnection<String, String> connection) {
        StatefulRedisClusterConnectionImpl clusterConnection = (StatefulRedisClusterConnectionImpl) connection;
        InvocationHandler h = clusterConnection.syncInvocationHandler();
        return (RedisCommands<String, String>) Proxy.newProxyInstance(ClusterTestUtil.class.getClassLoader(),
                new Class[] { RedisCommands.class }, h);
    }
}
