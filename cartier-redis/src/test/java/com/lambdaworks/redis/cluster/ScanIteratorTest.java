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
package com.lambdaworks.redis.cluster;

import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lambdaworks.KeysAndValues;
import com.lambdaworks.redis.KeyValue;
import com.lambdaworks.redis.ScanArgs;
import com.lambdaworks.redis.ScanIterator;
import com.lambdaworks.redis.ScoredValue;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;
import com.lambdaworks.redis.cluster.api.sync.RedisAdvancedClusterCommands;

/**
 * @author Mark Paluch
 */
public class ScanIteratorTest extends AbstractClusterTest {

    private StatefulRedisClusterConnection<String, String> connection;
    private RedisAdvancedClusterCommands<String, String> redis;

    @Before
    public void before() throws Exception {

        this.connection = clusterClient.connect();
        this.redis = this.connection.sync();
    }

    @After
    public void tearDown() throws Exception {
        this.connection.close();
    }

    @Test
    public void scanShouldThrowNoSuchElementExceptionOnEmpty() throws Exception {

        redis.mset(KeysAndValues.MAP);

        ScanIterator<String> scan = ScanIterator.scan(redis, ScanArgs.Builder.limit(50).match("key-foo"));

        assertThat(scan.hasNext()).isFalse();
        try {
            scan.next();
            fail("Missing NoSuchElementException");
        } catch (NoSuchElementException e) {
            assertThat(e).isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    public void keysSinglePass() throws Exception {

        redis.mset(KeysAndValues.MAP);

        ScanIterator<String> scan = ScanIterator.scan(redis, ScanArgs.Builder.limit(50).match("key-11*"));

        assertThat(scan.hasNext()).isTrue();
        assertThat(scan.hasNext()).isTrue();

        for (int i = 0; i < 11; i++) {
            assertThat(scan.hasNext()).isTrue();
            assertThat(scan.next()).isNotNull();
        }

        assertThat(scan.hasNext()).isFalse();
    }

    @Test
    public void keysMultiPass() throws Exception {

        redis.mset(KeysAndValues.MAP);

        ScanIterator<String> scan = ScanIterator.scan(redis);

        List<String> keys = scan.stream().collect(Collectors.toList());

        assertThat(keys).containsAll(KeysAndValues.KEYS);
    }

    @Test
    public void hscanShouldThrowNoSuchElementExceptionOnEmpty() throws Exception {

        redis.mset(KeysAndValues.MAP);

        ScanIterator<KeyValue<String, String>> scan = ScanIterator.hscan(redis, "none",
                ScanArgs.Builder.limit(50).match("key-foo"));

        assertThat(scan.hasNext()).isFalse();
        try {
            scan.next();
            fail("Missing NoSuchElementException");
        } catch (NoSuchElementException e) {
            assertThat(e).isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    public void hashSinglePass() throws Exception {

        redis.hmset(key, KeysAndValues.MAP);

        ScanIterator<KeyValue<String, String>> scan = ScanIterator.hscan(redis, key,
                ScanArgs.Builder.limit(50).match("key-11*"));

        assertThat(scan.hasNext()).isTrue();
        assertThat(scan.hasNext()).isTrue();

        for (int i = 0; i < 11; i++) {
            assertThat(scan.hasNext()).isTrue();
            assertThat(scan.next()).isNotNull();
        }

        assertThat(scan.hasNext()).isFalse();
    }

    @Test
    public void hashMultiPass() throws Exception {

        redis.hmset(key, KeysAndValues.MAP);

        ScanIterator<KeyValue<String, String>> scan = ScanIterator.hscan(redis, key);

        List<KeyValue<String, String>> keys = scan.stream().collect(Collectors.toList());

        assertThat(keys).containsAll(
                KeysAndValues.KEYS.stream().map(s -> new KeyValue<>(s, KeysAndValues.MAP.get(s))).collect(Collectors.toList()));
    }

    @Test
    public void sscanShouldThrowNoSuchElementExceptionOnEmpty() throws Exception {

        redis.sadd(key, KeysAndValues.VALUES.toArray(new String[0]));

        ScanIterator<String> scan = ScanIterator.sscan(redis, "none", ScanArgs.Builder.limit(50).match("key-foo"));

        assertThat(scan.hasNext()).isFalse();
        try {
            scan.next();
            fail("Missing NoSuchElementException");
        } catch (NoSuchElementException e) {
            assertThat(e).isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    public void setSinglePass() throws Exception {

        redis.sadd(key, KeysAndValues.KEYS.toArray(new String[0]));

        ScanIterator<String> scan = ScanIterator.sscan(redis, key, ScanArgs.Builder.limit(50).match("key-11*"));

        assertThat(scan.hasNext()).isTrue();
        assertThat(scan.hasNext()).isTrue();

        for (int i = 0; i < 11; i++) {
            assertThat(scan.hasNext()).isTrue();
            assertThat(scan.next()).isNotNull();
        }

        assertThat(scan.hasNext()).isFalse();
    }

    @Test
    public void setMultiPass() throws Exception {

        redis.sadd(key, KeysAndValues.KEYS.toArray(new String[0]));

        ScanIterator<String> scan = ScanIterator.sscan(redis, key);

        List<String> values = scan.stream().collect(Collectors.toList());

        assertThat(values).containsAll(values);
    }

    @Test
    public void zscanShouldThrowNoSuchElementExceptionOnEmpty() throws Exception {

        for (int i = 0; i < KeysAndValues.COUNT; i++) {
            redis.zadd(key, new ScoredValue<>(i, KeysAndValues.KEYS.get(i)));
        }

        ScanIterator<ScoredValue<String>> scan = ScanIterator.zscan(redis, "none", ScanArgs.Builder.limit(50).match("key-foo"));

        assertThat(scan.hasNext()).isFalse();
        try {
            scan.next();
            fail("Missing NoSuchElementException");
        } catch (NoSuchElementException e) {
            assertThat(e).isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    public void zsetSinglePass() throws Exception {

        for (int i = 0; i < KeysAndValues.COUNT; i++) {
            redis.zadd(key, new ScoredValue<>(i, KeysAndValues.KEYS.get(i)));
        }

        ScanIterator<ScoredValue<String>> scan = ScanIterator.zscan(redis, key, ScanArgs.Builder.limit(50).match("key-11*"));

        assertThat(scan.hasNext()).isTrue();
        assertThat(scan.hasNext()).isTrue();

        for (int i = 0; i < 11; i++) {
            assertThat(scan.hasNext()).isTrue();
            assertThat(scan.next()).isNotNull();
        }

        assertThat(scan.hasNext()).isFalse();
    }

    @Test
    public void zsetMultiPass() throws Exception {

        List<ScoredValue<String>> expected = new ArrayList<>();
        for (int i = 0; i < KeysAndValues.COUNT; i++) {
            ScoredValue<String> scoredValue = new ScoredValue<>(i, KeysAndValues.KEYS.get(i));
            expected.add(scoredValue);
            redis.zadd(key, scoredValue);
        }

        ScanIterator<ScoredValue<String>> scan = ScanIterator.zscan(redis, key);

        List<ScoredValue<String>> values = scan.stream().collect(Collectors.toList());

        assertThat(values).containsAll(values);
    }
}