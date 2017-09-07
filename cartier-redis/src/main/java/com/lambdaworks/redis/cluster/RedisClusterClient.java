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
package com.lambdaworks.redis.cluster;

import java.io.Closeable;
import java.net.SocketAddress;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.cluster.api.NodeSelectionSupport;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;
import com.lambdaworks.redis.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import com.lambdaworks.redis.cluster.api.sync.RedisAdvancedClusterCommands;
import com.lambdaworks.redis.cluster.event.ClusterTopologyChangedEvent;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;
import com.lambdaworks.redis.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import com.lambdaworks.redis.cluster.topology.ClusterTopologyRefresh;
import com.lambdaworks.redis.cluster.topology.NodeConnectionFactory;
import com.lambdaworks.redis.cluster.topology.TopologyComparators;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.codec.StringCodec;
import com.lambdaworks.redis.internal.LettuceAssert;
import com.lambdaworks.redis.internal.LettuceLists;
import com.lambdaworks.redis.output.ValueStreamingChannel;
import com.lambdaworks.redis.protocol.CommandHandler;
import com.lambdaworks.redis.pubsub.PubSubCommandHandler;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnectionImpl;
import com.lambdaworks.redis.resource.ClientResources;
import com.lambdaworks.redis.resource.SocketAddressResolver;

