/*
 * Copyright 2015-2017 the original author or authors.
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

import com.lambdaworks.redis.ConnectionFuture;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;
import com.lambdaworks.redis.cluster.pubsub.RedisClusterPubSubListener;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.pubsub.RedisPubSubAdapter;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import com.lambdaworks.redis.resource.ClientResources;

/**
 * {@link ClusterConnectionProvider} to provide {@link StatefulRedisPubSubConnection}s for Redis Cluster use.
 * <p>
 * {@link StatefulRedisPubSubConnection}s provided by this {@link ClusterConnectionProvider} get a {@link RedisPubSubListener}
 * registered that propagates received events to an upstream {@link RedisClusterPubSubListener} to provide message propagation.
 * Message propagation performs a {@link RedisClusterNode} lookup to distinguish notifications between cluster nodes.
 *
 * @author Mark Paluch
 * @since 4.4
 */
class ClusterPubSubConnectionProvider<K, V> extends PooledClusterConnectionProvider<K, V> {

    private final RedisClusterClient redisClusterClient;
    private final RedisCodec<K, V> redisCodec;
    private final RedisClusterPubSubListener<K, V> notifications;

    /**
     * Creates a new {@link ClusterPubSubConnectionProvider}.
     *
     * @param redisClusterClient must not be {@literal null}.
     * @param clusterWriter must not be {@literal null}.
     * @param redisCodec must not be {@literal null}.
     * @param notificationTarget must not be {@literal null}.
     */
    public ClusterPubSubConnectionProvider(RedisClusterClient redisClusterClient,
            ClusterDistributionChannelWriter<K, V> clusterWriter, RedisCodec<K, V> redisCodec,
            RedisClusterPubSubListener<K, V> notificationTarget) {

        super(redisClusterClient, clusterWriter, redisCodec);

        this.redisClusterClient = redisClusterClient;
        this.redisCodec = redisCodec;
        this.notifications = notificationTarget;
    }

    @Override
    protected ClusterNodeConnectionFactory<K, V> getConnectionFactory(RedisClusterClient redisClusterClient) {
        return new DecoratingClusterNodeConnectionFactory(new PubSubNodeConnectionFactory(redisClusterClient.getResources()));
    }

    @SuppressWarnings("unchecked")
    private class PubSubNodeConnectionFactory extends AbstractClusterNodeConnectionFactory<K, V> {

        public PubSubNodeConnectionFactory(ClientResources clientResources) {
            super(clientResources);
        }

        @Override
        public ConnectionFuture<StatefulRedisConnection<K, V>> apply(ConnectionKey key) {

            if (key.nodeId != null) {

                // NodeId connections do not provide command recovery due to cluster reconfiguration
                return redisClusterClient.connectPubSubToNodeAsync((RedisCodec) redisCodec, key.nodeId,
                        getSocketAddressSupplier(key));
            }

            // Host and port connections do provide command recovery due to cluster reconfiguration
            return redisClusterClient.connectPubSubToNodeAsync((RedisCodec) redisCodec, key.host + ":" + key.port,
                    getSocketAddressSupplier(key));
        }
    }

    @SuppressWarnings("unchecked")
    private class DecoratingClusterNodeConnectionFactory implements ClusterNodeConnectionFactory<K, V> {

        private final ClusterNodeConnectionFactory<K, V> delegate;

        public DecoratingClusterNodeConnectionFactory(ClusterNodeConnectionFactory<K, V> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void setPartitions(Partitions partitions) {
            delegate.setPartitions(partitions);
        }

        @Override
        public ConnectionFuture<StatefulRedisConnection<K, V>> apply(ConnectionKey key) {

            ConnectionFuture<StatefulRedisConnection<K, V>> future = delegate.apply(key);
            if (key.nodeId != null) {
                return future.thenApply(connection -> {
                    ((StatefulRedisPubSubConnection) connection).addListener(new DelegatingRedisClusterPubSubListener(
                            key.nodeId));
                    return connection;
                });
            }

            return future.thenApply(connection -> {
                ((StatefulRedisPubSubConnection) connection).addListener(new DelegatingRedisClusterPubSubListener(key.host,
                        key.port));

                return connection;
            });
        }
    }

    private class DelegatingRedisClusterPubSubListener extends RedisPubSubAdapter<K, V> {

        private final String nodeId;
        private final String host;
        private final int port;

        public DelegatingRedisClusterPubSubListener(String nodeId) {

            this.nodeId = nodeId;
            this.host = null;
            this.port = 0;
        }

        public DelegatingRedisClusterPubSubListener(String host, int port) {

            this.nodeId = null;
            this.host = host;
            this.port = port;
        }

        @Override
        public void message(K channel, V message) {
            notifications.message(getNode(), channel, message);
        }

        @Override
        public void message(K pattern, K channel, V message) {
            notifications.message(getNode(), pattern, channel, message);
        }

        @Override
        public void subscribed(K channel, long count) {
            notifications.subscribed(getNode(), channel, count);
        }

        @Override
        public void psubscribed(K pattern, long count) {
            notifications.psubscribed(getNode(), pattern, count);
        }

        @Override
        public void unsubscribed(K channel, long count) {
            notifications.unsubscribed(getNode(), channel, count);
        }

        @Override
        public void punsubscribed(K pattern, long count) {
            notifications.punsubscribed(getNode(), pattern, count);
        }

        private RedisClusterNode getNode() {
            return nodeId != null ? getPartitions().getPartitionByNodeId(nodeId) : getPartition(host, port);
        }
    }
}
