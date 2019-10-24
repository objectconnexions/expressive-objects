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
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;

public class JsonRepresentationTest_isArray_isMap_isValue {

    private JsonRepresentation jsonRepresentation;

    @Before
    public void setUp() throws Exception {
        jsonRepresentation = new JsonRepresentation(readJson("map.json"));
    }

    @Test
    public void forMap() throws JsonParseException, JsonMappingException, IOException {
        assertThat(jsonRepresentation.isArray(), is(false));
        assertThat(jsonRepresentation.isMap(), is(true));
        assertThat(jsonRepresentation.isValue(), is(false));
    }

    @Test
    public void forValue() throws JsonParseException, JsonMappingException, IOException {
        final JsonRepresentation valueRepresentation = jsonRepresentation.getRepresentation("aString");
        assertThat(valueRepresentation.isArray(), is(false));
        assertThat(valueRepresentation.isMap(), is(false));
        assertThat(valueRepresentation.isValue(), is(true));
    }

    @Test
    public void forList() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation = new JsonRepresentation(readJson("list.json"));
        assertThat(jsonRepresentation.isArray(), is(true));
        assertThat(jsonRepresentation.isMap(), is(false));
        assertThat(jsonRepresentation.isValue(), is(false));
    }

}
