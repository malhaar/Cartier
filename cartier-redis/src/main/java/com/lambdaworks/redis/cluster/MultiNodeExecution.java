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
package com.lambdaworks.redis.cluster;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import com.lambdaworks.redis.RedisCommandInterruptedException;
import com.lambdaworks.redis.RedisException;
import com.lambdaworks.redis.RedisFuture;

/**
 * Utility to perform and synchronize command executions on multiple cluster nodes.
 *
 * @author Mark Paluch
 */
class MultiNodeExecution {

    static <T> T execute(Callable<T> function) {
        try {
            return function.call();
        } catch (Exception e) {
            throw new RedisException(e);
        }
    }

    /**
     * Aggregate (sum) results of the {@link RedisFuture}s.
     *
     * @param executions mapping of a key to the future
     * @return future producing an aggregation result
     */
    protected static RedisFuture<Long> aggregateAsync(Map<?, ? extends CompletionStage<Long>> executions) {

        return new PipelinedRedisFuture<>(executions, objectPipelinedRedisFuture -> {
            AtomicLong result = new AtomicLong();
            for (CompletionStage<Long> future : executions.values()) {
                Long value = execute(() -> future.toCompletableFuture().get());
                if (value != null) {
                    result.getAndAdd(value);
                }
            }

            return result.get();
        });
    }

    /**
     * Returns the result of the first {@link RedisFuture} and guarantee that all futures are finished.
     *
     * @param executions mapping of a key to the future
     * @param <T> result type
     * @return future returning the first result.
     */
    protected static <T> RedisFuture<T> firstOfAsync(Map<?, ? extends CompletionStage<T>> executions) {

        return new PipelinedRedisFuture<>(executions, objectPipelinedRedisFuture -> {
            // make sure, that all futures are executed before returning the result.
                for (CompletionStage<T> future : executions.values()) {
                    execute(() -> future.toCompletableFuture().get());
                }
                for (CompletionStage<T> future : executions.values()) {
                    return execute(() -> future.toCompletableFuture().get());
                }
                return null;
            });
    }

    /**
     * Returns always {@literal OK} and guarantee that all futures are finished.
     *
     * @param executions mapping of a key to the future
     * @return future returning the first result.
     */
    protected static RedisFuture<String> alwaysOkOfAsync(Map<?, ? extends CompletionStage<String>> executions) {

        return new PipelinedRedisFuture<>(executions, objectPipelinedRedisFuture -> {
            // make sure, that all futures are executed before returning the result.
                for (CompletionStage<String> future : executions.values()) {
                    try {
                        future.toCompletableFuture().get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RedisCommandInterruptedException(e);
                    } catch (ExecutionException e) {
                        // swallow exceptions
            }
        }
        return "OK";

    }   );
    }
}
