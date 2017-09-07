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
package com.lambdaworks.redis.commands;

import static com.lambdaworks.redis.BitFieldArgs.offset;
import static com.lambdaworks.redis.BitFieldArgs.signed;
import static com.lambdaworks.redis.BitFieldArgs.typeWidthBasedOffset;
import static com.lambdaworks.redis.BitFieldArgs.unsigned;
import static com.lambdaworks.redis.BitFieldArgs.OverflowType.WRAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import java.nio.ByteBuffer;
import java.util.List;

import org.junit.Test;

import com.lambdaworks.RedisConditions;
import com.lambdaworks.redis.AbstractRedisClientTest;
import com.lambdaworks.redis.BitFieldArgs;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.codec.Utf8StringCodec;

/**
 * @author Will Glozer
 * @author Mark Paluch
 */
public class BitCommandTest extends AbstractRedisClientTest {

    protected RedisCommands<String, String> bitstring;

    @Override
    protected RedisCommands<String, String> connect() {
        connectBitString();
        return super.connect();
    }

    protected void connectBitString() {
        bitstring = client.connect(new BitStringCodec()).sync();
    }

    @Override
    public void closeConnection() throws Exception {
        bitstring.close();
        super.closeConnection();
    }

    @Test
    public void bitcount() throws Exception {
        assertThat((long) redis.bitcount(key)).isEqualTo(0);

        redis.setbit(key, 0, 1);
        redis.setbit(key, 1, 1);
        redis.setbit(key, 2, 1);

        assertThat((long) redis.bitcount(key)).isEqualTo(3);
        assertThat(redis.bitcount(key, 3, -1)).isEqualTo(0);
    }

