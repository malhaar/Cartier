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

import java.util.Date;
import java.util.List;

import com.lambdaworks.redis.api.sync.RedisServerCommands;
import com.lambdaworks.redis.protocol.CommandType;

/**
 * Synchronous executed commands for Server Control.
 * 
 * @param <K> Key type.
 * @param <V> Value type.
 * @author Mark Paluch
 * @since 3.0
 * @deprecated Use {@link RedisServerCommands}
 */
@Deprecated
public interface RedisServerConnection<K, V> extends RedisServerCommands<K, V> {
    /**
     * Asynchronously rewrite the append-only file.
     * 
     * @return String simple-string-reply always {@code OK}.
     */
    String bgrewriteaof();

    /**
     * Asynchronously save the dataset to disk.
     * 
     * @return String simple-string-reply
     */
    String bgsave();

    /**
     * Get the current connection name.
     * 
     * @return K bulk-string-reply The connection name, or a null bulk reply if no name is set.
     */
    K clientGetname();

    /**
     * Set the current connection name.
     * 
     * @param name the client name
     * @return simple-string-reply {@code OK} if the connection name was successfully set.
     */
    String clientSetname(K name);

    /**
     * Kill the connection of a client identified by ip:port.
     * 
     * @param addr ip:port
     * @return String simple-string-reply {@code OK} if the connection exists and has been closed
     */
    String clientKill(String addr);

    /**
     * Kill connections of clients which are filtered by {@code killArgs}
     *
     * @param killArgs args for the kill operation
     * @return Long integer-reply number of killed connections
     */
    Long clientKill(KillArgs killArgs);

    /**
     * Stop processing commands from clients for some time.
     * 
     * @param timeout the timeout value in milliseconds
     * @return String simple-string-reply The command returns OK or an error if the timeout is invalid.
     */
    String clientPause(long timeout);

    /**
     * Get the list of client connections.
     * 
     * @return String bulk-string-reply a unique string, formatted as follows: One client connection per line (separated by LF),
     *         each line is composed of a succession of property=value fields separated by a space character.
     */
    String clientList();

    /**
     * Returns an array reply of details about all Redis commands.
     * 
     * @return List&lt;Object&gt; array-reply
     */
    List<Object> command();

    /**
     * Returns an array reply of details about the requested commands.
     * 
     * @param commands the commands to query for
     * @return List&lt;Object&gt; array-reply
     */
    List<Object> commandInfo(String... commands);

    /**
     * Returns an array reply of details about the requested commands.
     * 
     * @param commands the commands to query for
     * @return List&lt;Object&gt; array-reply
     */
    List<Object> commandInfo(CommandType... commands);

    /**
     * Get total number of Redis commands.
     * 
     * @return Long integer-reply of number of total commands in this Redis server.
     */
    Long commandCount();

    /**
     * Get the value of a configuration parameter.
     * 
     * @param parameter name of the parameter
     * @return List&lt;String&gt; bulk-string-reply
     */
    List<String> configGet(String parameter);

    /**
     * Reset the stats returned by INFO.
     * 
     * @return String simple-string-reply always {@code OK}.
     */
    String configResetstat();

    /**
     * Rewrite the configuration file with the in memory configuration.
     * 
     * @return String simple-string-reply {@code OK} when the configuration was rewritten properly. Otherwise an error is
     *         returned.
     */
    String configRewrite();

    /**
     * Set a configuration parameter to the given value.
     * 
     * @param parameter the parameter name
     * @param value the parameter value
     * @return String simple-string-reply: {@code OK} when the configuration was set properly. Otherwise an error is returned.
     */
    String configSet(String parameter, String value);

    /**
     * Return the number of keys in the selected database.
     * 
     * @return Long integer-reply
     */
    Long dbsize();

    /**
     * Get debugging information about a key.
     * 
     * @param key the key
     * @return String simple-string-reply
     */
    String debugObject(K key);

    /**
     * Make the server crash: Invalid pointer access.
     */
    void debugSegfault();

    /**
     * Make the server crash: Out of memory.
     */
    void debugOom();

    /**
     * Get debugging information about the internal hash-table state.
     *
     * @param db the database number
     * @return String simple-string-reply
     */
    String debugHtstats(int db);

    /**
     * Remove all keys from all databases.
     * 
     * @return String simple-string-reply
     */
    String flushall();

    /**
     * Remove all keys asynchronously from all databases.
     *
     * @return String simple-string-reply
     */
    String flushallAsync();

    /**
     * Remove all keys from the current database.
     * 
     * @return String simple-string-reply
     */
    String flushdb();

    /**
     * Remove all keys asynchronously from the current database.
     *
     * @return String simple-string-reply
     */
    String flushdbAsync();

    /**
     * Get information and statistics about the server.
     * 
     * @return String bulk-string-reply as a collection of text lines.
     */
    String info();

    /**
     * Get information and statistics about the server.
     * 
     * @param section the section type: string
     * @return String bulk-string-reply as a collection of text lines.
     */
    String info(String section);

    /**
     * Get the UNIX time stamp of the last successful save to disk.
     * 
     * @return Date integer-reply an UNIX time stamp.
     */
    Date lastsave();

    /**
     * Synchronously save the dataset to disk.
     * 
     * @return String simple-string-reply The commands returns OK on success.
     */
    String save();

    /**
     * Synchronously save the dataset to disk and then shut down the server.
     * 
     * @param save {@literal true} force save operation
     */
    void shutdown(boolean save);

    /**
     * Make the server a slave of another instance, or promote it as master.
     * 
     * @param host the host type: string
     * @param port the port type: string
     * @return String simple-string-reply
     */
    String slaveof(String host, int port);

    /**
     * Promote server as master.
     * 
     * @return String simple-string-reply
     */
    String slaveofNoOne();

    /**
     * Read the slow log.
     * 
     * @return List&lt;Object&gt; deeply nested multi bulk replies
     */
    List<Object> slowlogGet();

    /**
     * Read the slow log.
     * 
     * @param count the count
     * @return List&lt;Object&gt; deeply nested multi bulk replies
     */
    List<Object> slowlogGet(int count);

    /**
     * Obtaining the current length of the slow log.
     * 
     * @return Long length of the slow log.
     */
    Long slowlogLen();

    /**
     * Resetting the slow log.
     * 
     * @return String simple-string-reply The commands returns OK on success.
     */
    String slowlogReset();

    /**
     * Internal command used for replication.
     * 
     * @return String simple-string-reply
     */
    @Deprecated
    String sync();

    /**
     * Return the current server time.
     * 
     * @return List&lt;V&gt; array-reply specifically:
     * 
     *         A multi bulk reply containing two elements:
     * 
     *         unix time in seconds. microseconds.
     */
    List<V> time();

}
