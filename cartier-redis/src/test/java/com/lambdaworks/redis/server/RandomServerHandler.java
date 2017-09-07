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
package com.lambdaworks.redis.server;

import java.security.SecureRandom;
import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.base64.Base64;

/**
 * Handler to generate random base64 data.
 */
@ChannelHandler.Sharable
public class RandomServerHandler extends ChannelInboundHandlerAdapter {

    private SecureRandom random = new SecureRandom();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        byte initial[] = new byte[1];
        random.nextBytes(initial);

        byte[] response = new byte[Math.abs((int) initial[0])];

        Arrays.fill(response, "A".getBytes()[0]);

        ByteBuf buf = ctx.alloc().heapBuffer(response.length);

        ByteBuf encoded = buf.writeBytes(response);
        ctx.write(encoded);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
