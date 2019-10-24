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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;

public class RepresentationTypeTest_getMediaType_lookup {

    @Test
    public void roundtrip() {
        for (final RepresentationType repType : RepresentationType.values()) {
            final MediaType mediaType = repType.getMediaType();
            final RepresentationType lookup = RepresentationType.lookup(mediaType);
            assertSame(repType, lookup);
        }
    }

    @Test
    public void whenUnknown() {
        assertThat(RepresentationType.lookup(MediaType.APPLICATION_ATOM_XML_TYPE), is(RepresentationType.GENERIC));
    }

    @Test
    public void whenNull() {
        assertThat(RepresentationType.lookup((MediaType) null), is(RepresentationType.GENERIC));
    }

    @Test
    public void getMediaTypeProfile() {
        assertThat(RepresentationType.VERSION.getMediaTypeWithProfile(), is("urn:org.restfulobjects/version"));
        assertThat(RepresentationType.GENERIC.getMediaTypeWithProfile(), is(nullValue()));
    }

}
