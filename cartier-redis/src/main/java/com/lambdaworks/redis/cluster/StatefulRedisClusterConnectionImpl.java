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

import static com.lambdaworks.redis.protocol.CommandType.AUTH;
import static com.lambdaworks.redis.protocol.CommandType.READONLY;
import static com.lambdaworks.redis.protocol.CommandType.READWRITE;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;
import com.lambdaworks.redis.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands;
import com.lambdaworks.redis.cluster.api.rx.RedisAdvancedClusterReactiveCommands;
import com.lambdaworks.redis.cluster.api.sync.NodeSelection;
import com.lambdaworks.redis.cluster.api.sync.NodeSelectionCommands;
import com.lambdaworks.redis.cluster.api.sync.RedisAdvancedClusterCommands;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.codec.StringCodec;
import com.lambdaworks.redis.internal.LettuceAssert;
import com.lambdaworks.redis.models.command.CommandDetail;
import com.lambdaworks.redis.models.command.CommandDetailParser;
import com.lambdaworks.redis.output.StatusOutput;
import com.lambdaworks.redis.protocol.*;

import io.netty.channel.ChannelHandler;

/**
 * A thread-safe connection to a Redis Cluster. Multiple threads may share one {@link StatefulRedisClusterConnectionImpl}
 *
 * A {@link ConnectionWatchdog} monitors each connection and reconnects automatically until {@link #close} is called. All
 * pending commands will be (re)sent after successful reconnection.
 *
 * @author Mark Paluch
 * @since 4.0
 */
