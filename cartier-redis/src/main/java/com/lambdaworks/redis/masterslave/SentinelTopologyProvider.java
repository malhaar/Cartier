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
package com.lambdaworks.redis.masterslave;

import static com.lambdaworks.redis.masterslave.MasterSlaveUtils.CODEC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisException;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.internal.LettuceAssert;
import com.lambdaworks.redis.models.role.RedisInstance;
import com.lambdaworks.redis.models.role.RedisNodeDescription;
import com.lambdaworks.redis.sentinel.api.StatefulRedisSentinelConnection;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Topology provider using Redis Sentinel and the Sentinel API.
 *
 * @author Mark Paluch
 * @since 4.1
 */
public class SentinelTopologyProvider implements TopologyProvider {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SentinelTopologyProvider.class);

    private final String masterId;
    private final RedisClient redisClient;
    private final RedisURI sentinelUri;
    private final long timeout;
    private final TimeUnit timeUnit;

    /**
     * Creates a new {@link SentinelTopologyProvider}.
     *
     * @param masterId must not be empty
     * @param redisClient must not be {@literal null}.
     * @param sentinelUri must not be {@literal null}.
     */
    public SentinelTopologyProvider(String masterId, RedisClient redisClient, RedisURI sentinelUri) {

        LettuceAssert.notEmpty(masterId, "MasterId must not be empty");
        LettuceAssert.notNull(redisClient, "RedisClient must not be null");
        LettuceAssert.notNull(sentinelUri, "Sentinel URI must not be null");

        this.masterId = masterId;
        this.redisClient = redisClient;
        this.sentinelUri = sentinelUri;
        this.timeout = sentinelUri.getTimeout();
        this.timeUnit = sentinelUri.getUnit();
    }

    @Override
    public List<RedisNodeDescription> getNodes() {

        logger.debug("lookup topology for masterId {}", masterId);

        try (StatefulRedisSentinelConnection<String, String> connection = redisClient.connectSentinel(CODEC, sentinelUri)) {

            RedisFuture<Map<String, String>> masterFuture = connection.async().master(masterId);
            RedisFuture<List<Map<String, String>>> slavesFuture = connection.async().slaves(masterId);

            List<RedisNodeDescription> result = new ArrayList<>();
            try {
                Map<String, String> master = masterFuture.get(timeout, timeUnit);
                List<Map<String, String>> slaves = slavesFuture.get(timeout, timeUnit);

                result.add(toNode(master, RedisInstance.Role.MASTER));
                result.addAll(slaves.stream().filter(SentinelTopologyProvider::isAvailable)
                        .map(map -> toNode(map, RedisInstance.Role.SLAVE)).collect(Collectors.toList()));

            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                throw new RedisException(e);
            }

            return result;
        }
    }

    private static boolean isAvailable(Map<String, String> map) {

        String flags = map.get("flags");
        if (flags != null) {
            if (flags.contains("s_down") || flags.contains("o_down") || flags.contains("disconnected")) {
                return false;
            }
        }
        return true;
    }

    private RedisNodeDescription toNode(Map<String, String> map, RedisInstance.Role role) {

        String ip = map.get("ip");
        String port = map.get("port");
        return new RedisMasterSlaveNode(ip, Integer.parseInt(port), sentinelUri, role);
    }

}
