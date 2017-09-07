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
package com.lambdaworks.redis.pubsub.api.async;

import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;

/**
 * Asynchronous and thread-safe Redis PubSub API.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 * @author Mark Paluch
 * @since 3.0
 */
public interface RedisPubSubAsyncCommands<K, V> extends RedisAsyncCommands<K, V>, RedisPubSubConnection<K, V> {

    /**
     * Add a new listener.
     * 
     * @param listener Listener.
     * @deprecated Use {@link #getStatefulConnection()} and
     *             {@link StatefulRedisPubSubConnection#addListener(RedisPubSubListener)}.
     */
    @Deprecated
    void addListener(RedisPubSubListener<K, V> listener);

    /**
     * Remove an existing listener.
     * 
     * @param listener Listener.
     * @deprecated Use {@link #getStatefulConnection()} and
     *             {@link StatefulRedisPubSubConnection#removeListener(RedisPubSubListener)}.
     */
    @Deprecated
    void removeListener(RedisPubSubListener<K, V> listener);

    /**
     * Listen for messages published to channels matching the given patterns.
     * 
     * @param patterns the patterns
     * @return RedisFuture&lt;Void&gt; Future to synchronize {@code psubscribe} completion
     */
    RedisFuture<Void> psubscribe(K... patterns);

    /**
     * Stop listening for messages posted to channels matching the given patterns.
     * 
     * @param patterns the patterns
     * @return RedisFuture&lt;Void&gt; Future to synchronize {@code punsubscribe} completion
     */
    RedisFuture<Void> punsubscribe(K... patterns);

    /**
     * Listen for messages published to the given channels.
     * 
     * @param channels the channels
     * @return RedisFuture&lt;Void&gt; Future to synchronize {@code subscribe} completion
     */
    RedisFuture<Void> subscribe(K... channels);

    /**
     * Stop listening for messages posted to the given channels.
     * 
     * @param channels the channels
     * @return RedisFuture&lt;Void&gt; Future to synchronize {@code unsubscribe} completion.
     */
    RedisFuture<Void> unsubscribe(K... channels);

    /**
     * @return the underlying connection.
     */
    StatefulRedisPubSubConnection<K, V> getStatefulConnection();
}
