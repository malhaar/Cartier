/*
 * Copyright 2011-2017 the original author or authors.
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Consumer;

import com.lambdaworks.redis.RedisCommandExecutionException;
import com.lambdaworks.redis.RedisCommandInterruptedException;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.internal.LettuceAssert;
import com.lambdaworks.redis.output.CommandOutput;

import io.netty.buffer.ByteBuf;

/**
 * An asynchronous redis command and its result. All successfully executed commands will eventually return a
 * {@link CommandOutput} object.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 * @param <T> Command output type.
 *
 * @author Mark Paluch
 */
public class AsyncCommand<K, V, T> extends CompletableFuture<T> implements RedisCommand<K, V, T>, RedisFuture<T>,
        CompleteableCommand<T>, DecoratedCommand<K, V, T> {

    private static final AtomicIntegerFieldUpdater<AsyncCommand> COUNT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
            AsyncCommand.class, "count");

    private final RedisCommand<K, V, T> command;

    private volatile int count = 1;

    /**
     * @param command the command, must not be {@literal null}.
     */
    public AsyncCommand(RedisCommand<K, V, T> command) {
        this(command, 1);
    }

    /**
     * @param command the command, must not be {@literal null}.
     */
    protected AsyncCommand(RedisCommand<K, V, T> command, int count) {
        LettuceAssert.notNull(command, "RedisCommand must not be null");
        this.command = command;
        this.count = count;
    }

    /**
     * Wait up to the specified time for the command output to become available.
     *
     * @param timeout Maximum time to wait for a result.
     * @param unit Unit of time for the timeout.
     *
     * @return true if the output became available.
     */
    @Override
    public boolean await(long timeout, TimeUnit unit) {
        try {
            get(timeout, unit);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RedisCommandInterruptedException(e);
        } catch (ExecutionException e) {
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Get the object that holds this command's output.
     *
     * @return The command output object.
     */
    @Override
    public CommandOutput<K, V, T> getOutput() {
        return command.getOutput();
    }

    /**
     * Mark this command complete and notify all waiting threads.
     */
    @Override
    public void complete() {
        if (COUNT_UPDATER.decrementAndGet(this) == 0) {
            completeResult();
            command.complete();
        }
    }

    protected void completeResult() {
        if (command.getOutput() == null) {
            complete(null);
        } else if (command.getOutput().hasError()) {
            doCompleteExceptionally(new RedisCommandExecutionException(command.getOutput().getError()));
        } else {
            complete(command.getOutput().get());
        }
    }

    @Override
    public boolean completeExceptionally(Throwable ex) {
        boolean result = false;
        if (COUNT_UPDATER.decrementAndGet(this) == 0) {
            result = doCompleteExceptionally(ex);
        }
        return result;
    }

    private boolean doCompleteExceptionally(Throwable ex) {
        command.completeExceptionally(ex);
        return super.completeExceptionally(ex);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            command.cancel();
            return super.cancel(mayInterruptIfRunning);
        } finally {
            COUNT_UPDATER.set(this, 0);
        }
    }

    @Override
    public String getError() {
        return command.getOutput().getError();
    }

    @Override
    public CommandArgs<K, V> getArgs() {
        return command.getArgs();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [type=").append(getType());
        sb.append(", output=").append(getOutput());
        sb.append(", commandType=").append(command.getClass().getName());
        sb.append(']');
        return sb.toString();
    }

    @Override
    public ProtocolKeyword getType() {
        return command.getType();
    }

    @Override
    public void cancel() {
        cancel(true);
    }

    @Override
    public void encode(ByteBuf buf) {
        command.encode(buf);
    }

    @Override
    public void setOutput(CommandOutput<K, V, T> output) {
        command.setOutput(output);
    }

    @Override
    public void onComplete(Consumer<? super T> action) {
        thenAccept(action);
    }

    @Override
    public RedisCommand<K, V, T> getDelegate() {
        return command;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof RedisCommand)) {
            return false;
        }

        RedisCommand<?, ?, ?> left = command;
        while (left instanceof DecoratedCommand) {
            left = CommandWrapper.unwrap(left);
        }

        RedisCommand<?, ?, ?> right = (RedisCommand<?, ?, ?>) o;
        while (right instanceof DecoratedCommand) {
            right = CommandWrapper.unwrap(right);
        }

        return left == right;
    }

    @Override
    public int hashCode() {

        RedisCommand<?, ?, ?> toHash = command;
        while (toHash instanceof DecoratedCommand) {
            toHash = CommandWrapper.unwrap(toHash);
        }

        return toHash != null ? toHash.hashCode() : 0;
    }
}
