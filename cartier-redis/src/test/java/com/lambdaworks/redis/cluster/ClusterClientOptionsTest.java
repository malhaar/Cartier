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

/**
 * @author Mark Paluch
 */
public class ClusterClientOptionsTest {

    @Test
    public void testCopy() throws Exception {

        ClusterClientOptions options = ClusterClientOptions.builder().closeStaleConnections(true).refreshClusterView(true)
                .autoReconnect(false).requestQueueSize(100).suspendReconnectOnProtocolFailure(true).maxRedirects(1234)
                .validateClusterNodeMembership(false).build();

        ClusterClientOptions copy = ClusterClientOptions.copyOf(options);

        assertThat(copy.getRefreshPeriod()).isEqualTo(options.getRefreshPeriod());
        assertThat(copy.getRefreshPeriodUnit()).isEqualTo(options.getRefreshPeriodUnit());
        assertThat(copy.isCloseStaleConnections()).isEqualTo(options.isCloseStaleConnections());
        assertThat(copy.isRefreshClusterView()).isEqualTo(options.isRefreshClusterView());
        assertThat(copy.isValidateClusterNodeMembership()).isEqualTo(options.isValidateClusterNodeMembership());
        assertThat(copy.getRequestQueueSize()).isEqualTo(options.getRequestQueueSize());
        assertThat(copy.isAutoReconnect()).isEqualTo(options.isAutoReconnect());
        assertThat(copy.isCancelCommandsOnReconnectFailure()).isEqualTo(options.isCancelCommandsOnReconnectFailure());
        assertThat(copy.isSuspendReconnectOnProtocolFailure()).isEqualTo(options.isSuspendReconnectOnProtocolFailure());
        assertThat(copy.getMaxRedirects()).isEqualTo(options.getMaxRedirects());
    }

    @Test
    public void enablesRefreshUsingDeprecatedMethods() throws Exception {

        ClusterClientOptions options = ClusterClientOptions.builder().refreshClusterView(true)
                .refreshPeriod(10, TimeUnit.MINUTES).build();

        assertThat(options.getRefreshPeriod()).isEqualTo(10);
        assertThat(options.getRefreshPeriodUnit()).isEqualTo(TimeUnit.MINUTES);
        assertThat(options.isRefreshClusterView()).isEqualTo(true);
    }
}
