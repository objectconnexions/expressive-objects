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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.LinkRepresentation;

public class LinkRepresentationTest_equals {

    @Test
    public void equalDependsOnMethodAndHref() throws UnsupportedEncodingException {
        final LinkRepresentation link = new LinkRepresentation().withHref("http://localhost:8080/objects/ABC:123").withMethod(HttpMethod.GET);
        final LinkRepresentation link2 = new LinkRepresentation().withHref("http://localhost:8080/objects/ABC:123").withMethod(HttpMethod.GET);
        final LinkRepresentation link3 = new LinkRepresentation().withHref("http://localhost:8080/objects/ABC:123").withMethod(HttpMethod.PUT);
        final LinkRepresentation link4 = new LinkRepresentation().withHref("http://localhost:8080/objects/ABC:456").withMethod(HttpMethod.GET);

        assertThat(link, is(equalTo(link2)));
        assertThat(link, is(not(equalTo(link3))));
        assertThat(link, is(not(equalTo(link4))));
    }

    @Test
    public void equalDoesNotDependsOnMethodAndHref() throws UnsupportedEncodingException {
        final LinkRepresentation link = new LinkRepresentation().withHref("http://localhost:8080/objects/ABC:123").withMethod(HttpMethod.GET).withRel("something");
        final LinkRepresentation link2 = new LinkRepresentation().withHref("http://localhost:8080/objects/ABC:123").withMethod(HttpMethod.GET).withRel("else");

        assertThat(link, is(equalTo(link2)));
    }

}
