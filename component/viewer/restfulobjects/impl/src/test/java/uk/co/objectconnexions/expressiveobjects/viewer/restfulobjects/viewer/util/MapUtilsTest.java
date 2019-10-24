/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.util.MapUtils;

public class MapUtilsTest {

    @Test
    public void happyCase() throws Exception {
        final Map<String, String> map = MapUtils.mapOf("foo", "bar", "foz", "boz");
        assertThat(map.get("foo"), is("bar"));
        assertThat(map.get("foz"), is("boz"));
        assertThat(map.size(), is(2));
    }

    @Test
    public void emptyList() throws Exception {
        final Map<String, String> map = MapUtils.mapOf();
        assertThat(map.size(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void uneven() throws Exception {
        MapUtils.mapOf("foo");
    }

}
