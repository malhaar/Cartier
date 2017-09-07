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
package com.lambdaworks.redis.models.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.lambdaworks.redis.internal.LettuceLists;

public class CommandDetailParserTest {

    @Test
    public void testMappings() throws Exception {
        assertThat(CommandDetailParser.FLAG_MAPPING).hasSameSizeAs(CommandDetail.Flag.values());
    }

    @Test
    public void testEmptyList() throws Exception {

        List<CommandDetail> result = CommandDetailParser.parse(new ArrayList<>());
        assertThat(result).isEmpty();
    }

    @Test
    public void testMalformedList() throws Exception {
        Object o = LettuceLists.newList("", "", "");
        List<CommandDetail> result = CommandDetailParser.parse(LettuceLists.newList(o));
        assertThat(result).isEmpty();
    }

    @Test
    public void testParse() throws Exception {
        Object o = LettuceLists.newList("get", "1", LettuceLists.newList("fast", "loading"), 1L, 2L, 3L);
        List<CommandDetail> result = CommandDetailParser.parse(LettuceLists.newList(o));
        assertThat(result).hasSize(1);

        CommandDetail commandDetail = result.get(0);
        assertThat(commandDetail.getName()).isEqualTo("get");
        assertThat(commandDetail.getArity()).isEqualTo(1);
        assertThat(commandDetail.getFlags()).hasSize(2);
        assertThat(commandDetail.getFirstKeyPosition()).isEqualTo(1);
        assertThat(commandDetail.getLastKeyPosition()).isEqualTo(2);
        assertThat(commandDetail.getKeyStepCount()).isEqualTo(3);
    }

    @Test
    public void testModel() throws Exception {
        CommandDetail commandDetail = new CommandDetail();
        commandDetail.setArity(1);
        commandDetail.setFirstKeyPosition(2);
        commandDetail.setLastKeyPosition(3);
        commandDetail.setKeyStepCount(4);
        commandDetail.setName("theName");
        commandDetail.setFlags(new HashSet<>());

        assertThat(commandDetail.toString()).contains(CommandDetail.class.getSimpleName());
    }
}
