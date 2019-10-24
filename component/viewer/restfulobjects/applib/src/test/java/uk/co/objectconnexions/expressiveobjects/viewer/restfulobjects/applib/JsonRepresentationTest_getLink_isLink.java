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

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.LinkRepresentation;

public class JsonRepresentationTest_getLink_isLink {

    private LinkRepresentation link;
    private JsonRepresentation jsonRepresentation;

    @Before
    public void setUp() throws Exception {
        link = new LinkRepresentation().withHref("http://foo/bar").withMethod(HttpMethod.GET);
        jsonRepresentation = new JsonRepresentation(readJson("map.json"));
    }

    @Test
    public void forLink_whenSimpleKey() throws JsonParseException, JsonMappingException, IOException {
        link.withRel("someRel");
        assertThat(jsonRepresentation.isLink("aLink"), is(true));
        assertThat(jsonRepresentation.getLink("aLink"), is(link));
    }

    @Test
    public void forLink_whenMultipartKey() throws JsonParseException, JsonMappingException, IOException {
        link.withRel("someSubRel");
        assertThat(jsonRepresentation.isLink("aSubMap.aLink"), is(true));
        assertThat(jsonRepresentation.getLink("aSubMap.aLink"), is(link));
    }

    @Test
    public void forNonExistent() throws JsonParseException, JsonMappingException, IOException {
        assertThat(jsonRepresentation.isLink("doesNotExist"), is(false));
        assertThat(jsonRepresentation.getLink("doesNotExist"), is(nullValue()));
    }

    @Test
    public void forValue() throws JsonParseException, JsonMappingException, IOException {
        assertThat(jsonRepresentation.isLink("anInt"), is(false));
        try {
            jsonRepresentation.getLink("anInt");
            fail();
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), is("'anInt' is a value that does not represent a link"));
        }
    }

    @Test
    public void forMap() throws JsonParseException, JsonMappingException, IOException {
        assertThat(jsonRepresentation.isLink("aSubMap"), is(false));
        try {
            jsonRepresentation.getLink("aSubMap");
            fail();
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), is("'aSubMap' is a map that does not fully represent a link"));
        }
    }

    @Test
    public void forList() throws JsonParseException, JsonMappingException, IOException {
        assertThat(jsonRepresentation.isLink("aSubList"), is(false));
        try {
            jsonRepresentation.getLink("aSubList");
            fail();
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), is("'aSubList' is an array that does not represent a link"));
        }
    }
}
