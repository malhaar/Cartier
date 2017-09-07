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

import static com.lambdaworks.redis.cluster.PartitionsConsensusTestSupport.createMap;
import static com.lambdaworks.redis.cluster.PartitionsConsensusTestSupport.createNode;
import static com.lambdaworks.redis.cluster.PartitionsConsensusTestSupport.createPartitions;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;

/**
 * @author Mark Paluch
 */
public class HealthyMajorityPartitionsConsensusTest {

    private RedisClusterNode node1 = createNode(1);
    private RedisClusterNode node2 = createNode(2);
    private RedisClusterNode node3 = createNode(3);
    private RedisClusterNode node4 = createNode(4);
    private RedisClusterNode node5 = createNode(5);

    @Test
    public void sameSharedViewShouldDecideForHealthyNodes() throws Exception {

        Partitions partitions1 = createPartitions(node1, node2, node3, node4, node5);
        Partitions partitions2 = createPartitions(node1, node2, node3, node4, node5);
        Partitions partitions3 = createPartitions(node1, node2, node3, node4, node5);

        Map<RedisURI, Partitions> map = createMap(partitions1, partitions2, partitions3);

        Partitions result = PartitionsConsensus.HEALTHY_MAJORITY.getPartitions(null, map);

        assertThat(Arrays.asList(partitions1, partitions2, partitions3)).contains(result);
    }

    @Test
    public void unhealthyNodeViewShouldDecideForHealthyNodes() throws Exception {

        Partitions partitions1 = createPartitions(node1, node2);
        Partitions partitions2 = createPartitions(node2, node3, node4, node5);
        Partitions partitions3 = createPartitions(node2, node3, node4, node5);

        Map<RedisURI, Partitions> map = createMap(partitions1, partitions2, partitions3);

        node2.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));
        node3.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));
        node4.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));
        node5.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));

        Partitions result = PartitionsConsensus.HEALTHY_MAJORITY.getPartitions(null, map);

        assertThat(result).isSameAs(partitions1);
    }

    @Test
    public void splitNodeViewShouldDecideForHealthyNodes() throws Exception {

        Partitions partitions1 = createPartitions(node1, node2, node3);
        Partitions partitions2 = createPartitions();
        Partitions partitions3 = createPartitions(node3, node4, node5);

        Map<RedisURI, Partitions> map = createMap(partitions1, partitions2, partitions3);

        node1.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));
        node2.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));
        node3.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));

        Partitions result = PartitionsConsensus.HEALTHY_MAJORITY.getPartitions(null, map);

        assertThat(result).isSameAs(partitions3);
    }

    @Test
    public void splitUnhealthyNodeViewShouldDecideForHealthyNodes() throws Exception {

        Partitions partitions1 = createPartitions(node1, node2);
        Partitions partitions2 = createPartitions(node2, node3);
        Partitions partitions3 = createPartitions(node3, node4, node5);

        Map<RedisURI, Partitions> map = createMap(partitions1, partitions2, partitions3);

        node2.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));
        node3.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));
        node4.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.FAIL));

        Partitions result = PartitionsConsensus.HEALTHY_MAJORITY.getPartitions(null, map);

        assertThat(Arrays.asList(partitions1, partitions3)).contains(result);
    }
}
