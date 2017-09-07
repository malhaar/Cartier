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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.lambdaworks.redis.internal.LettuceSets;
import org.junit.Test;

/**
 * @author Mark Paluch
 */
public class RedisURITest {

    @Test
    public void equalsTest() {

        RedisURI redisURI1 = RedisURI.create("redis://auth@localhost:1234/5");
        RedisURI redisURI2 = RedisURI.create("redis://auth@localhost:1234/5");
        RedisURI redisURI3 = RedisURI.create("redis://auth@localhost:1231/5");

        assertThat(redisURI1).isEqualTo(redisURI2);
        assertThat(redisURI1.hashCode()).isEqualTo(redisURI2.hashCode());
        assertThat(redisURI1.toString()).contains("localhost").contains("1234");

        assertThat(redisURI3).isNotEqualTo(redisURI2);
        assertThat(redisURI3.hashCode()).isNotEqualTo(redisURI2.hashCode());
    }

    @Test
    public void setUsage() {

        RedisURI redisURI1 = RedisURI.create("redis://auth@localhost:1234/5");
        RedisURI redisURI2 = RedisURI.create("redis://auth@localhost:1234/5");
        RedisURI redisURI3 = RedisURI.create("redis://auth@localhost:1234/6");

        Set<RedisURI> set = LettuceSets.unmodifiableSet(redisURI1, redisURI2, redisURI3);

        assertThat(set).hasSize(2);
    }

    @Test
    public void mapUsage() {

        RedisURI redisURI1 = RedisURI.create("redis://auth@localhost:1234/5");
        RedisURI redisURI2 = RedisURI.create("redis://auth@localhost:1234/5");

        Map<RedisURI, String> map = new LinkedHashMap<>();
        map.put(redisURI1, "something");

        assertThat(map.get(redisURI2)).isEqualTo("something");
    }

    @Test
    public void simpleUriTest() {
        RedisURI redisURI = RedisURI.create("redis://localhost:6379");
        assertThat(redisURI.toURI().toString()).isEqualTo("redis://localhost");
    }

    @Test
    public void sslUriTest() {
        RedisURI redisURI = RedisURI.create("redis+ssl://localhost:6379");
        assertThat(redisURI.toURI().toString()).isEqualTo("rediss://localhost:6379");
    }

    @Test
    public void tlsUriTest() {
        RedisURI redisURI = RedisURI.create("redis+tls://localhost:6379");
        assertThat(redisURI.toURI().toString()).isEqualTo("redis+tls://localhost:6379");
    }

    @Test
    public void multipleClusterNodesTest() {
        RedisURI redisURI = RedisURI.create("redis+ssl://password@host1:6379,host2:6380");
        assertThat(redisURI.toURI().toString()).isEqualTo("rediss://password@host1:6379,host2:6380");
    }

    @Test
    public void sentinelEqualsTest() {

        RedisURI redisURI1 = RedisURI.create("redis-sentinel://auth@h1:222,h2,h3:1234/5?sentinelMasterId=masterId");
        RedisURI redisURI2 = RedisURI.create("redis-sentinel://auth@h1:222,h2,h3:1234/5#masterId");
        RedisURI redisURI3 = RedisURI.create("redis-sentinel://auth@h1,h2,h3:1234/5#OtherMasterId");

        assertThat(redisURI1).isEqualTo(redisURI2);
        assertThat(redisURI1.hashCode()).isEqualTo(redisURI2.hashCode());
        assertThat(redisURI1.toString()).contains("h1");

        assertThat(redisURI3).isNotEqualTo(redisURI2);
        assertThat(redisURI3.hashCode()).isNotEqualTo(redisURI2.hashCode());
    }

    @Test
    public void sentinelUriTest() {

        RedisURI redisURI = RedisURI.create("redis-sentinel://auth@h1:222,h2,h3:1234/5?sentinelMasterId=masterId");
        assertThat(redisURI.getSentinelMasterId()).isEqualTo("masterId");
        assertThat(redisURI.getSentinels().get(0).getPort()).isEqualTo(222);
        assertThat(redisURI.getSentinels().get(1).getPort()).isEqualTo(RedisURI.DEFAULT_SENTINEL_PORT);
        assertThat(redisURI.getSentinels().get(2).getPort()).isEqualTo(1234);
        assertThat(redisURI.getDatabase()).isEqualTo(5);

        assertThat(redisURI.toURI().toString()).isEqualTo(
                "redis-sentinel://auth@h1:222,h2,h3:1234?database=5&sentinelMasterId=masterId");
    }

    @Test
    public void socketEqualsTest() {

        RedisURI redisURI1 = RedisURI.create("redis-socket:///var/tmp/socket");
        RedisURI redisURI2 = RedisURI.create("redis-socket:///var/tmp/socket");
        RedisURI redisURI3 = RedisURI.create("redis-socket:///var/tmp/other-socket?db=2");

        assertThat(redisURI1).isEqualTo(redisURI2);
        assertThat(redisURI1.hashCode()).isEqualTo(redisURI2.hashCode());
        assertThat(redisURI1.toString()).contains("/var/tmp/socket");

        assertThat(redisURI3).isNotEqualTo(redisURI2);
        assertThat(redisURI3.hashCode()).isNotEqualTo(redisURI2.hashCode());
    }

