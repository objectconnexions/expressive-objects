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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StringUtils_StripNewLinesTest {

    @Test
    public void shouldDoNothingIfNone() {
        assertThat(StringUtils.stripNewLines("abc"), is("abc"));
    }

    @Test
    public void shouldStripIfJustBackslashN() {
        assertThat(StringUtils.stripNewLines("abc\n"), is("abc"));
    }

    @Test
    public void shouldStripIfBackslashRBackslashN() {
        assertThat(StringUtils.stripNewLines("abc\r\n"), is("abc"));
    }

    @Test
    public void shouldStripIfJustBackslashR() {
        assertThat(StringUtils.stripNewLines("abc\r"), is("abc"));
    }

    @Test
    public void shouldStripIfSeveral() {
        assertThat(StringUtils.stripNewLines("abc\r\ndef\r\n"), is("abcdef"));
    }

    @Test
    public void shouldStripIfBackslashRBackslashNBackslashR() {
        assertThat(StringUtils.stripNewLines("abc\n\r\ndef"), is("abcdef"));
    }

}
