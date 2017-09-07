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
package com.lambdaworks.redis.commands.rx;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lambdaworks.redis.ClientOptions;
import com.lambdaworks.redis.RedisException;
import com.lambdaworks.redis.api.rx.RedisReactiveCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.commands.TransactionCommandTest;
import com.lambdaworks.redis.internal.LettuceLists;

import rx.Observable;
import rx.observables.BlockingObservable;
import rx.observers.TestSubscriber;

/**
 * @author Mark Paluch
 */
public class TransactionRxCommandTest extends TransactionCommandTest {

    private RedisReactiveCommands<String, String> commands;

    @Override
    protected RedisCommands<String, String> connect() {
        return RxSyncInvocationHandler.sync(client.connectAsync().getStatefulConnection());
    }

    @Before
    public void openConnection() throws Exception {
        client.setOptions(ClientOptions.builder().build());
        redis = connect();
        redis.flushall();
        redis.flushdb();

        commands = redis.getStatefulConnection().reactive();
    }

    @After
    public void closeConnection() throws Exception {
        redis.close();
    }

    @Test
    public void discard() throws Exception {
        assertThat(first(commands.multi())).isEqualTo("OK");

        commands.set(key, value);

        assertThat(first(commands.discard())).isEqualTo("OK");
        assertThat(first(commands.get(key))).isNull();
    }

    @Test
    public void execSingular() throws Exception {

        assertThat(first(commands.multi())).isEqualTo("OK");

        redis.set(key, value);

        assertThat(first(commands.exec())).isEqualTo("OK");
        assertThat(first(commands.get(key))).isEqualTo(value);
    }

    @Test
    public void errorInMulti() throws Exception {
        commands.multi().subscribe();
        commands.set(key, value).subscribe();
        commands.lpop(key).onExceptionResumeNext(Observable.<String> empty()).subscribe();
        commands.get(key).subscribe();

        List<Object> values = all(commands.exec());
        assertThat(values.get(0)).isEqualTo("OK");
        assertThat(values.get(1) instanceof RedisException).isTrue();
        assertThat(values.get(2)).isEqualTo(value);
    }

    @Test
    public void resultOfMultiIsContainedInCommandObservables() throws Exception {

        TestSubscriber<String> set1 = TestSubscriber.create();
        TestSubscriber<String> set2 = TestSubscriber.create();
        TestSubscriber<String> mget = TestSubscriber.create();
        TestSubscriber<Long> llen = TestSubscriber.create();
        TestSubscriber<Object> exec = TestSubscriber.create();

        commands.multi().subscribe();
        commands.set("key1", "value1").subscribe(set1);
        commands.set("key2", "value2").subscribe(set2);
        commands.mget("key1", "key2").subscribe(mget);
        commands.llen("something").subscribe(llen);
        commands.exec().subscribe(exec);

        exec.awaitTerminalEvent();

        set1.assertValue("OK");
        set2.assertValue("OK");
        mget.assertValues("value1", "value2");
        llen.assertValue(0L);
    }

    @Test
    public void resultOfMultiIsContainedInExecObservable() throws Exception {

        TestSubscriber<Object> exec = TestSubscriber.create();

        commands.multi().subscribe();
        commands.set("key1", "value1").subscribe();
        commands.set("key2", "value2").subscribe();
        commands.mget("key1", "key2").subscribe();
        commands.llen("something").subscribe();
        commands.exec().subscribe(exec);

        exec.awaitTerminalEvent();

        assertThat(exec.getOnNextEvents()).hasSize(4).containsExactly("OK", "OK", list("value1", "value2"), 0L);
    }

    protected <T> T first(Observable<T> observable) {
        BlockingObservable<T> blocking = observable.toBlocking();
        Iterator<T> iterator = blocking.getIterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    protected <T> List<T> all(Observable<T> observable) {
        BlockingObservable<T> blocking = observable.toBlocking();
        Iterator<T> iterator = blocking.getIterator();
        return LettuceLists.newList(iterator);
    }
}
