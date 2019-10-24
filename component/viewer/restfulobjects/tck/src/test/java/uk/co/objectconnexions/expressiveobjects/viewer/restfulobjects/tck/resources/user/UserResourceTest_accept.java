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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.resources.user;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.webserver.WebServer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulClient;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse.HttpStatusCode;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.homepage.HomePageRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.ExpressiveObjectsWebServerRule;

public class UserResourceTest_accept {

    @Rule
    public ExpressiveObjectsWebServerRule webServerRule = new ExpressiveObjectsWebServerRule();

    private RestfulClient client;

    @Before
    public void setUp() throws Exception {
        final WebServer webServer = webServerRule.getWebServer();
        client = new RestfulClient(webServer.getBase());
    }

    @Test
    public void applicationJson() throws Exception {

        final RestfulRequest request = client.createRequest(HttpMethod.GET, "user").withHeader(RestfulRequest.Header.ACCEPT, MediaType.APPLICATION_JSON_TYPE);
        final RestfulResponse<HomePageRepresentation> restfulResponse = request.executeT();

        assertThat(restfulResponse.getStatus(), is(HttpStatusCode.OK));
    }

    @Test
    public void applicationJson_profileUser() throws Exception {

        final RestfulRequest request = client.createRequest(HttpMethod.GET, "user").withHeader(RestfulRequest.Header.ACCEPT, RepresentationType.USER.getMediaType());
        final RestfulResponse<HomePageRepresentation> restfulResponse = request.executeT();

        assertThat(restfulResponse.getStatus(), is(HttpStatusCode.OK));
    }

    @Test
    public void applicationJson_invalid() throws Exception {

        final RestfulRequest request = client.createRequest(HttpMethod.GET, "user").withHeader(RestfulRequest.Header.ACCEPT, RepresentationType.HOME_PAGE.getMediaType());
        final RestfulResponse<HomePageRepresentation> restfulResponse = request.executeT();

        assertThat(restfulResponse.getStatus(), is(HttpStatusCode.NOT_ACCEPTABLE));
    }

}
