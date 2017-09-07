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
package com.lambdaworks.redis.commands;

import static com.lambdaworks.redis.protocol.CommandType.ZRANGEBYLEX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

import com.lambdaworks.redis.AbstractRedisClientTest;
import com.lambdaworks.redis.ReactiveCommandDispatcher;
import com.lambdaworks.redis.RedisCommandExecutionException;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.codec.Utf8StringCodec;
import com.lambdaworks.redis.output.StatusOutput;
import com.lambdaworks.redis.output.ValueListOutput;
import com.lambdaworks.redis.protocol.*;

import rx.Observable;

/**
 * @author Mark Paluch
 */
public class CustomCommandTest extends AbstractRedisClientTest {

    protected final Utf8StringCodec utf8StringCodec = new Utf8StringCodec();

    @Test
    public void dispatchSet() throws Exception {

        String response = redis.dispatch(MyCommands.SET, new StatusOutput<>(utf8StringCodec),
                new CommandArgs<>(utf8StringCodec).addKey(key).addValue(value));

        assertThat(response).isEqualTo("OK");
    }

    @Test
    public void dispatchWithoutArgs() throws Exception {

        String response = redis.dispatch(MyCommands.INFO, new StatusOutput<>(utf8StringCodec));

        assertThat(response).contains("connected_clients");
    }

    @Test(expected = RedisCommandExecutionException.class)
    public void dispatchShouldFailForWrongDataType() throws Exception {

        redis.hset(key, key, value);
        redis.dispatch(CommandType.GET, new StatusOutput<>(utf8StringCodec), new CommandArgs<>(utf8StringCodec).addKey(key));
    }

    @Test
    public void dispatchTransactions() throws Exception {

        redis.multi();
        String response = redis.dispatch(CommandType.SET, new StatusOutput<>(utf8StringCodec),
                new CommandArgs<>(utf8StringCodec).addKey(key).addValue(value));

        List<Object> exec = redis.exec();

        assertThat(response).isNull();
        assertThat(exec).hasSize(1).contains("OK");
    }

    @Test
    public void standaloneAsyncPing() throws Exception {

        RedisCommand<String, String, String> command = new Command<>(MyCommands.PING, new StatusOutput<>(new Utf8StringCodec()),
                null);

        AsyncCommand<String, String, String> async = new AsyncCommand<>(command);
        getStandaloneConnection().dispatch(async);

        assertThat(async.get()).isEqualTo("PONG");
    }

    @Test
    public void standaloneFireAndForget() throws Exception {

        RedisCommand<String, String, String> command = new Command<>(MyCommands.PING, new StatusOutput<>(new Utf8StringCodec()),
                null);
        getStandaloneConnection().dispatch(command);
        assertThat(command.isCancelled()).isFalse();

    }

    @Test
    public void standaloneReactivePing() throws Exception {

        RedisCommand<String, String, String> command = new Command<>(MyCommands.PING, new StatusOutput<>(new Utf8StringCodec()),
                null);
        ReactiveCommandDispatcher<String, String, String> dispatcher = new ReactiveCommandDispatcher<>(command,
                getStandaloneConnection(), false);

        String result = Observable.create(dispatcher).toBlocking().first();

        assertThat(result).isEqualTo("PONG");
    }

    private StatefulRedisConnection<String, String> getStandaloneConnection() {

        assumeTrue(redis.getStatefulConnection() instanceof StatefulRedisConnection);
        return redis.getStatefulConnection();
    }

    public enum MyCommands implements ProtocolKeyword {
        PING, SET, INFO;

        private final byte name[];

        MyCommands() {
            // cache the bytes for the command name. Reduces memory and cpu pressure when using commands.
            name = name().getBytes();
        }

        @Override
        public byte[] getBytes() {
            return name;
        }
    }
}
