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
package uk.co.objectconnexions.expressiveobjects.core.commons.lang;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class StringUtils_SplitOnCommas {

    @Test
    public void length() {
        final List<String> list = StringUtils.splitOnCommas("foo,bar");
        Assert.assertThat(list.size(), CoreMatchers.is(2));
    }

    @Test
    public void elements() {
        final List<String> list = StringUtils.splitOnCommas("foo,bar");
        Assert.assertThat(list.get(0), CoreMatchers.is("foo"));
        Assert.assertThat(list.get(1), CoreMatchers.is("bar"));
    }

    @Test
    public void whenHasWhiteSpaceAfterComma() {
        final List<String> list = StringUtils.splitOnCommas("foo, bar");
        Assert.assertThat(list.get(0), CoreMatchers.is("foo"));
        Assert.assertThat(list.get(1), CoreMatchers.is("bar"));
    }

    @Test
    public void whenHasLeadingWhiteSpace() {
        final List<String> list = StringUtils.splitOnCommas(" foo, bar");
        Assert.assertThat(list.get(0), CoreMatchers.is("foo"));
        Assert.assertThat(list.get(1), CoreMatchers.is("bar"));
    }

    @Test
    public void whenNull() {
        final List<String> list = StringUtils.splitOnCommas(null);
        Assert.assertThat(list, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void whenEmpty() {
        final List<String> list = StringUtils.splitOnCommas("");
        Assert.assertThat(list.size(), CoreMatchers.is(0));
    }

    @Test
    public void whenOnlyWhiteSpace() {
        final List<String> list = StringUtils.splitOnCommas(" ");
        Assert.assertThat(list.size(), CoreMatchers.is(0));
    }

    @Test
    public void whenOnlyWhiteSpaceTabs() {
        final List<String> list = StringUtils.splitOnCommas("\t");
        Assert.assertThat(list.size(), CoreMatchers.is(0));
    }

}