    @Test
    public void bitfieldType() throws Exception {
        assertThat(signed(64).getBits()).isEqualTo(64);
        assertThat(signed(64).isSigned()).isTrue();
        assertThat(unsigned(63).getBits()).isEqualTo(63);
        assertThat(unsigned(63).isSigned()).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void bitfieldTypeSigned65() throws Exception {
        signed(65);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bitfieldTypeUnsigned64() throws Exception {
        unsigned(64);
    }

    @Test(expected = IllegalStateException.class)
    public void bitfieldBuilderEmptyPreviousType() throws Exception {
        new BitFieldArgs().overflow(WRAP).get();
    }

    @Test
    public void bitfieldArgsTest() throws Exception {

        assertThat(signed(5).toString()).isEqualTo("i5");
        assertThat(unsigned(5).toString()).isEqualTo("u5");

        assertThat(offset(5).toString()).isEqualTo("5");
        assertThat(typeWidthBasedOffset(5).toString()).isEqualTo("#5");
    }

    @Test
    public void bitfield() throws Exception {

        assumeTrue(RedisConditions.of(redis).hasCommand("BITFIELD"));

        BitFieldArgs bitFieldArgs = BitFieldArgs.Builder.set(signed(8), 0, 1).set(5, 1).incrBy(2, 3).get().get(2);

        List<Long> values = redis.bitfield(key, bitFieldArgs);

        assertThat(values).containsExactly(0L, 32L, 3L, 0L, 3L);
        assertThat(bitstring.get(key)).isEqualTo("0000000000010011");
    }

    @Test
    public void bitfieldGetWithOffset() throws Exception {

        assumeTrue(RedisConditions.of(redis).hasCommand("BITFIELD"));

        BitFieldArgs bitFieldArgs = BitFieldArgs.Builder.set(signed(8), 0, 1).get(signed(2), typeWidthBasedOffset(1));

        List<Long> values = redis.bitfield(key, bitFieldArgs);

        assertThat(values).containsExactly(0L, 0L);
        assertThat(bitstring.get(key)).isEqualTo("10000000");
    }

    @Test
    public void bitfieldSet() throws Exception {

        assumeTrue(RedisConditions.of(redis).hasCommand("BITFIELD"));

        BitFieldArgs bitFieldArgs = BitFieldArgs.Builder.set(signed(8), 0, 5).set(5);

        List<Long> values = redis.bitfield(key, bitFieldArgs);

        assertThat(values).containsExactly(0L, 5L);
        assertThat(bitstring.get(key)).isEqualTo("10100000");
    }

    @Test
    public void bitfieldWithOffsetSet() throws Exception {

        assumeTrue(RedisConditions.of(redis).hasCommand("BITFIELD"));

        redis.bitfield(key, BitFieldArgs.Builder.set(signed(8), typeWidthBasedOffset(2), 5));
        assertThat(bitstring.get(key)).isEqualTo("000000000000000010100000");

        redis.del(key);
        redis.bitfield(key, BitFieldArgs.Builder.set(signed(8), offset(2), 5));
        assertThat(bitstring.get(key)).isEqualTo("1000000000000010");
    }

    @Test
    public void bitfieldIncrBy() throws Exception {

        assumeTrue(RedisConditions.of(redis).hasCommand("BITFIELD"));

        BitFieldArgs bitFieldArgs = BitFieldArgs.Builder.set(signed(8), 0, 5).incrBy(1);

        List<Long> values = redis.bitfield(key, bitFieldArgs);

        assertThat(values).containsExactly(0L, 6L);
        assertThat(bitstring.get(key)).isEqualTo("01100000");
    }

    @Test
    public void bitfieldWithOffsetIncrBy() throws Exception {

        assumeTrue(RedisConditions.of(redis).hasCommand("BITFIELD"));

        redis.bitfield(key, BitFieldArgs.Builder.incrBy(signed(8), typeWidthBasedOffset(2), 1));
        assertThat(bitstring.get(key)).isEqualTo("000000000000000010000000");

        redis.del(key);
        redis.bitfield(key, BitFieldArgs.Builder.incrBy(signed(8), offset(2), 1));
        assertThat(bitstring.get(key)).isEqualTo("0000000000000010");
    }

    @Test
    public void bitfieldOverflow() throws Exception {

        assumeTrue(RedisConditions.of(redis).hasCommand("BITFIELD"));

        BitFieldArgs bitFieldArgs = BitFieldArgs.Builder.overflow(WRAP).set(signed(8), 9, Integer.MAX_VALUE).get(signed(8));

        List<Long> values = redis.bitfield(key, bitFieldArgs);
        assertThat(values).containsExactly(0L, 0L);
        assertThat(bitstring.get(key)).isEqualTo("000000001111111000000001");
    }

    @Test
    public void bitpos() throws Exception {
        assertThat((long) redis.bitcount(key)).isEqualTo(0);
        redis.setbit(key, 0, 0);
        redis.setbit(key, 1, 1);

        assertThat(bitstring.get(key)).isEqualTo("00000010");
        assertThat((long) redis.bitpos(key, true)).isEqualTo(1);
    }

    @Test
    public void bitposOffset() throws Exception {
        assertThat((long) redis.bitcount(key)).isEqualTo(0);
        redis.setbit(key, 0, 1);
        redis.setbit(key, 1, 1);
        redis.setbit(key, 2, 0);
        redis.setbit(key, 3, 0);
        redis.setbit(key, 4, 0);
        redis.setbit(key, 5, 1);

        assertThat((long) bitstring.getbit(key, 1)).isEqualTo(1);
        assertThat((long) bitstring.getbit(key, 4)).isEqualTo(0);
        assertThat((long) bitstring.getbit(key, 5)).isEqualTo(1);
        assertThat(bitstring.get(key)).isEqualTo("00100011");
        assertThat((long) redis.bitpos(key, false, 0, 0)).isEqualTo(2);
    }

    @Test
    public void bitopAnd() throws Exception {
        redis.setbit("foo", 0, 1);
        redis.setbit("bar", 1, 1);
        redis.setbit("baz", 2, 1);
        assertThat(redis.bitopAnd(key, "foo", "bar", "baz")).isEqualTo(1);
        assertThat((long) redis.bitcount(key)).isEqualTo(0);
        assertThat(bitstring.get(key)).isEqualTo("00000000");
    }

    @Test
    public void bitopNot() throws Exception {
        redis.setbit("foo", 0, 1);
        redis.setbit("foo", 2, 1);

        assertThat(redis.bitopNot(key, "foo")).isEqualTo(1);
        assertThat((long) redis.bitcount(key)).isEqualTo(6);
        assertThat(bitstring.get(key)).isEqualTo("11111010");
    }

    @Test
    public void bitopOr() throws Exception {
        redis.setbit("foo", 0, 1);
        redis.setbit("bar", 1, 1);
        redis.setbit("baz", 2, 1);
        assertThat(redis.bitopOr(key, "foo", "bar", "baz")).isEqualTo(1);
        assertThat(bitstring.get(key)).isEqualTo("00000111");
    }

    @Test
    public void bitopXor() throws Exception {
        redis.setbit("foo", 0, 1);
        redis.setbit("bar", 0, 1);
        redis.setbit("baz", 2, 1);
        assertThat(redis.bitopXor(key, "foo", "bar", "baz")).isEqualTo(1);
        assertThat(bitstring.get(key)).isEqualTo("00000100");
    }

    @Test
    public void getbit() throws Exception {
        assertThat(redis.getbit(key, 0)).isEqualTo(0);
        redis.setbit(key, 0, 1);
        assertThat(redis.getbit(key, 0)).isEqualTo(1);
    }

    @Test
    public void setbit() throws Exception {

        assertThat(redis.setbit(key, 0, 1)).isEqualTo(0);
        assertThat(redis.setbit(key, 0, 0)).isEqualTo(1);
    }

    public static class BitStringCodec extends Utf8StringCodec {
        @Override
        public String decodeValue(ByteBuffer bytes) {
            StringBuilder bits = new StringBuilder(bytes.remaining() * 8);
            while (bytes.remaining() > 0) {
                byte b = bytes.get();
                for (int i = 0; i < 8; i++) {
                    bits.append(Integer.valueOf(b >>> i & 1));
                }
            }
            return bits.toString();
        }
    }
}
