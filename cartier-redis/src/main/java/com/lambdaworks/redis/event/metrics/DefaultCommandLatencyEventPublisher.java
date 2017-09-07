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
package com.lambdaworks.redis.event.metrics;

import com.lambdaworks.redis.event.EventBus;
import com.lambdaworks.redis.event.EventPublisherOptions;
import com.lambdaworks.redis.metrics.CommandLatencyCollector;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ScheduledFuture;

/**
 * Default implementation of a {@link CommandLatencyCollector} for command latencies.
 * 
 * @author Mark Paluch
 */
public class DefaultCommandLatencyEventPublisher implements MetricEventPublisher {

    private final EventExecutorGroup eventExecutorGroup;
    private final EventPublisherOptions options;
    private final EventBus eventBus;
    private final CommandLatencyCollector commandLatencyCollector;

    private final Runnable EMITTER = new Runnable() {
        @Override
        public void run() {
            emitMetricsEvent();
        }
    };

    private volatile ScheduledFuture<?> scheduledFuture;

    public DefaultCommandLatencyEventPublisher(EventExecutorGroup eventExecutorGroup, EventPublisherOptions options,
            EventBus eventBus, CommandLatencyCollector commandLatencyCollector) {
        this.eventExecutorGroup = eventExecutorGroup;
        this.options = options;
        this.eventBus = eventBus;
        this.commandLatencyCollector = commandLatencyCollector;

        if (options.eventEmitInterval() > 0) {
            scheduledFuture = this.eventExecutorGroup.scheduleAtFixedRate(EMITTER, options.eventEmitInterval(),
                    options.eventEmitInterval(), options.eventEmitIntervalUnit());
        }
    }

    @Override
    public boolean isEnabled() {
        return options.eventEmitInterval() > 0 && scheduledFuture != null;
    }

    @Override
    public void shutdown() {

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }

    @Override
    public void emitMetricsEvent() {

        if (!isEnabled() || !commandLatencyCollector.isEnabled()) {
            return;
        }

        eventBus.publish(new CommandLatencyEvent(commandLatencyCollector.retrieveMetrics()));
    }

}
