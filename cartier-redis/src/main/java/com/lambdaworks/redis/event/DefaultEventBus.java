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
package com.lambdaworks.redis.event;

import rx.Observable;
import rx.Scheduler;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Default implementation for an {@link EventBus}. Events are published using a {@link Scheduler}.
 * 
 * @author Mark Paluch
 * @since 3.4
 */
public class DefaultEventBus implements EventBus {

    private final Subject<Event, Event> bus;
    private final Scheduler scheduler;

    public DefaultEventBus(Scheduler scheduler) {
        this.bus = PublishSubject.<Event> create().toSerialized();
        this.scheduler = scheduler;
    }

    @Override
    public Observable<Event> get() {
        return bus.onBackpressureDrop().observeOn(scheduler);
    }

    @Override
    public void publish(Event event) {
        if (bus.hasObservers()) {
            bus.onNext(event);
        }
    }
}