import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * A scalable and thread-safe <a href="http://redis.io/">Redis</a> cluster client supporting synchronous, asynchronous and
 * reactive execution models. Multiple threads may share one connection. The cluster client handles command routing based on the
 * first key of the command and maintains a view of the cluster that is available when calling the {@link #getPartitions()}
 * method.
 *
 * <p>
 * Connections to the cluster members are opened on the first access to the cluster node and managed by the
 * {@link StatefulRedisClusterConnection}. You should not use transactional commands on cluster connections since {@code MULTI},
 * {@code EXEC} and {@code DISCARD} have no key and cannot be assigned to a particular node. A cluster connection uses a default
 * connection to run non-keyed commands.
 * </p>
 *
 * <p>
 * Connections to particular nodes can be obtained by {@link StatefulRedisClusterConnection#getConnection(String)} providing the
 * node id or {@link StatefulRedisClusterConnection#getConnection(String, int)} by host and port.
 * </p>
 *
 * <p>
 * <a href="http://redis.io/topics/cluster-spec#multiple-keys-operations">Multiple keys operations</a> have to operate on a key
 * that hashes to the same slot. Following commands do not need to follow that rule since they are pipelined according to its
 * hash value to multiple nodes in parallel on the sync, async and, reactive API:
 * </p>
 * <ul>
 * <li>{@link RedisAdvancedClusterAsyncCommands#del(Object[]) DEL}</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#unlink(Object[]) UNLINK}</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#mget(Object[]) MGET}</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#mget(ValueStreamingChannel, Object[]) MGET with streaming}</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#mset(Map) MSET}</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#msetnx(Map) MSETNX}</li>
 * </ul>
 *
 * <p>
 * Following commands on the Cluster sync, async and, reactive API are implemented with a Cluster-flavor:
 * </p>
 * <ul>
 * <li>{@link RedisAdvancedClusterAsyncCommands#clientSetname(Object)} Executes {@code CLIENT SET} on all connections and
 * initializes new connections with the {@code clientName}.</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#flushall()} Run {@code FLUSHALL} on all master nodes.</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#flushdb()} Executes {@code FLUSHDB} on all master nodes.</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#keys(Object)} Executes {@code KEYS} on all.</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#randomkey()} Returns a random key from a random master node.</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#scriptFlush()} Executes {@code SCRIPT FLUSH} on all nodes.</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#scriptKill()} Executes {@code SCRIPT KILL} on all nodes.</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#shutdown(boolean)} Executes {@code SHUTDOWN} on all nodes.</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#scan()} Executes a {@code SCAN} on all nodes according to {@link ReadFrom}. The
 * resulting cursor must be reused across the {@code SCAN} to scan iteratively across the whole cluster.</li>
 * </ul>
 *
 * <p>
 * Cluster commands can be issued to multiple hosts in parallel by using the {@link NodeSelectionSupport} API. A set of nodes is
 * selected using a {@link java.util.function.Predicate} and commands can be issued to the node selection
 *
 * <code><pre>
 * AsyncExecutions<String> ping = commands.masters().commands().ping();
 * Collection<RedisClusterNode> nodes = ping.nodes();
 * nodes.stream().forEach(redisClusterNode -&gt; ping.get(redisClusterNode));
 * </pre></code>
 * </p>
 *
 * {@link RedisClusterClient} is an expensive resource. Reuse this instance or share external {@link ClientResources} as much as
 * possible.
 *
 * @author Mark Paluch
 * @since 3.0
 * @see RedisURI
 * @see StatefulRedisClusterConnection
 * @see RedisCodec
 * @see ClusterClientOptions
 * @see ClientResources
 */
public class RedisClusterClient extends AbstractRedisClient {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(RedisClusterClient.class);

    protected final AtomicBoolean clusterTopologyRefreshActivated = new AtomicBoolean(false);
    protected final AtomicReference<ScheduledFuture<?>> clusterTopologyRefreshFuture = new AtomicReference<>();

    private final ClusterTopologyRefresh refresh = new ClusterTopologyRefresh(new NodeConnectionFactoryImpl(), getResources());
    private final ClusterTopologyRefreshScheduler clusterTopologyRefreshScheduler = new ClusterTopologyRefreshScheduler(this,
            getResources());
    private final Iterable<RedisURI> initialUris;

    private Partitions partitions;

    /**
     * Non-private constructor to make {@link RedisClusterClient} proxyable.
     */
    protected RedisClusterClient() {

        setOptions(ClusterClientOptions.create());
        this.initialUris = Collections.emptyList();
    }

    /**
     * Initialize the client with an initial cluster URI.
     *
     * @param initialUri initial cluster URI
     * @deprecated Use {@link #create(RedisURI)}
     */
    @Deprecated
    public RedisClusterClient(RedisURI initialUri) {
        this(Collections.singletonList(assertNotNull(initialUri)));
    }

    /**
     * Initialize the client with a list of cluster URI's. All uris are tried in sequence for connecting initially to the
     * cluster. If any uri is successful for connection, the others are not tried anymore. The initial uri is needed to discover
     * the cluster structure for distributing the requests.
     *
     * @param redisURIs iterable of initial {@link RedisURI cluster URIs}. Must not be {@literal null} and not empty.
     * @deprecated Use {@link #create(Iterable)}
     */
    @Deprecated
    public RedisClusterClient(List<RedisURI> redisURIs) {
        this(null, redisURIs);
    }

    /**
     * Initialize the client with a list of cluster URI's. All uris are tried in sequence for connecting initially to the
     * cluster. If any uri is successful for connection, the others are not tried anymore. The initial uri is needed to discover
     * the cluster structure for distributing the requests.
     *
     * @param clientResources the client resources. If {@literal null}, the client will create a new dedicated instance of
     *        client resources and keep track of them.
     * @param redisURIs iterable of initial {@link RedisURI cluster URIs}. Must not be {@literal null} and not empty.
     */
    protected RedisClusterClient(ClientResources clientResources, Iterable<RedisURI> redisURIs) {

        super(clientResources);

        assertNotEmpty(redisURIs);
        assertSameOptions(redisURIs);

        this.initialUris = Collections.unmodifiableList(LettuceLists.newList(redisURIs));

        setDefaultTimeout(getFirstUri().getTimeout(), getFirstUri().getUnit());
        setOptions(ClusterClientOptions.builder().build());
    }

    private static void assertSameOptions(Iterable<RedisURI> redisURIs) {

        Boolean ssl = null;
        Boolean startTls = null;
        Boolean verifyPeer = null;

        for (RedisURI redisURI : redisURIs) {

            if (ssl == null) {
                ssl = redisURI.isSsl();
            }
            if (startTls == null) {
                startTls = redisURI.isStartTls();
            }
            if (verifyPeer == null) {
                verifyPeer = redisURI.isVerifyPeer();
            }

            if (ssl.booleanValue() != redisURI.isSsl()) {
                throw new IllegalArgumentException("RedisURI " + redisURI
                        + " SSL is not consistent with the other seed URI SSL settings");
            }

            if (startTls.booleanValue() != redisURI.isStartTls()) {
                throw new IllegalArgumentException("RedisURI " + redisURI
                        + " StartTLS is not consistent with the other seed URI StartTLS settings");
            }

            if (verifyPeer.booleanValue() != redisURI.isVerifyPeer()) {
                throw new IllegalArgumentException("RedisURI " + redisURI
                        + " VerifyPeer is not consistent with the other seed URI VerifyPeer settings");
            }
        }
    }

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with default {@link ClientResources}. You can
     * connect to different Redis servers but you must supply a {@link RedisURI} on connecting.
     *
     * @param redisURI the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */
    public static RedisClusterClient create(RedisURI redisURI) {
        assertNotNull(redisURI);
        return create(Collections.singleton(redisURI));
    }

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with default {@link ClientResources}. You can
     * connect to different Redis servers but you must supply a {@link RedisURI} on connecting.
     *
     * @param redisURIs one or more Redis URI, must not be {@literal null} and not empty.
     * @return a new instance of {@link RedisClusterClient}
     */
    public static RedisClusterClient create(Iterable<RedisURI> redisURIs) {
        assertNotEmpty(redisURIs);
        assertSameOptions(redisURIs);
        return new RedisClusterClient(null, redisURIs);
    }

    /**
     * Create a new client that connects to the supplied uri with default {@link ClientResources}. You can connect to different
     * Redis servers but you must supply a {@link RedisURI} on connecting.
     *
     * @param uri the Redis URI, must not be empty or {@literal null}.
     * @return a new instance of {@link RedisClusterClient}
     */
    public static RedisClusterClient create(String uri) {
        LettuceAssert.notEmpty(uri, "URI must not be empty");
        return create(RedisClusterURIUtil.toRedisURIs(URI.create(uri)));
    }

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with shared {@link ClientResources}. You need to
     * shut down the {@link ClientResources} upon shutting down your application.You can connect to different Redis servers but
     * you must supply a {@link RedisURI} on connecting.
     *
     * @param clientResources the client resources, must not be {@literal null}
     * @param redisURI the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */
    public static RedisClusterClient create(ClientResources clientResources, RedisURI redisURI) {
        assertNotNull(clientResources);
        assertNotNull(redisURI);
        return create(clientResources, Collections.singleton(redisURI));
    }

    /**
     * Create a new client that connects to the supplied uri with shared {@link ClientResources}.You need to shut down the
     * {@link ClientResources} upon shutting down your application. You can connect to different Redis servers but you must
     * supply a {@link RedisURI} on connecting.
     *
     * @param clientResources the client resources, must not be {@literal null}
     * @param uri the Redis URI, must not be empty or {@literal null}.
     * @return a new instance of {@link RedisClusterClient}
     */
    public static RedisClusterClient create(ClientResources clientResources, String uri) {
        assertNotNull(clientResources);
        LettuceAssert.notEmpty(uri, "URI must not be empty");
        return create(clientResources, RedisClusterURIUtil.toRedisURIs(URI.create(uri)));
    }

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with shared {@link ClientResources}. You need to
     * shut down the {@link ClientResources} upon shutting down your application.You can connect to different Redis servers but
     * you must supply a {@link RedisURI} on connecting.
     *
     * @param clientResources the client resources, must not be {@literal null}
     * @param redisURIs one or more Redis URI, must not be {@literal null} and not empty
     * @return a new instance of {@link RedisClusterClient}
     */
    public static RedisClusterClient create(ClientResources clientResources, Iterable<RedisURI> redisURIs) {
        assertNotNull(clientResources);
        assertNotEmpty(redisURIs);
        assertSameOptions(redisURIs);
        return new RedisClusterClient(clientResources, redisURIs);
    }

    /**
     * Connect to a Redis Cluster and treat keys and values as UTF-8 strings.
     * <p>
     * What to expect from this connection:
     * </p>
     * <ul>
     * <li>A <i>default</i> connection is created to the node with the lowest latency</li>
     * <li>Keyless commands are send to the default connection</li>
     * <li>Single-key keyspace commands are routed to the appropriate node</li>
     * <li>Multi-key keyspace commands require the same slot-hash and are routed to the appropriate node</li>
     * <li>Pub/sub commands are sent to the node that handles the slot derived from the pub/sub channel</li>
     * </ul>
     *
     * @return A new stateful Redis Cluster connection
     */
    public StatefulRedisClusterConnection<String, String> connect() {
        return connect(newStringStringCodec());
    }

    /**
     * Connect to a Redis Cluster. Use the supplied {@link RedisCodec codec} to encode/decode keys and values.
     * <p>
     * What to expect from this connection:
     * </p>
     * <ul>
     * <li>A <i>default</i> connection is created to the node with the lowest latency</li>
     * <li>Keyless commands are send to the default connection</li>
     * <li>Single-key keyspace commands are routed to the appropriate node</li>
     * <li>Multi-key keyspace commands require the same slot-hash and are routed to the appropriate node</li>
     * <li>Pub/sub commands are sent to the node that handles the slot derived from the pub/sub channel</li>
     * </ul>
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return A new stateful Redis Cluster connection
     */
    @SuppressWarnings("unchecked")
    public <K, V> StatefulRedisClusterConnection<K, V> connect(RedisCodec<K, V> codec) {
        return connectClusterImpl(codec);
    }

    /**
     * Connect to a Redis Cluster using pub/sub connections and treat keys and values as UTF-8 strings.
     * <p>
     * What to expect from this connection:
     * </p>
     * <ul>
     * <li>A <i>default</i> connection is created to the node with the least number of clients</li>
     * <li>Pub/sub commands are sent to the node with the least number of clients</li>
     * <li>Keyless commands are send to the default connection</li>
     * <li>Single-key keyspace commands are routed to the appropriate node</li>
     * <li>Multi-key keyspace commands require the same slot-hash and are routed to the appropriate node</li>
     * </ul>
     *
     * @return A new stateful Redis Cluster connection
     */
    public StatefulRedisClusterPubSubConnection<String, String> connectPubSub() {
        return connectPubSub(newStringStringCodec());
    }

    /**
     * Connect to a Redis Cluster using pub/sub connections. Use the supplied {@link RedisCodec codec} to encode/decode keys and
     * values.
     * <p>
     * What to expect from this connection:
     * </p>
     * <ul>
     * <li>A <i>default</i> connection is created to the node with the least number of clients</li>
     * <li>Pub/sub commands are sent to the node with the least number of clients</li>
     * <li>Keyless commands are send to the default connection</li>
     * <li>Single-key keyspace commands are routed to the appropriate node</li>
     * <li>Multi-key keyspace commands require the same slot-hash and are routed to the appropriate node</li>
     * </ul>
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return A new stateful Redis Cluster connection
     */
    @SuppressWarnings("unchecked")
    public <K, V> StatefulRedisClusterPubSubConnection<K, V> connectPubSub(RedisCodec<K, V> codec) {
        return connectClusterPubSubImpl(codec);
    }

    /**
     * Open a new synchronous connection to a Redis Cluster that treats keys and values as UTF-8 strings.
     *
     * @return A new connection
     * @deprecated Use {@code connect().sync()}
     */
    @Deprecated
    public RedisAdvancedClusterCommands<String, String> connectCluster() {
        return connectCluster(newStringStringCodec());
    }

    /**
     * Open a new synchronous connection to a Redis Cluster. Use the supplied {@link RedisCodec codec} to encode/decode keys and
     * values.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return A new connection
     * @deprecated @deprecated Use {@code connect(codec).sync()}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public <K, V> RedisAdvancedClusterCommands<K, V> connectCluster(RedisCodec<K, V> codec) {
        return connectClusterImpl(codec).sync();
    }

    /**
     * Open a new asynchronous connection to a Redis Cluster that treats keys and values as UTF-8 strings.
     *
     * @return A new connection
     * @deprecated Use {@code connect().async()}
     */
    @Deprecated
    public RedisAdvancedClusterAsyncCommands<String, String> connectClusterAsync() {
        return connectClusterImpl(newStringStringCodec()).async();
    }

    /**
     * Open a new asynchronous connection to a Redis Cluster. Use the supplied {@link RedisCodec codec} to encode/decode keys
     * and values.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return A new connection
     * @deprecated @deprecated Use {@code connect(codec).async()}
     */
    @Deprecated
    public <K, V> RedisAdvancedClusterAsyncCommands<K, V> connectClusterAsync(RedisCodec<K, V> codec) {
        return connectClusterImpl(codec).async();
    }

    protected StatefulRedisConnection<String, String> connectToNode(final SocketAddress socketAddress) {
        return connectToNode(newStringStringCodec(), socketAddress.toString(), null, () -> socketAddress);
    }

    /**
     * Create a connection to a redis socket address.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param nodeId the nodeId
     * @param clusterWriter global cluster writer
     * @param socketAddressSupplier supplier for the socket address
     * @param <K> Key type
     * @param <V> Value type
     * @return A new connection
     */
    <K, V> StatefulRedisConnection<K, V> connectToNode(RedisCodec<K, V> codec, String nodeId,
            RedisChannelWriter<K, V> clusterWriter, final Supplier<SocketAddress> socketAddressSupplier) {
        return getConnection(connectToNodeAsync(codec, nodeId, clusterWriter, socketAddressSupplier));
    }

    /**
     * Create a connection to a redis socket address.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param nodeId the nodeId
     * @param clusterWriter global cluster writer
     * @param socketAddressSupplier supplier for the socket address
     * @param <K> Key type
     * @param <V> Value type
     * @return A new connection
     */
    <K, V> ConnectionFuture<StatefulRedisConnection<K, V>> connectToNodeAsync(RedisCodec<K, V> codec, String nodeId,
            RedisChannelWriter<K, V> clusterWriter, final Supplier<SocketAddress> socketAddressSupplier) {

        assertNotNull(codec);
        assertNotEmpty(initialUris);
        LettuceAssert.notNull(socketAddressSupplier, "SocketAddressSupplier must not be null");

        SocketAddress socketAddress = socketAddressSupplier.get();

        logger.debug(String.format("connectToNodeAsync(%s at %s)", nodeId, socketAddress));

        ClusterNodeCommandHandler<K, V> handler = new ClusterNodeCommandHandler<>(clientOptions, getResources(), clusterWriter);
        StatefulRedisConnectionImpl<K, V> connection = new StatefulRedisConnectionImpl<>(handler, codec, timeout, unit);

        ConnectionFuture<StatefulRedisConnection<K, V>> connectionFuture = connectStatefulAsync(handler, connection,
                getFirstUri(), socketAddressSupplier);

        return connectionFuture.whenComplete((conn, throwable) -> {
            if (throwable != null) {
                connection.close();
            }
        });
    }

    /**
     * Create a pub/sub connection to a redis socket address.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param nodeId the nodeId
     * @param socketAddressSupplier supplier for the socket address
     * @param <K> Key type
     * @param <V> Value type
     * @return A new connection
     */
    <K, V> StatefulRedisPubSubConnection<K, V> connectPubSubToNode(RedisCodec<K, V> codec, String nodeId,
            Supplier<SocketAddress> socketAddressSupplier) {
        return getConnection(connectPubSubToNodeAsync(codec, nodeId, socketAddressSupplier));
    }

    /**
     * Create a pub/sub connection to a redis socket address.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param nodeId the nodeId
     * @param socketAddressSupplier supplier for the socket address
     * @param <K> Key type
     * @param <V> Value type
     * @return A new connection
     */
    <K, V> ConnectionFuture<StatefulRedisPubSubConnection<K, V>> connectPubSubToNodeAsync(RedisCodec<K, V> codec,
            String nodeId, Supplier<SocketAddress> socketAddressSupplier) {

        assertNotNull(codec);
        assertNotEmpty(initialUris);

        LettuceAssert.notNull(socketAddressSupplier, "SocketAddressSupplier must not be null");

        logger.debug("connectPubSubToNode(" + nodeId + ")");

        PubSubCommandHandler<K, V> handler = new PubSubCommandHandler<>(clientOptions, clientResources, codec);
        StatefulRedisPubSubConnectionImpl<K, V> connection = new StatefulRedisPubSubConnectionImpl<>(handler, codec, timeout,
                unit);

        ConnectionFuture<StatefulRedisPubSubConnection<K, V>> connectionFuture = connectStatefulAsync(handler, connection,
                getFirstUri(), socketAddressSupplier);

        return connectionFuture.whenComplete((conn, throwable) -> {
            if (throwable != null) {
                connection.close();
            }
        });
    }

    /**
     * Create a clustered pub/sub connection with command distributor.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return a new connection
     */
    <K, V> StatefulRedisClusterConnectionImpl<K, V> connectClusterImpl(RedisCodec<K, V> codec) {

        if (partitions == null) {
            initializePartitions();
        }

        activateTopologyRefreshIfNeeded();

        logger.debug("connectCluster(" + initialUris + ")");

        Supplier<SocketAddress> socketAddressSupplier = getSocketAddressSupplier(TopologyComparators::sortByClientCount);

        CommandHandler<K, V> handler = new CommandHandler<>(clientOptions, clientResources);

        ClusterDistributionChannelWriter<K, V> clusterWriter = new ClusterDistributionChannelWriter<>(clientOptions, handler,
                clusterTopologyRefreshScheduler);
        PooledClusterConnectionProvider<K, V> pooledClusterConnectionProvider = new PooledClusterConnectionProvider<>(this,
                clusterWriter, codec);

        clusterWriter.setClusterConnectionProvider(pooledClusterConnectionProvider);

        StatefulRedisClusterConnectionImpl<K, V> connection = new StatefulRedisClusterConnectionImpl<>(clusterWriter, codec,
                timeout, unit);

        connection.setReadFrom(ReadFrom.MASTER);
        connection.setPartitions(partitions);

        boolean connected = false;
        RedisException causingException = null;
        int connectionAttempts = Math.max(1, partitions.size());

        for (int i = 0; i < connectionAttempts; i++) {
            try {
                connectStateful(handler, connection, getFirstUri(), socketAddressSupplier);
                connection.inspectRedisState();
                connected = true;
                break;
            } catch (RedisException e) {
                logger.warn(e.getMessage());
                causingException = e;
            }
        }

        if (!connected) {
            connection.close();
            if (causingException != null) {
                throw causingException;
            }
        }

        connection.registerCloseables(closeableResources, clusterWriter, pooledClusterConnectionProvider);


        return connection;
    }

    /**
     * Create a clustered connection with command distributor.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return a new connection
     */
    <K, V> StatefulRedisClusterPubSubConnection<K, V> connectClusterPubSubImpl(RedisCodec<K, V> codec) {

        if (partitions == null) {
            initializePartitions();
        }

        activateTopologyRefreshIfNeeded();

        logger.debug("connectClusterPubSub(" + initialUris + ")");

        Supplier<SocketAddress> socketAddressSupplier = getSocketAddressSupplier(TopologyComparators::sortByClientCount);

        PubSubCommandHandler<K, V> handler = new PubSubCommandHandler<>(clientOptions, clientResources, codec);

        ClusterDistributionChannelWriter<K, V> clusterWriter = new ClusterDistributionChannelWriter<>(clientOptions, handler,
                clusterTopologyRefreshScheduler);

        StatefulRedisClusterPubSubConnectionImpl<K, V> connection = new StatefulRedisClusterPubSubConnectionImpl<>(
                clusterWriter, codec, timeout, unit);

        ClusterPubSubConnectionProvider<K, V> pooledClusterConnectionProvider = new ClusterPubSubConnectionProvider<>(this,
                clusterWriter, codec, connection.getUpstreamListener());

        clusterWriter.setClusterConnectionProvider(pooledClusterConnectionProvider);

        connection.setPartitions(partitions);

        boolean connected = false;
        RedisException causingException = null;
        int connectionAttempts = Math.max(1, partitions.size());

        for (int i = 0; i < connectionAttempts; i++) {
            try {
                connectStateful(handler, connection, getFirstUri(), socketAddressSupplier);
                connection.inspectRedisState();
                connected = true;
                break;
            } catch (RedisException e) {
                logger.warn(e.getMessage());
                causingException = e;
            }
        }

        if (!connected) {
            connection.close();
            if (causingException != null) {
                throw causingException;
            }
        }

        connection.registerCloseables(closeableResources, clusterWriter, pooledClusterConnectionProvider);

        return connection;
    }

    /**
     * Connect to a endpoint provided by {@code socketAddressSupplier} using connection settings (authentication, SSL) from
     * {@code connectionSettings}.
     */
    private <K, V> void connectStateful(CommandHandler<K, V> handler, StatefulRedisConnectionImpl<K, V> connection,
            RedisURI connectionSettings, Supplier<SocketAddress> socketAddressSupplier) {

        getConnection(connectStatefulAsync(handler, connection, connectionSettings, socketAddressSupplier));
    }

    /**
     * Connect to a endpoint provided by {@code socketAddressSupplier} using connection settings (authentication, SSL) from
     * {@code connectionSettings}.
     */
    private <K, V> void connectStateful(CommandHandler<K, V> handler, StatefulRedisClusterConnectionImpl<K, V> connection,
            RedisURI connectionSettings, Supplier<SocketAddress> socketAddressSupplier) {

        getConnection(connectStatefulAsync(handler, connection, connectionSettings, socketAddressSupplier));
    }

    /**
     * Initiates a channel connection considering {@link ClientOptions} initialization options, authentication and client name
     * options.
     */
    private <K, V, T extends RedisChannelHandler<K, V>, S> ConnectionFuture<S> connectStatefulAsync(
            CommandHandler<K, V> handler, T connection, RedisURI connectionSettings,
            Supplier<SocketAddress> socketAddressSupplier) {

        ConnectionBuilder connectionBuilder = createConnectionBuilder(handler, connection, connectionSettings,
                socketAddressSupplier);

        if (clientOptions.isPingBeforeActivateConnection()) {
            if (hasPassword(connectionSettings)) {
                connectionBuilder.enableAuthPingBeforeConnect();
            } else {
                connectionBuilder.enablePingBeforeConnect();
            }
        }

        ConnectionFuture<RedisChannelHandler<K, V>> future = initializeChannelAsync(connectionBuilder);

        if (!clientOptions.isPingBeforeActivateConnection() && hasPassword(connectionSettings)) {
            future = future.thenApplyAsync(channelHandler -> {

                if (connection instanceof StatefulRedisClusterConnectionImpl) {
                    ((StatefulRedisClusterConnectionImpl) connection).async()
                            .auth(new String(connectionSettings.getPassword()));
                }

                if (connection instanceof StatefulRedisConnectionImpl) {
                    ((StatefulRedisConnectionImpl) connection).async().auth(new String(connectionSettings.getPassword()));
                }

                return channelHandler;
            }, clientResources.eventExecutorGroup());

        }

        if (LettuceStrings.isNotEmpty(connectionSettings.getClientName())) {
            future = future.thenApply(channelHandler -> {

                if (connection instanceof StatefulRedisClusterConnectionImpl) {
                    ((StatefulRedisClusterConnectionImpl) connection).setClientName(connectionSettings.getClientName());
                }

                if (connection instanceof StatefulRedisConnectionImpl) {
                    ((StatefulRedisConnectionImpl) connection).setClientName(connectionSettings.getClientName());
                }
                return channelHandler;
            });
        }

        return future.thenApply(channelHandler -> (S) connection);
    }

    private boolean hasPassword(RedisURI connectionSettings) {
        return connectionSettings.getPassword() != null && connectionSettings.getPassword().length != 0;
    }

    private <K, V> ConnectionBuilder createConnectionBuilder(CommandHandler<K, V> handler,
            RedisChannelHandler<K, V> connection, RedisURI connectionSettings, Supplier<SocketAddress> socketAddressSupplier) {

        ConnectionBuilder connectionBuilder;
        if (connectionSettings.isSsl()) {
            SslConnectionBuilder sslConnectionBuilder = SslConnectionBuilder.sslConnectionBuilder();
            sslConnectionBuilder.ssl(connectionSettings);
            connectionBuilder = sslConnectionBuilder;
        } else {
            connectionBuilder = ConnectionBuilder.connectionBuilder();
        }

        connectionBuilder.reconnectionListener(new ReconnectEventListener(clusterTopologyRefreshScheduler));
        connectionBuilder.clientOptions(clientOptions);
        connectionBuilder.clientResources(clientResources);
        connectionBuilder(handler, connection, socketAddressSupplier, connectionBuilder, connectionSettings);
        channelType(connectionBuilder, connectionSettings);
        return connectionBuilder;
    }

    /**
     * Reload partitions and re-initialize the distribution table.
     */
    public void reloadPartitions() {

        if (partitions == null) {
            initializePartitions();
            partitions.updateCache();
        } else {

            Partitions loadedPartitions = loadPartitions();
            if (TopologyComparators.isChanged(getPartitions(), loadedPartitions)) {

                logger.debug("Using a new cluster topology");

                List<RedisClusterNode> before = new ArrayList<>(getPartitions());
                List<RedisClusterNode> after = new ArrayList<>(loadedPartitions);

                getResources().eventBus().publish(new ClusterTopologyChangedEvent(before, after));
            }

            this.partitions.reload(loadedPartitions.getPartitions());
        }

        updatePartitionsInConnections();
    }

    protected void updatePartitionsInConnections() {

        forEachClusterConnection(input -> {
            input.setPartitions(partitions);
        });

        forEachClusterPubSubConnection(input -> {
            input.setPartitions(partitions);
        });
    }

    protected void initializePartitions() {
        this.partitions = loadPartitions();
    }

    /**
     * Retrieve the cluster view. Partitions are shared amongst all connections opened by this client instance.
     *
     * @return the partitions.
     */
    public Partitions getPartitions() {
        if (partitions == null) {
            initializePartitions();
        }
        return partitions;
    }

    /**
     * Retrieve partitions. Nodes within {@link Partitions} are ordered by latency. Lower latency nodes come first.
     *
     * @return Partitions
     */
    protected Partitions loadPartitions() {

        Iterable<RedisURI> topologyRefreshSource = getTopologyRefreshSource();

        String message = "Cannot retrieve initial cluster partitions from initial URIs " + topologyRefreshSource;
        try {
            Map<RedisURI, Partitions> partitions = refresh.loadViews(topologyRefreshSource, useDynamicRefreshSources());

            if (partitions.isEmpty()) {
                throw new RedisException(message);
            }

            Partitions loadedPartitions = determinePartitions(this.partitions, partitions);
            RedisURI viewedBy = refresh.getViewedBy(partitions, loadedPartitions);

            for (RedisClusterNode partition : loadedPartitions) {
                if (viewedBy != null) {
                    RedisURI uri = partition.getUri();
                    RedisClusterURIUtil.applyUriConnectionSettings(viewedBy, uri);
                }
            }

            activateTopologyRefreshIfNeeded();

            return loadedPartitions;

        } catch (RedisConnectionException e) {
            throw new RedisException(message, e);
        }
    }

    /**
     * Determines a {@link Partitions topology view} based on the current and the obtain topology views.
     *
     * @param current the current topology view. May be {@literal null} if {@link RedisClusterClient} has no topology view yet.
     * @param topologyViews the obtain topology views
     * @return the {@link Partitions topology view} to use.
     */
    protected Partitions determinePartitions(Partitions current, Map<RedisURI, Partitions> topologyViews) {

        if (current == null) {
            return PartitionsConsensus.HEALTHY_MAJORITY.getPartitions(null, topologyViews);
        }

        return PartitionsConsensus.KNOWN_MAJORITY.getPartitions(current, topologyViews);
    }

    private void activateTopologyRefreshIfNeeded() {

        if (getOptions() instanceof ClusterClientOptions) {
            ClusterClientOptions options = (ClusterClientOptions) getOptions();
            ClusterTopologyRefreshOptions topologyRefreshOptions = options.getTopologyRefreshOptions();

            if (!topologyRefreshOptions.isPeriodicRefreshEnabled() || clusterTopologyRefreshActivated.get()) {
                return;
            }

            if (clusterTopologyRefreshActivated.compareAndSet(false, true)) {
                ScheduledFuture<?> scheduledFuture = genericWorkerPool.scheduleAtFixedRate(clusterTopologyRefreshScheduler,
                        options.getRefreshPeriod(), options.getRefreshPeriod(), options.getRefreshPeriodUnit());
                clusterTopologyRefreshFuture.set(scheduledFuture);
            }
        }
    }

    /**
     * Sets the new cluster topology. The partitions are not applied to existing connections.
     *
     * @param partitions partitions object
     */
    public void setPartitions(Partitions partitions) {
        this.partitions = partitions;
    }

    /**
     * Returns the {@link ClientResources} which are used with that client.
     *
     * @return the {@link ClientResources} for this client
     */
    public ClientResources getResources() {
        return clientResources;
    }

    /**
     * Shutdown this client and close all open connections. The client should be discarded after calling shutdown.
     *
     * @param quietPeriod the quiet period as described in the documentation
     * @param timeout the maximum amount of time to wait until the executor is shutdown regardless if a task was submitted
     *        during the quiet period
     * @param timeUnit the unit of {@code quietPeriod} and {@code timeout}
     */
    @Override
    public void shutdown(long quietPeriod, long timeout, TimeUnit timeUnit) {

        if (clusterTopologyRefreshActivated.compareAndSet(true, false)) {

            ScheduledFuture<?> scheduledFuture = clusterTopologyRefreshFuture.get();

            try {
                scheduledFuture.cancel(false);
                clusterTopologyRefreshFuture.set(null);
            } catch (Exception e) {
                logger.debug("Could not unschedule Cluster topology refresh", e);
            }
        }

        super.shutdown(quietPeriod, timeout, timeUnit);
    }

    /**
     * Set the {@link ClusterClientOptions} for the client.
     *
     * @param clientOptions client options for the client and connections that are created after setting the options
     */
    public void setOptions(ClusterClientOptions clientOptions) {
        super.setOptions(clientOptions);
    }

    // -------------------------------------------------------------------------
    // Implementation hooks and helper methods
    // -------------------------------------------------------------------------

    /**
     * Returns the first {@link RedisURI} configured with this {@link RedisClusterClient} instance.
     *
     * @return the first {@link RedisURI}.
     */
    protected RedisURI getFirstUri() {
        assertNotEmpty(initialUris);
        Iterator<RedisURI> iterator = initialUris.iterator();
        return iterator.next();
    }

    /**
     * Returns a {@link Supplier} for {@link SocketAddress connection points}.
     *
     * @param sortFunction Sort function to enforce a specific order. The sort function must not change the order or the input
     *        parameter but create a new collection with the desired order, must not be {@literal null}.
     * @return {@link Supplier} for {@link SocketAddress connection points}.
     */
    protected Supplier<SocketAddress> getSocketAddressSupplier(Function<Partitions, Collection<RedisClusterNode>> sortFunction) {

        LettuceAssert.notNull(sortFunction, "Sort function must not be null");

        final RoundRobinSocketAddressSupplier socketAddressSupplier = new RoundRobinSocketAddressSupplier(partitions,
                sortFunction, clientResources);
        return () -> {
            if (partitions.isEmpty()) {
                SocketAddress socketAddress = SocketAddressResolver.resolve(getFirstUri(), clientResources.dnsResolver());
                logger.debug("Resolved SocketAddress {} using {}", socketAddress, getFirstUri());
                return socketAddress;
            }

            return socketAddressSupplier.get();
        };
    }

    /**
     * Returns an {@link Iterable} of the initial {@link RedisURI URIs}.
     *
     * @return the initial {@link RedisURI URIs}
     */
    protected Iterable<RedisURI> getInitialUris() {
        return initialUris;
    }

    /**
     * Apply a {@link Consumer} of {@link StatefulRedisClusterConnectionImpl} to all active connections.
     *
     * @param function the {@link Consumer}.
     */
    protected void forEachClusterConnection(Consumer<StatefulRedisClusterConnectionImpl<?, ?>> function) {
        forEachCloseable(input -> input instanceof StatefulRedisClusterConnectionImpl, function);
    }

    /**
     * Apply a {@link Consumer} of {@link StatefulRedisClusterPubSubConnectionImpl} to all active connections.
     *
     * @param function the {@link Consumer}.
     */
    protected void forEachClusterPubSubConnection(Consumer<StatefulRedisClusterPubSubConnectionImpl<?, ?>> function) {
        forEachCloseable(input -> input instanceof StatefulRedisClusterPubSubConnectionImpl, function);
    }

    /**
     * Apply a {@link Consumer} of {@link Closeable} to all active connections.
     *
     * @param <T>
     * @param function the {@link Consumer}.
     */
    @SuppressWarnings("unchecked")
    protected <T extends Closeable> void forEachCloseable(Predicate<? super Closeable> selector, Consumer<T> function) {
        for (Closeable c : closeableResources) {
            if (selector.test(c)) {
                function.accept((T) c);
            }
        }
    }

    /**
     * Returns the seed {@link RedisURI} for the topology refreshing. This method is called before each topology refresh to
     * provide an {@link Iterable} of {@link RedisURI} that is used to perform the next topology refresh.
     * <p>
     * Subclasses of {@link RedisClusterClient} may override that method.
     *
     * @return {@link Iterable} of {@link RedisURI} for the next topology refresh.
     */
    protected Iterable<RedisURI> getTopologyRefreshSource() {

        boolean initialSeedNodes = !useDynamicRefreshSources();

        Iterable<RedisURI> seed;
        if (initialSeedNodes || partitions == null || partitions.isEmpty()) {
            seed = RedisClusterClient.this.initialUris;
        } else {
            List<RedisURI> uris = new ArrayList<>();
            for (RedisClusterNode partition : TopologyComparators.sortByUri(partitions)) {
                uris.add(partition.getUri());
            }
            seed = uris;
        }
        return seed;
    }

    /**
     * Returns {@link true} if {@link ClusterTopologyRefreshOptions#useDynamicRefreshSources() dynamic refresh sources} are
     * enabled.
     * <p>
     * Subclasses of {@link RedisClusterClient} may override that method.
     *
     * @return {@link true} if dynamic refresh sources are used.
     * @see ClusterTopologyRefreshOptions#useDynamicRefreshSources()
     */
    protected boolean useDynamicRefreshSources() {

        if (getClusterClientOptions() != null) {
            ClusterTopologyRefreshOptions topologyRefreshOptions = getClusterClientOptions().getTopologyRefreshOptions();

            return topologyRefreshOptions.useDynamicRefreshSources();
        }
        return true;
    }

    /**
     * Returns a {@link String} {@link RedisCodec codec}.
     *
     * @return a {@link String} {@link RedisCodec codec}.
     * @see StringCodec#UTF8
     */
    protected RedisCodec<String, String> newStringStringCodec() {
        return StringCodec.UTF8;
    }

    ClusterClientOptions getClusterClientOptions() {
        if (getOptions() instanceof ClusterClientOptions) {
            return (ClusterClientOptions) getOptions();
        }
        return null;
    }

    boolean expireStaleConnections() {
        return getClusterClientOptions() == null || getClusterClientOptions().isCloseStaleConnections();
    }

    private static <K, V> void assertNotNull(RedisCodec<K, V> codec) {
        LettuceAssert.notNull(codec, "RedisCodec must not be null");
    }

    private static void assertNotEmpty(Iterable<RedisURI> redisURIs) {
        LettuceAssert.notNull(redisURIs, "RedisURIs must not be null");
        LettuceAssert.isTrue(redisURIs.iterator().hasNext(), "RedisURIs must not be empty");
    }

    private static RedisURI assertNotNull(RedisURI redisURI) {
        LettuceAssert.notNull(redisURI, "RedisURI must not be null");
        return redisURI;
    }

    private static void assertNotNull(ClientResources clientResources) {
        LettuceAssert.notNull(clientResources, "ClientResources must not be null");
    }

    private class NodeConnectionFactoryImpl implements NodeConnectionFactory {

        @Override
        public <K, V> StatefulRedisConnection<K, V> connectToNode(RedisCodec<K, V> codec, SocketAddress socketAddress) {
            return RedisClusterClient.this.connectToNode(codec, socketAddress.toString(), null, () -> socketAddress);
        }

        @Override
        public <K, V> ConnectionFuture<StatefulRedisConnection<K, V>> connectToNodeAsync(RedisCodec<K, V> codec,
                SocketAddress socketAddress) {
            return RedisClusterClient.this.connectToNodeAsync(codec, socketAddress.toString(), null, () -> socketAddress);
        }
    }
}