    @Test
    public void socketUriTest() {

        RedisURI redisURI = RedisURI.create("redis-socket:///var/tmp/other-socket?db=2");

        assertThat(redisURI.getDatabase()).isEqualTo(2);
        assertThat(redisURI.getSocket()).isEqualTo("/var/tmp/other-socket");
        assertThat(redisURI.toURI().toString()).isEqualTo("redis-socket:///var/tmp/other-socket?database=2");
    }

    @Test
    public void socketAltUriTest() {

        RedisURI redisURI = RedisURI.create("redis+socket:///var/tmp/other-socket?db=2");

        assertThat(redisURI.getDatabase()).isEqualTo(2);
        assertThat(redisURI.getSocket()).isEqualTo("/var/tmp/other-socket");
        assertThat(redisURI.toURI().toString()).isEqualTo("redis-socket:///var/tmp/other-socket?database=2");
    }

    @Test
    public void timeoutParsingTest() {
        checkUriTimeout("redis://auth@localhost:1234/5?timeout=5000", 5000, TimeUnit.MILLISECONDS);
        checkUriTimeout("redis://auth@localhost:1234/5?timeout=5000ms", 5000, TimeUnit.MILLISECONDS);
        checkUriTimeout("redis://auth@localhost:1234/5?timeout=5s", 5, TimeUnit.SECONDS);
        checkUriTimeout("redis://auth@localhost:1234/5?timeout=100us", 100, TimeUnit.MICROSECONDS);
        checkUriTimeout("redis://auth@localhost:1234/5?TIMEOUT=1000000NS", 1000000, TimeUnit.NANOSECONDS);
        checkUriTimeout("redis://auth@localhost:1234/5?timeout=60m", 60, TimeUnit.MINUTES);
        checkUriTimeout("redis://auth@localhost:1234/5?timeout=24h", 24, TimeUnit.HOURS);
        checkUriTimeout("redis://auth@localhost:1234/5?timeout=1d", 1, TimeUnit.DAYS);

        checkUriTimeout("redis://auth@localhost:1234/5?timeout=-1", 0, TimeUnit.MILLISECONDS);

        RedisURI defaultUri = new RedisURI();
        checkUriTimeout("redis://auth@localhost:1234/5?timeout=junk", defaultUri.getTimeout(), defaultUri.getUnit());

        RedisURI redisURI = RedisURI.create("redis://auth@localhost:1234/5?timeout=5000ms");
        assertThat(redisURI.toURI().toString()).isEqualTo("redis://auth@localhost:1234?database=5&timeout=5000ms");
    }

    @Test
    public void queryStringDecodingTest() {
        String timeout = "%74%69%6D%65%6F%75%74";
        String eq = "%3d";
        String s = "%73";
        checkUriTimeout("redis://auth@localhost:1234/5?" + timeout + eq + "5" + s, 5, TimeUnit.SECONDS);
    }

    @Test
    public void timeoutParsingWithJunkParamTest() {
        RedisURI redisURI1 = RedisURI.create("redis-sentinel://auth@localhost:1234/5?timeout=5s;junkparam=#master-instance");
        assertThat(redisURI1.getTimeout()).isEqualTo(5);
        assertThat(redisURI1.getUnit()).isEqualTo(TimeUnit.SECONDS);
        assertThat(redisURI1.getSentinelMasterId()).isEqualTo("master-instance");
    }

    private RedisURI checkUriTimeout(String uri, long expectedTimeout, TimeUnit expectedUnit) {
        RedisURI redisURI1 = RedisURI.create(uri);
        assertThat(redisURI1.getTimeout()).isEqualTo(expectedTimeout);
        assertThat(redisURI1.getUnit()).isEqualTo(expectedUnit);
        return redisURI1;
    }

    @Test
    public void databaseParsingTest() {
        RedisURI redisURI = RedisURI.create("redis://auth@localhost:1234/?database=21");
        assertThat(redisURI.getDatabase()).isEqualTo(21);

        assertThat(redisURI.toURI().toString()).isEqualTo("redis://auth@localhost:1234?database=21");
    }

    @Test
    public void clientNameParsingTest() {
        RedisURI redisURI = RedisURI.create("redis://auth@localhost:1234/?clientName=hello");
        assertThat(redisURI.getClientName()).isEqualTo("hello");

        assertThat(redisURI.toURI().toString()).isEqualTo("redis://auth@localhost:1234?clientName=hello");
    }

    @Test
    public void parsingWithInvalidValuesTest() {
        RedisURI redisURI = RedisURI
                .create("redis://@host:1234/?database=AAA&database=&timeout=&timeout=XYZ&sentinelMasterId=");
        assertThat(redisURI.getDatabase()).isEqualTo(0);
        assertThat(redisURI.getSentinelMasterId()).isNull();

        assertThat(redisURI.toURI().toString()).isEqualTo("redis://host:1234");
    }
}
