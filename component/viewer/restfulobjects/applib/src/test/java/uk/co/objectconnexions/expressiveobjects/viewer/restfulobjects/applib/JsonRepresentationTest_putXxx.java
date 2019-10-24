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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;

public class JsonRepresentationTest_putXxx {

    private JsonRepresentation jsonRepresentation;

    @Before
    public void setUp() throws Exception {
        jsonRepresentation = JsonRepresentation.newMap();
    }

    @Test
    public void putInt() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation.mapPut("a", 123);
        assertThat(jsonRepresentation.getInt("a"), is(123));
    }

    @Test
    public void putInt_multipart() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation.mapPut("a.b", 456);
        assertThat(jsonRepresentation.getInt("a.b"), is(456));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putInt_pathBlockedByValue() throws JsonParseException, JsonMappingException, IOException {
        // given
        jsonRepresentation.mapPut("a", 123);

        // when
        jsonRepresentation.mapPut("a.b", 456);
    }

}
