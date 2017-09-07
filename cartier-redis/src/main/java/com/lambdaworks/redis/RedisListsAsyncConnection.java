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

import java.util.List;

import com.lambdaworks.redis.api.async.RedisListAsyncCommands;
import com.lambdaworks.redis.output.ValueStreamingChannel;

/**
 * Asynchronous executed commands for Lists.
 * 
 * @param <K> Key type.
 * @param <V> Value type.
 * @author Mark Paluch
 * @since 3.0
 * @deprecated Use {@link RedisListAsyncCommands}
 */
@Deprecated
public interface RedisListsAsyncConnection<K, V> {

    /**
     * Remove and get the first element in a list, or block until one is available.
     * 
     * @param timeout the timeout in seconds
     * @param keys the keys
     * @return RedisFuture&lt;KeyValue&lt;K,V&gt;&gt; array-reply specifically:
     * 
     *         A {@literal null} multi-bulk when no element could be popped and the timeout expired. A two-element multi-bulk
     *         with the first element being the name of the key where an element was popped and the second element being the
     *         value of the popped element.
     */
    RedisFuture<KeyValue<K, V>> blpop(long timeout, K... keys);

    /**
     * Remove and get the last element in a list, or block until one is available.
     * 
     * @param timeout the timeout in seconds
     * @param keys the keys
     * @return RedisFuture&lt;KeyValue&lt;K,V&gt;&gt; array-reply specifically:
     * 
     *         A {@literal null} multi-bulk when no element could be popped and the timeout expired. A two-element multi-bulk
     *         with the first element being the name of the key where an element was popped and the second element being the
     *         value of the popped element.
     */
    RedisFuture<KeyValue<K, V>> brpop(long timeout, K... keys);

    /**
     * Pop a value from a list, push it to another list and return it; or block until one is available.
     * 
     * @param timeout the timeout in seconds
     * @param source the source key
     * @param destination the destination type: key
     * @return RedisFuture&lt;V&gt; bulk-string-reply the element being popped from {@code source} and pushed to
     *         {@code destination}. If {@code timeout} is reached, a
     */
    RedisFuture<V> brpoplpush(long timeout, K source, K destination);

    /**
     * Get an element from a list by its index.
     * 
     * @param key the key
     * @param index the index type: long
     * @return RedisFuture&lt;V&gt; bulk-string-reply the requested element, or {@literal null} when {@code index} is out of
     *         range.
     */
    RedisFuture<V> lindex(K key, long index);

    /**
     * Insert an element before or after another element in a list.
     * 
     * @param key the key
     * @param before the before
     * @param pivot the pivot
     * @param value the value
     * @return RedisFuture&lt;Long&gt; integer-reply the length of the list after the insert operation, or {@code -1} when the
     *         value {@code pivot} was not found.
     */
    RedisFuture<Long> linsert(K key, boolean before, V pivot, V value);

    /**
     * Get the length of a list.
     * 
     * @param key the key
     * @return RedisFuture&lt;Long&gt; integer-reply the length of the list at {@code key}.
     */
    RedisFuture<Long> llen(K key);

    /**
     * Remove and get the first element in a list.
     * 
     * @param key the key
     * @return RedisFuture&lt;V&gt; bulk-string-reply the value of the first element, or {@literal null} when {@code key} does
     *         not exist.
     */
    RedisFuture<V> lpop(K key);

    /**
     * Prepend one or multiple values to a list.
     * 
     * @param key the key
     * @param values the value
     * @return RedisFuture&lt;Long&gt; integer-reply the length of the list after the push operations.
     */
    RedisFuture<Long> lpush(K key, V... values);

    /**
     * Prepend a value to a list, only if the list exists.
     * 
     * @param key the key
     * @param value the value
     * @return RedisFuture&lt;Long&gt; integer-reply the length of the list after the push operation.
     * @deprecated Use {@link #lpushx(Object, Object[])}
     */
    @Deprecated
    RedisFuture<Long> lpushx(K key, V value);

    /**
     * Prepend values to a list, only if the list exists.
     *
     * @param key the key
     * @param values the values
     * @return Long integer-reply the length of the list after the push operation.
     */
    RedisFuture<Long> lpushx(K key, V... values);

    /**
     * Get a range of elements from a list.
     * 
     * @param key the key
     * @param start the start type: long
     * @param stop the stop type: long
     * @return RedisFuture&lt;List&lt;V&gt;&gt; array-reply list of elements in the specified range.
     */
    RedisFuture<List<V>> lrange(K key, long start, long stop);

    /**
     * Get a range of elements from a list.
     * 
     * @param channel the channel
     * @param key the key
     * @param start the start type: long
     * @param stop the stop type: long
     * @return RedisFuture&lt;Long&gt; count of elements in the specified range.
     */
    RedisFuture<Long> lrange(ValueStreamingChannel<V> channel, K key, long start, long stop);

    /**
     * Remove elements from a list.
     * 
     * @param key the key
     * @param count the count type: long
     * @param value the value
     * @return RedisFuture&lt;Long&gt; integer-reply the number of removed elements.
     */
    RedisFuture<Long> lrem(K key, long count, V value);

    /**
     * Set the value of an element in a list by its index.
     * 
     * @param key the key
     * @param index the index type: long
     * @param value the value
     * @return RedisFuture&lt;String&gt; simple-string-reply
     */
    RedisFuture<String> lset(K key, long index, V value);

    /**
     * Trim a list to the specified range.
     * 
     * @param key the key
     * @param start the start type: long
     * @param stop the stop type: long
     * @return RedisFuture&lt;String&gt; simple-string-reply
     */
    RedisFuture<String> ltrim(K key, long start, long stop);

    /**
     * Remove and get the last element in a list.
     * 
     * @param key the key
     * @return RedisFuture&lt;V&gt; bulk-string-reply the value of the last element, or {@literal null} when {@code key} does
     *         not exist.
     */
    RedisFuture<V> rpop(K key);

    /**
     * Remove the last element in a list, append it to another list and return it.
     * 
     * @param source the source key
     * @param destination the destination type: key
     * @return RedisFuture&lt;V&gt; bulk-string-reply the element being popped and pushed.
     */
    RedisFuture<V> rpoplpush(K source, K destination);

    /**
     * Append one or multiple values to a list.
     * 
     * @param key the key
     * @param values the value
     * @return RedisFuture&lt;Long&gt; integer-reply the length of the list after the push operation.
     */
    RedisFuture<Long> rpush(K key, V... values);

    /**
     * Append a value to a list, only if the list exists.
     * 
     * @param key the key
     * @param value the value
     * @return RedisFuture&lt;Long&gt; integer-reply the length of the list after the push operation.
     * @deprecated Use {@link #rpushx(java.lang.Object, java.lang.Object[])}
     */
    @Deprecated
    RedisFuture<Long> rpushx(K key, V value);

    /**
     * Append values to a list, only if the list exists.
     *
     * @param key the key
     * @param values the values
     * @return RedisFuture&lt;Long&gt; integer-reply the length of the list after the push operation.
     */
    RedisFuture<Long> rpushx(K key, V... values);
}
