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

package uk.co.objectconnexions.expressiveobjects.core.commons.matchers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

public class ExpressiveObjectsMatchers_StartsWithStripNewLinesTest {

    private Matcher<String> fooMatcher;

    @Before
    public void setUp() {
        fooMatcher = ExpressiveObjectsMatchers.startsWithStripNewLines("foo");
    }

    @Test
    public void shouldMatchExactString() {
        assertThat(fooMatcher.matches("foo"), is(true));
    }

    @Test
    public void shouldMatchIfStartsWithAndStringNoNewLines() {
        assertThat(fooMatcher.matches("foodef"), is(true));
    }

    @Test
    public void shouldMatchIfStartsWithStringHasNewLinesAfter() {
        assertThat(fooMatcher.matches("food\ne\rfan\rg"), is(true));
    }

    @Test
    public void shouldMatchIfStartsWithStringHasNewLinesWithin() {
        assertThat(fooMatcher.matches("f\ro\nodef"), is(true));
    }

    @Test
    public void shouldNotMatchIfDoesNotStartWithString() {
        assertThat(fooMatcher.matches("fob"), is(false));
    }

}
