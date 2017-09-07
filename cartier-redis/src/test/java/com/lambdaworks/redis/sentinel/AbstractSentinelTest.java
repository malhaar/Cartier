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
package com.lambdaworks.redis.sentinel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

import com.lambdaworks.redis.AbstractTest;
import com.lambdaworks.redis.FastShutdown;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.sentinel.api.sync.RedisSentinelCommands;

/**
 * @author Mark Paluch
 */
public abstract class AbstractSentinelTest extends AbstractTest {

    public static final String MASTER_ID = "mymaster";

    protected static RedisClient sentinelClient;
    protected RedisSentinelCommands<String, String> sentinel;

    @AfterClass
    public static void shutdownClient() {
        FastShutdown.shutdown(sentinelClient);
    }

    @Before
    public void openConnection() throws Exception {
        sentinel = sentinelClient.connectSentinel().sync();
    }

    @After
    public void closeConnection() throws Exception {
        if (sentinel != null) {
            sentinel.close();
        }
    }

}
