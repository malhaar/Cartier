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
package com.lambdaworks.redis.protocol;

import static com.lambdaworks.redis.protocol.RedisStateMachine.State;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lambdaworks.redis.RedisException;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.codec.Utf8StringCodec;
import com.lambdaworks.redis.output.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Will Glozer
 * @author Mark Paluch
 */
public class StateMachineTest {
    protected RedisCodec<String, String> codec = new Utf8StringCodec();
    protected Charset charset = Charset.forName("UTF-8");
    protected CommandOutput<String, String, String> output;
    protected RedisStateMachine<String, String> rsm;

    @BeforeClass
    public static void beforeClass() {

        LoggerContext ctx = (LoggerContext) LogManager.getContext();
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(RedisStateMachine.class.getName());
        loggerConfig.setLevel(Level.ALL);
    }

    @AfterClass
    public static void afterClass() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext();
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(RedisStateMachine.class.getName());
        loggerConfig.setLevel(null);
    }

    @Before
    public final void createStateMachine() throws Exception {
        output = new StatusOutput<>(codec);
        rsm = new RedisStateMachine<>();
    }

    @Test
    public void single() throws Exception {
        assertThat(rsm.decode(buffer("+OK\r\n"), output)).isTrue();
        assertThat(output.get()).isEqualTo("OK");
    }

    @Test
    public void error() throws Exception {
        assertThat(rsm.decode(buffer("-ERR\r\n"), output)).isTrue();
        assertThat(output.getError()).isEqualTo("ERR");
    }

    @Test
    public void errorWithoutLineBreak() throws Exception {
        assertThat(rsm.decode(buffer("-ERR"), output)).isFalse();
        assertThat(rsm.decode(buffer("\r\n"), output)).isTrue();
        assertThat(output.getError()).isEqualTo("");
    }

    @Test
    public void integer() throws Exception {
        CommandOutput<String, String, Long> output = new IntegerOutput<>(codec);
        assertThat(rsm.decode(buffer(":1\r\n"), output)).isTrue();
        assertThat((long) output.get()).isEqualTo(1);
    }

    @Test
    public void bulk() throws Exception {
        CommandOutput<String, String, String> output = new ValueOutput<>(codec);
        assertThat(rsm.decode(buffer("$-1\r\n"), output)).isTrue();
        assertThat(output.get()).isNull();
        assertThat(rsm.decode(buffer("$3\r\nfoo\r\n"), output)).isTrue();
        assertThat(output.get()).isEqualTo("foo");
    }

    @Test
    public void multi() throws Exception {
        CommandOutput<String, String, List<String>> output = new ValueListOutput<>(codec);
        ByteBuf buffer = buffer("*2\r\n$-1\r\n$2\r\nok\r\n");
        assertThat(rsm.decode(buffer, output)).isTrue();
        assertThat(output.get()).isEqualTo(Arrays.asList(null, "ok"));
    }

    @Test
    public void multiEmptyArray1() throws Exception {
        CommandOutput<String, String, List<Object>> output = new NestedMultiOutput<>(codec);
        ByteBuf buffer = buffer("*2\r\n$3\r\nABC\r\n*0\r\n");
        assertThat(rsm.decode(buffer, output)).isTrue();
        assertThat(output.get().get(0)).isEqualTo("ABC");
        assertThat(output.get().get(1)).isEqualTo(Arrays.asList());
        assertThat(output.get().size()).isEqualTo(2);
    }

    @Test
    public void multiEmptyArray2() throws Exception {
        CommandOutput<String, String, List<Object>> output = new NestedMultiOutput<>(codec);
        ByteBuf buffer = buffer("*2\r\n*0\r\n$3\r\nABC\r\n");
        assertThat(rsm.decode(buffer, output)).isTrue();
        assertThat(output.get().get(0)).isEqualTo(Arrays.asList());
        assertThat(output.get().get(1)).isEqualTo("ABC");
        assertThat(output.get().size()).isEqualTo(2);
    }

    @Test
    public void multiEmptyArray3() throws Exception {
        CommandOutput<String, String, List<Object>> output = new NestedMultiOutput<>(codec);
        ByteBuf buffer = buffer("*2\r\n*2\r\n$2\r\nAB\r\n$2\r\nXY\r\n*0\r\n");
        assertThat(rsm.decode(buffer, output)).isTrue();
        assertThat(output.get().get(0)).isEqualTo(Arrays.asList("AB", "XY"));
        assertThat(output.get().get(1)).isEqualTo(Arrays.asList());
        assertThat(output.get().size()).isEqualTo(2);
    }

    @Test
    public void partialFirstLine() throws Exception {
        assertThat(rsm.decode(buffer("+"), output)).isFalse();
        assertThat(rsm.decode(buffer("-"), output)).isFalse();
        assertThat(rsm.decode(buffer(":"), output)).isFalse();
        assertThat(rsm.decode(buffer("$"), output)).isFalse();
        assertThat(rsm.decode(buffer("*"), output)).isFalse();
    }

    @Test(expected = RedisException.class)
    public void invalidReplyType() throws Exception {
        rsm.decode(buffer("="), output);
    }

    @Test
    public void sillyTestsForEmmaCoverage() throws Exception {
        assertThat(State.Type.valueOf("SINGLE")).isEqualTo(State.Type.SINGLE);
    }

    protected ByteBuf buffer(String content) {
        return Unpooled.copiedBuffer(content, charset);
    }
}
