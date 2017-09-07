/*
 * Copyright 2017 the original author or authors.
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

import java.util.concurrent.TimeUnit;

import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.api.rx.RedisReactiveCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.protocol.RedisCommand;

/**
 * @author Mark Paluch
 */
public class EmptyStatefulRedisConnection extends RedisChannelHandler implements StatefulRedisConnection {

    public final static EmptyStatefulRedisConnection INSTANCE = new EmptyStatefulRedisConnection(
            EmptyRedisChannelWriter.INSTANCE);

    public EmptyStatefulRedisConnection(RedisChannelWriter writer) {
        super(writer, 0, TimeUnit.MINUTES);
    }
    @Override
    public boolean isMulti() {
        return false;
    }

    @Override
    public RedisCommands sync() {
        return null;
    }

    @Override
    public RedisAsyncCommands async() {
        return null;
    }

    @Override
    public RedisReactiveCommands reactive() {
        return null;
    }

    @Override
    public void setTimeout(long timeout, TimeUnit unit) {

    }

    @Override
    public TimeUnit getTimeoutUnit() {
        return null;
    }

    @Override
    public long getTimeout() {
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public ClientOptions getOptions() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setAutoFlushCommands(boolean autoFlush) {

    }

    @Override
    public void flushCommands() {

    }

    @Override
    public RedisCommand dispatch(RedisCommand command) {
        return null;
    }
}
