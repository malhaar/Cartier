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
package com.lambdaworks.redis.api.async;

import java.util.concurrent.TimeUnit;

import com.lambdaworks.redis.RedisAsyncConnection;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands;

/**
 * A complete asynchronous and thread-safe Redis API with 400+ Methods.
 * 
 * @param <K> Key type.
 * @param <V> Value type.
 * @author Mark Paluch
 * @since 3.0
 */
public interface RedisAsyncCommands<K, V> extends RedisHashAsyncCommands<K, V>, RedisKeyAsyncCommands<K, V>,
        RedisStringAsyncCommands<K, V>, RedisListAsyncCommands<K, V>, RedisSetAsyncCommands<K, V>,
        RedisSortedSetAsyncCommands<K, V>, RedisScriptingAsyncCommands<K, V>, RedisServerAsyncCommands<K, V>,
        RedisHLLAsyncCommands<K, V>, BaseRedisAsyncCommands<K, V>, RedisClusterAsyncCommands<K, V>,
        RedisTransactionalAsyncCommands<K, V>, RedisGeoAsyncCommands<K, V>, RedisAsyncConnection<K, V> {

    /**
     * Set the default timeout for operations.
     * 
     * @param timeout the timeout value
     * @param unit the unit of the timeout value
     */
    void setTimeout(long timeout, TimeUnit unit);

    /**
     * Authenticate to the server.
     * 
     * @param password the password
     * @return String simple-string-reply
     */
    String auth(String password);

    /**
     * Change the selected database for the current connection.
     * 
     * @param db the database number
     * @return String simple-string-reply
     */
    String select(int db);

    /**
     * Swap two Redis databases, so that immediately all the clients connected to a given DB will see the data of the other DB,
     * and the other way around
     *
     * @param db1 the first database number
     * @param db2 the second database number
     * @return String simple-string-reply
     */
    RedisFuture<String> swapdb(int db1, int db2);

    /**
     * @return the underlying connection.
     */
    StatefulRedisConnection<K, V> getStatefulConnection();

}
