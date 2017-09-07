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
package com.lambdaworks.redis.cluster;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.lambdaworks.redis.cluster.ClusterTopologyRefreshOptions.RefreshTrigger;

/**
 * @author Mark Paluch
 */
public class ClusterTopologyRefreshOptionsTest {

    @Test
    public void testBuilder() throws Exception {

        ClusterTopologyRefreshOptions options = ClusterTopologyRefreshOptions.builder()//
                .enablePeriodicRefresh(true).refreshPeriod(10, TimeUnit.MINUTES)//
                .dynamicRefreshSources(false) //
                .enableAdaptiveRefreshTrigger(RefreshTrigger.MOVED_REDIRECT)//
                .adaptiveRefreshTriggersTimeout(15, TimeUnit.MILLISECONDS)//
                .closeStaleConnections(false)//
                .refreshTriggersReconnectAttempts(2)//
                .build();

        assertThat(options.getRefreshPeriod()).isEqualTo(10);
        assertThat(options.getRefreshPeriodUnit()).isEqualTo(TimeUnit.MINUTES);
        assertThat(options.isCloseStaleConnections()).isEqualTo(false);
        assertThat(options.isPeriodicRefreshEnabled()).isTrue();
        assertThat(options.useDynamicRefreshSources()).isFalse();
        assertThat(options.getAdaptiveRefreshTimeout()).isEqualTo(15);
        assertThat(options.getAdaptiveRefreshTimeoutUnit()).isEqualTo(TimeUnit.MILLISECONDS);
        assertThat(options.getAdaptiveRefreshTriggers()).containsOnly(RefreshTrigger.MOVED_REDIRECT);
        assertThat(options.getRefreshTriggersReconnectAttempts()).isEqualTo(2);
    }

    @Test
    public void testCopy() throws Exception {

        ClusterTopologyRefreshOptions master = ClusterTopologyRefreshOptions.builder()//
                .enablePeriodicRefresh(true).refreshPeriod(10, TimeUnit.MINUTES)//
                .dynamicRefreshSources(false) //
                .enableAdaptiveRefreshTrigger(RefreshTrigger.MOVED_REDIRECT)//
                .adaptiveRefreshTriggersTimeout(15, TimeUnit.MILLISECONDS)//
                .closeStaleConnections(false)//
                .refreshTriggersReconnectAttempts(2)//
                .build();

        ClusterTopologyRefreshOptions options = ClusterTopologyRefreshOptions.copyOf(master);

        assertThat(options.getRefreshPeriod()).isEqualTo(10);
        assertThat(options.getRefreshPeriodUnit()).isEqualTo(TimeUnit.MINUTES);
        assertThat(options.isCloseStaleConnections()).isEqualTo(false);
        assertThat(options.isPeriodicRefreshEnabled()).isTrue();
        assertThat(options.useDynamicRefreshSources()).isFalse();
        assertThat(options.getAdaptiveRefreshTimeout()).isEqualTo(15);
        assertThat(options.getAdaptiveRefreshTimeoutUnit()).isEqualTo(TimeUnit.MILLISECONDS);
        assertThat(options.getAdaptiveRefreshTriggers()).containsOnly(RefreshTrigger.MOVED_REDIRECT);
        assertThat(options.getRefreshTriggersReconnectAttempts()).isEqualTo(2);
    }

    @Test
    public void testDefault() throws Exception {

        ClusterTopologyRefreshOptions options = ClusterTopologyRefreshOptions.create();

        assertThat(options.getRefreshPeriod()).isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD);
        assertThat(options.getRefreshPeriodUnit()).isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD_UNIT);
        assertThat(options.isCloseStaleConnections()).isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_CLOSE_STALE_CONNECTIONS);
        assertThat(options.isPeriodicRefreshEnabled()).isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_PERIODIC_REFRESH_ENABLED).isFalse();
        assertThat(options.useDynamicRefreshSources()).isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_DYNAMIC_REFRESH_SOURCES)
                .isTrue();
        assertThat(options.getAdaptiveRefreshTimeout())
                .isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_ADAPTIVE_REFRESH_TIMEOUT);
        assertThat(options.getAdaptiveRefreshTimeoutUnit())
                .isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_ADAPTIVE_REFRESH_TIMEOUT_UNIT);
        assertThat(options.getAdaptiveRefreshTriggers())
                .isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_ADAPTIVE_REFRESH_TRIGGERS);
        assertThat(options.getRefreshTriggersReconnectAttempts())
                .isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_REFRESH_TRIGGERS_RECONNECT_ATTEMPTS);
    }

    @Test
    public void testEnabled() throws Exception {

        ClusterTopologyRefreshOptions options = ClusterTopologyRefreshOptions.enabled();

        assertThat(options.isPeriodicRefreshEnabled()).isTrue();
        assertThat(options.useDynamicRefreshSources()).isTrue();
        assertThat(options.getAdaptiveRefreshTriggers()).contains(RefreshTrigger.ASK_REDIRECT, RefreshTrigger.MOVED_REDIRECT,
                RefreshTrigger.PERSISTENT_RECONNECTS);
    }
}
