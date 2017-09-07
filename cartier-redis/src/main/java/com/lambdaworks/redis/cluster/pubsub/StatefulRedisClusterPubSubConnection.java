/*
 * Copyright 2016 the original author or authors.
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
package com.lambdaworks.redis.cluster.pubsub;

import java.util.function.Predicate;

import com.lambdaworks.redis.RedisException;
import com.lambdaworks.redis.cluster.ClusterClientOptions;
import com.lambdaworks.redis.cluster.api.sync.NodeSelection;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lambdaworks.redis.cluster.pubsub.api.async.RedisClusterPubSubAsyncCommands;
import com.lambdaworks.redis.cluster.pubsub.api.rx.RedisClusterPubSubReactiveCommands;
import com.lambdaworks.redis.cluster.pubsub.api.sync.RedisClusterPubSubCommands;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;

/**
 * A stateful Pub/Sub connection for Redis Cluster use. This connection type is intended for Pub/Sub messaging with Redis
 * Cluster. The connection provides transparent command routing based on the first command key.
 * <p>
 * This connection allows publishing and subscription to Pub/Sub messages within a Redis Cluster. Due to Redis Cluster's nature,
 * messages are broadcasted across the cluster and a client can connect to any arbitrary node to participate with a
 * subscription.
 *
 * <pre>
 *     <code>
 *  StatefulRedisClusterPubSubConnection<String, String> connection = clusterClient.connectPubSub();
 *  connection.addListener(…);
 *
 *  RedisClusterPubSubCommands<String, String> sync = connection.sync();
 *  sync.subscribe("channel");
 *  sync.publish("channel", "message");
 *     </code>
 * </pre>
 *
 * <h3>Keyspace notifications</h3> Redis clients can subscribe to user-space Pub/Sub messages and Redis keyspace notifications.
 * Other than user-space Pub/Sub messages are Keyspace notifications not broadcasted to the whole cluster. They stay node-local.
 * Subscription to keyspace notifications requires subscription to the nodes which publish the keyspace notifications.
 *
 * <p>
 * {@link StatefulRedisClusterPubSubConnection} allows node-specific subscriptions and {@link #setNodeMessagePropagation message
 * propagation}. {@link #setNodeMessagePropagation} can notify a {@link RedisPubSubListener} that requires a single registration
 * with {@link #addListener(RedisPubSubListener) this connection}. Node-subscriptions are supported on
 * {@link #getConnection(String, int) connection} and {@link NodeSelection} levels through
 * {@link RedisClusterPubSubAsyncCommands#nodes(Predicate) asynchronous}, {@link RedisClusterPubSubCommands#nodes(Predicate)
 * synchronous}, and {@link RedisClusterPubSubReactiveCommands#nodes(Predicate) reactive} APIs.
 *
 * <pre>
 *     <code>
 *  StatefulRedisClusterPubSubConnection<String, String> connection = clusterClient.connectPubSub();
 *  connection.addListener(…);
 *
 *  RedisClusterPubSubCommands<String, String> sync = connection.sync();
 *  sync.slaves().commands().psubscribe("__key*__:*");
 *     </code>
 * </pre>
 *
 * @author Mark Paluch
 * @since 4.4
 */
public interface StatefulRedisClusterPubSubConnection<K, V> extends StatefulRedisPubSubConnection<K, V> {

    /**
     * Returns the {@link RedisClusterPubSubCommands} API for the current connection. Does not create a new connection.
     *
     * @return the synchronous API for the underlying connection.
     */
    RedisClusterPubSubCommands<K, V> sync();

    /**
     * Returns the {@link RedisClusterPubSubAsyncCommands} API for the current connection. Does not create a new connection.
     *
     * @return the asynchronous API for the underlying connection.
     */
    RedisClusterPubSubAsyncCommands<K, V> async();

    /**
     * Returns the {@link RedisClusterPubSubReactiveCommands} API for the current connection. Does not create a new connection.
     *
     * @return the reactive API for the underlying connection.
     */
    RedisClusterPubSubReactiveCommands<K, V> reactive();

    /**
     * Retrieve a connection to the specified cluster node using the nodeId. Host and port are looked up in the node list. This
     * connection is bound to the node id. Once the cluster topology view is updated, the connection will try to reconnect the
     * to the node with the specified {@code nodeId}, that behavior can also lead to a closed connection once the node with the
     * specified {@code nodeId} is no longer part of the cluster.
     *
     * Do not close the connections. Otherwise, unpredictable behavior will occur. The nodeId must be part of the cluster and is
     * validated against the current topology view in {@link com.lambdaworks.redis.cluster.models.partitions.Partitions}.
     *
     * @param nodeId the node Id
     * @return a connection to the requested cluster node
     * @throws RedisException if the requested node identified by {@code nodeId} is not part of the cluster
     */
    StatefulRedisPubSubConnection<K, V> getConnection(String nodeId);

    /**
     * Retrieve a connection to the specified cluster node using host and port. This connection is bound to a host and port.
     * Updates to the cluster topology view can close the connection once the host, identified by {@code host} and {@code port},
     * are no longer part of the cluster.
     *
     * Do not close the connections. Otherwise, unpredictable behavior will occur. Host and port connections are verified by
     * default for cluster membership, see {@link ClusterClientOptions#isValidateClusterNodeMembership()}.
     *
     * @param host the host
     * @param port the port
     * @return a connection to the requested cluster node
     * @throws RedisException if the requested node identified by {@code host} and {@code port} is not part of the cluster
     */
    StatefulRedisPubSubConnection<K, V> getConnection(String host, int port);

    /**
     * @return Known partitions for this connection.
     */
    Partitions getPartitions();

    /**
     * Enables/disables node message propagation to {@code this} {@link StatefulRedisClusterPubSubConnection connections}
     * {@link RedisPubSubListener listeners}.
     * <p>
     * If {@code enabled} is {@literal true}, then Pub/Sub messages received on node-specific connections are propagated to this
     * connection facade. Registered {@link RedisPubSubListener} will receive messages from individual node subscriptions.
     * <p>
     * Node event propagation is disabled by default.
     * 
     * @param enabled {@literal true} to enable node message propagation; {@literal false} (default) to disable message
     *        propagation.
     */
    void setNodeMessagePropagation(boolean enabled);

    /**
     * Add a new {@link RedisClusterPubSubListener listener}.
     *
     * @param listener the listener, must not be {@literal null}.
     */
    void addListener(RedisClusterPubSubListener<K, V> listener);

    /**
     * Remove an existing {@link RedisClusterPubSubListener listener}.
     *
     * @param listener the listener, must not be {@literal null}.
     */
    void removeListener(RedisClusterPubSubListener<K, V> listener);
}