@ChannelHandler.Sharable
public class StatefulRedisClusterConnectionImpl<K, V> extends RedisChannelHandler<K, V> implements
        StatefulRedisClusterConnection<K, V> {

    private Partitions partitions;

    private char[] password;
    private boolean readOnly;
    private String clientName;

    protected final RedisCodec<K, V> codec;
    protected final RedisAdvancedClusterCommands<K, V> sync;
    protected final RedisAdvancedClusterAsyncCommandsImpl<K, V> async;
    protected final RedisAdvancedClusterReactiveCommandsImpl<K, V> reactive;

    private volatile RedisState state;

    /**
     * Initialize a new connection.
     *
     * @param writer the channel writer
     * @param codec Codec used to encode/decode keys and values.
     * @param timeout Maximum time to wait for a response.
     * @param unit Unit of time for the timeout.
     */
    public StatefulRedisClusterConnectionImpl(RedisChannelWriter<K, V> writer, RedisCodec<K, V> codec, long timeout,
            TimeUnit unit) {
        super(writer, timeout, unit);
        this.codec = codec;

        this.async = new RedisAdvancedClusterAsyncCommandsImpl<>(this, codec);
        this.sync = (RedisAdvancedClusterCommands) Proxy.newProxyInstance(AbstractRedisClient.class.getClassLoader(),
                new Class<?>[] { RedisAdvancedClusterConnection.class, RedisAdvancedClusterCommands.class },
                syncInvocationHandler());
        this.reactive = new RedisAdvancedClusterReactiveCommandsImpl<>(this, codec);
    }

    @Override
    public RedisAdvancedClusterCommands<K, V> sync() {
        return sync;
    }

    protected InvocationHandler syncInvocationHandler() {
        return new ClusterFutureSyncInvocationHandler<>(this, RedisClusterAsyncCommands.class, NodeSelection.class,
                NodeSelectionCommands.class, async());
    }

    @Override
    public RedisAdvancedClusterAsyncCommands<K, V> async() {
        return async;
    }

    @Override
    public RedisAdvancedClusterReactiveCommands<K, V> reactive() {
        return reactive;
    }

    @Deprecated
    protected RedisAdvancedClusterReactiveCommandsImpl<K, V> getReactiveCommands() {
        return reactive;
    }

    void inspectRedisState() {

        List<CommandDetail> commands = CommandDetailParser.parse(sync().command());

        this.state = new RedisState(commands);
    }

    RedisState getState() {
        return state;
    }

    private RedisURI lookup(String nodeId) {

        for (RedisClusterNode partition : partitions) {
            if (partition.getNodeId().equals(nodeId)) {
                return partition.getUri();
            }
        }
        return null;
    }

    @Override
    public StatefulRedisConnection<K, V> getConnection(String nodeId) {

        RedisURI redisURI = lookup(nodeId);

        if (redisURI == null) {
            throw new RedisException("NodeId " + nodeId + " does not belong to the cluster");
        }

        return getClusterDistributionChannelWriter().getClusterConnectionProvider().getConnection(
                ClusterConnectionProvider.Intent.WRITE, nodeId);
    }

    @Override
    public StatefulRedisConnection<K, V> getConnection(String host, int port) {

        return getClusterDistributionChannelWriter().getClusterConnectionProvider().getConnection(
                ClusterConnectionProvider.Intent.WRITE, host, port);
    }

    public ClusterDistributionChannelWriter<K, V> getClusterDistributionChannelWriter() {
        return (ClusterDistributionChannelWriter<K, V>) super.getChannelWriter();
    }

    @Override
    public void activated() {

        super.activated();
        // do not block in here, since the channel flow will be interrupted.
        if (password != null) {
            async.authAsync(password);
        }

        if (clientName != null) {
            setClientName(clientName);
        }

        if (readOnly) {
            async.readOnly();
        }
    }

    void setClientName(String clientName) {

        CommandArgs<String, String> args = new CommandArgs<>(StringCodec.UTF8).add(CommandKeyword.SETNAME).addValue(clientName);
        AsyncCommand<String, String, String> async = new AsyncCommand<>(new Command<>(CommandType.CLIENT, new StatusOutput<>(
                StringCodec.UTF8), args));
        this.clientName = clientName;

        dispatch((RedisCommand) async);
    }

    @Override
    public <T, C extends RedisCommand<K, V, T>> C dispatch(C command) {

        RedisCommand<K, V, T> local = command;

        if (local.getType().name().equals(AUTH.name())) {
            local = attachOnComplete(local, status -> {
                if (status.equals("OK")) {
                    char[] password = CommandArgsAccessor.getFirstCharArray(command.getArgs());

                    if (password != null) {
                        this.password = password;
                    } else {

                        String stringPassword = CommandArgsAccessor.getFirstString(command.getArgs());
                        if (stringPassword != null) {
                            this.password = stringPassword.toCharArray();
                        }
                    }
                }
            });
        }

        if (local.getType().name().equals(READONLY.name())) {
            local = attachOnComplete(local, status -> {
                if (status.equals("OK")) {
                    this.readOnly = true;
                }
            });
        }

        if (local.getType().name().equals(READWRITE.name())) {
            local = attachOnComplete(local, status -> {
                if (status.equals("OK")) {
                    this.readOnly = false;
                }
            });
        }

        return super.dispatch((C) local);
    }

    private <T> RedisCommand<K, V, T> attachOnComplete(RedisCommand<K, V, T> command, Consumer<T> consumer) {

        if (command instanceof CompleteableCommand) {
            CompleteableCommand<T> completeable = (CompleteableCommand<T>) command;
            completeable.onComplete(consumer);
        }
        return command;
    }

    public void setPartitions(Partitions partitions) {
        this.partitions = partitions;
        getClusterDistributionChannelWriter().setPartitions(partitions);
    }

    public Partitions getPartitions() {
        return partitions;
    }

    @Override
    public void setReadFrom(ReadFrom readFrom) {
        LettuceAssert.notNull(readFrom, "ReadFrom must not be null");
        getClusterDistributionChannelWriter().setReadFrom(readFrom);
    }

    @Override
    public ReadFrom getReadFrom() {
        return getClusterDistributionChannelWriter().getReadFrom();
    }
}
