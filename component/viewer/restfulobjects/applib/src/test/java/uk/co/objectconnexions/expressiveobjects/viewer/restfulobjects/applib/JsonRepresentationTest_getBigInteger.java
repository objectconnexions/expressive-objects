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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib;

import static uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonFixture.readJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigInteger;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;

public class JsonRepresentationTest_getBigInteger {

    private JsonRepresentation jsonRepresentation;

    @Before
    public void setUp() throws Exception {
        jsonRepresentation = new JsonRepresentation(readJson("map.json"));
    }

    @Test
    public void happyCase() throws JsonParseException, JsonMappingException, IOException {
        assertThat(jsonRepresentation.getBigInteger("aBigInteger"), is(new BigInteger("12345678901234534132452342342789678234234")));
    }

    @Test
    public void forNonExistent() throws JsonParseException, JsonMappingException, IOException {
        assertThat(jsonRepresentation.getBigInteger("doesNotExist"), is(nullValue()));
    }

    @Test
    public void forValueButNot() throws JsonParseException, JsonMappingException, IOException {
        try {
            jsonRepresentation.getBigInteger("aString");
            fail();
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), is("'aString' is not a biginteger"));
        }
    }

    @Test
    public void forMap() throws JsonParseException, JsonMappingException, IOException {
        try {
            jsonRepresentation.getBigInteger("aSubMap");
            fail();
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), is("'aSubMap' is not a biginteger"));
        }
    }

    @Test
    public void forList() throws JsonParseException, JsonMappingException, IOException {
        try {
            jsonRepresentation.getBigInteger("aSubList");
            fail();
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), is("'aSubList' is not a biginteger"));
        }
    }

    @Test
    public void forMultipartKey() throws JsonParseException, JsonMappingException, IOException {
        assertThat(jsonRepresentation.getDouble("aSubMap.aDouble"), is(12345678901234534.4567));
    }

}
