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

package uk.co.objectconnexions.expressiveobjects.core.commons.ensure;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class Ensure_GivenValueThatDoesMatchTest {

    @Test
    public void whenCallEnsureThatArgThenShouldReturnOriginalObject() {
        final String object = "foo";
        final String returnedObject = Ensure.ensureThatArg(object, is(not(nullValue(String.class))));
        assertThat(returnedObject, sameInstance(object));
    }

    @Test
    public void whenCallEnsureThatArgWithOverloadedShouldReturnOriginalObject() {
        final String object = "foo";
        final String returnedObject = Ensure.ensureThatArg(object, is(not(nullValue(String.class))), "some message");
        assertThat(returnedObject, sameInstance(object));
    }

    @Test
    public void whenCallEnsureThatStateThenShouldReturnOriginalObject() {
        final String object = "foo";
        final String returnedObject = Ensure.ensureThatState(object, is(not(nullValue(String.class))));
        assertThat(returnedObject, sameInstance(object));
    }

    @Test
    public void whenCallEnsureThatStateWithOverloadedShouldReturnOriginalObject() {
        final String object = "foo";
        final String returnedObject = Ensure.ensureThatState(object, is(not(nullValue(String.class))), "some message");
        assertThat(returnedObject, sameInstance(object));
    }

    @Test
    public void whenCallEnsureThatContextThenShouldReturnOriginalObject() {
        final String object = "foo";
        final String returnedObject = Ensure.ensureThatContext(object, is(not(nullValue(String.class))));
        assertThat(returnedObject, sameInstance(object));
    }

    @Test
    public void whenCallEnsureThatContextWithOverloadedShouldReturnOriginalObject() {
        final String object = "foo";
        final String returnedObject = Ensure.ensureThatContext(object, is(not(nullValue(String.class))), "some message");
        assertThat(returnedObject, sameInstance(object));
    }

}
