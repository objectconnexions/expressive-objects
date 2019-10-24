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

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;

public class JsonRepresentationTest_getRepresentation {

    private JsonRepresentation jsonRepresentation;

    @Before
    public void setUp() throws Exception {
        jsonRepresentation = new JsonRepresentation(readJson("map.json"));
    }

    @Test
    public void forMap() throws JsonParseException, JsonMappingException, IOException {
        final JsonRepresentation mapRepresentation = jsonRepresentation.getRepresentation("aLink");
        assertThat(mapRepresentation.getString("rel"), is("someRel"));
        assertThat(mapRepresentation.isMap(), is(true));
    }

    @Test
    public void forNonExistent() throws JsonParseException, JsonMappingException, IOException {
        assertThat(jsonRepresentation.getRepresentation("doesNotExist"), is(nullValue()));
    }

    @Test
    public void forValue() throws JsonParseException, JsonMappingException, IOException {
        final JsonRepresentation valueRepresentation = jsonRepresentation.getRepresentation("anInt");
        assertThat(valueRepresentation.isValue(), is(true));
    }

    @Test
    public void forList() throws JsonParseException, JsonMappingException, IOException {
        final JsonRepresentation listRepresentation = jsonRepresentation.getRepresentation("aSubList");
        assertThat(listRepresentation.isArray(), is(true));
    }

    @Test
    public void forPath() throws JsonParseException, JsonMappingException, IOException {
        final JsonRepresentation representation = jsonRepresentation.getRepresentation("aSubMap.aLink");
        assertThat(representation.isMap(), is(true));
        assertThat(representation.getString("href"), is("http://foo/bar"));
    }

    @Test
    public void forListWithSingleCriteriaMatchingOne() throws JsonParseException, JsonMappingException, IOException {
        final JsonRepresentation representation = jsonRepresentation.getRepresentation("anotherSubMap.aListOfLinks[rel=aRel]");
        assertThat(representation.isLink(), is(true));
        assertThat(representation.asLink().getHref(), is("http://foo/bar"));
    }

    @Test
    public void forListWithMultipleCriteriaMatchingOne() throws JsonParseException, JsonMappingException, IOException {
        final JsonRepresentation representation = jsonRepresentation.getRepresentation("anotherSubMap.aListOfLinks[rel=multiRel data=someData]");
        assertThat(representation.isLink(), is(true));
        assertThat(representation.asLink().getHref(), is("http://foo/bar/multiRel1"));
    }

    @Test
    public void forListWithMultipleCriteriaMatchingMultiple() throws JsonParseException, JsonMappingException, IOException {
        final JsonRepresentation representation = jsonRepresentation.getRepresentation("anotherSubMap.aListOfLinks[rel=multiRel method=GET]");
        assertThat(representation.isArray(), is(true));
        assertThat(representation.size(), is(2));
    }

    @Test
    public void whenStartingWithList() throws JsonParseException, JsonMappingException, IOException {
        final JsonRepresentation listRepresentation = jsonRepresentation.getRepresentation("anotherSubMap.aListOfLinks");
        final JsonRepresentation representation = listRepresentation.getRepresentation("[rel=multiRel method=GET]");
        assertThat(representation.isArray(), is(true));
        assertThat(representation.size(), is(2));
    }

}
