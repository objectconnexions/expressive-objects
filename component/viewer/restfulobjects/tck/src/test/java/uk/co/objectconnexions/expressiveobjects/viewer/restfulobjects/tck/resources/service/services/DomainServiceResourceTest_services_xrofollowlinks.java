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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.resources.service.services;

import static uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.RepresentationMatchers.assertThat;
import static uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.RepresentationMatchers.isArray;
import static uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.RepresentationMatchers.isLink;
import static uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.RepresentationMatchers.isMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.webserver.WebServer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulClient;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest.RequestParameter;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.domainobjects.ListRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.ExpressiveObjectsWebServerRule;

public class DomainServiceResourceTest_services_xrofollowlinks {

    @Rule
    public ExpressiveObjectsWebServerRule webServerRule = new ExpressiveObjectsWebServerRule();
    private RestfulClient client;

    @Before
    public void setUp() throws Exception {
        final WebServer webServer = webServerRule.getWebServer();
        client = new RestfulClient(webServer.getBase());
    }

    @Test
    public void xrofollowLinks() throws Exception {

        RestfulRequest request;
        RestfulResponse<ListRepresentation> restfulResponse;
        ListRepresentation repr;

        request = client.createRequest(HttpMethod.GET, "services");
        restfulResponse = request.executeT();
        repr = restfulResponse.getEntity();

        assertThat(repr.getValues(), isArray());
        assertThat(repr.getValues().size(), is(greaterThan(0)));
        assertThat(repr.getValues().arrayGet(0), isLink().novalue());

        request = client.createRequest(HttpMethod.GET, "services").withArg(RequestParameter.FOLLOW_LINKS, "values");
        restfulResponse = request.executeT();
        repr = restfulResponse.getEntity();

        assertThat(repr.getValues().arrayGet(0), isLink().value(is(not(Matchers.nullValue(JsonRepresentation.class)))));
        assertThat(repr.getValues().arrayGet(0).getRepresentation("value"), isMap());
    }

}
