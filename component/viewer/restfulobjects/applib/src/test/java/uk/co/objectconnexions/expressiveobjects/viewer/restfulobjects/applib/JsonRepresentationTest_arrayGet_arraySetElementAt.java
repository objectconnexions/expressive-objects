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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;

public class JsonRepresentationTest_arrayGet_arraySetElementAt {

    private JsonRepresentation jsonRepresentation;
    private JsonRepresentation arrayRepr;
    private JsonRepresentation objectRepr;

    @Before
    public void setUp() throws Exception {
        arrayRepr = JsonRepresentation.newArray();
        objectRepr = JsonRepresentation.newMap();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void arrayGet_outOfBounds() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation = new JsonRepresentation(readJson("emptyList.json"));
        jsonRepresentation.arrayGet(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void arraySetElementAt_outOfBounds() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation = new JsonRepresentation(readJson("emptyList.json"));
        jsonRepresentation.arraySetElementAt(0, objectRepr);
    }

    @Test
    public void arrayGet_forNonEmptyList() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation = new JsonRepresentation(readJson("list.json"));
        assertThat(jsonRepresentation.arrayGet(0), is(not(nullValue())));
    }

    @Test
    public void arraySetElementAt_happyCaseWhenSetElementToObject() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation = new JsonRepresentation(readJson("list.json"));
        jsonRepresentation.arraySetElementAt(0, objectRepr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void arraySetElementAt_forAttemptingToSetElementToArray() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation = new JsonRepresentation(readJson("list.json"));
        jsonRepresentation.arraySetElementAt(0, arrayRepr);
    }

    @Test(expected = IllegalStateException.class)
    public void arrayGet_forMap() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation = new JsonRepresentation(readJson("emptyMap.json"));
        jsonRepresentation.arrayGet(0);
    }

    @Test(expected = IllegalStateException.class)
    public void arrayGet_forValue() throws JsonParseException, JsonMappingException, IOException {
        jsonRepresentation = new JsonRepresentation(readJson("map.json"));
        final JsonRepresentation valueRepresentation = jsonRepresentation.getRepresentation("anInt");
        valueRepresentation.arrayGet(0);
    }

}
