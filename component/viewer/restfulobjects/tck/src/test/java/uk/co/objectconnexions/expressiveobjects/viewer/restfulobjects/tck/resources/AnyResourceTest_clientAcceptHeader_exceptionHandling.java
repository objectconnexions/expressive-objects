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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.resources;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.webserver.WebServer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulClient;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest.Header;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse.HttpStatusCode;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.homepage.HomePageRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.util.Parser;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.ExpressiveObjectsWebServerRule;

public class AnyResourceTest_clientAcceptHeader_exceptionHandling {

    @Rule
    public ExpressiveObjectsWebServerRule webServerRule = new ExpressiveObjectsWebServerRule();

    private RestfulClient client;

    @Before
    public void setUp() throws Exception {
        final WebServer webServer = webServerRule.getWebServer();
        client = new RestfulClient(webServer.getBase());
    }

    @Test
    public void whenSetsNoAcceptHeader_isOk() throws Exception {
        // given
        final RestfulRequest restfulReq = client.createRequest(HttpMethod.GET, "/");

        // when
        final RestfulResponse<HomePageRepresentation> restfulResp = restfulReq.executeT();

        // then
        assertThat(restfulResp.getStatus(), is(HttpStatusCode.OK));
        assertThat(restfulResp.getHeader(RestfulResponse.Header.CONTENT_TYPE), is(RepresentationType.HOME_PAGE.getMediaType()));
    }

    @Test
    public void whenSetsAcceptHeaderOfApplicationJson_isOk() throws Exception {

        // given
        final RestfulRequest restfulReq = client.createRequest(HttpMethod.GET, "/");
        restfulReq.withHeader(Header.ACCEPT, MediaType.APPLICATION_JSON_TYPE);

        // when
        final RestfulResponse<HomePageRepresentation> restfulResp = restfulReq.executeT();

        // then
        assertThat(restfulResp.getStatus(), is(HttpStatusCode.OK));
        assertThat(restfulResp.getHeader(RestfulResponse.Header.CONTENT_TYPE), is(RepresentationType.HOME_PAGE.getMediaType()));
    }

    @Ignore("RestEasy seems to reject with a 500, 'No match for accept header', rather than a 405.")
    @Test
    public void whenSetsIncorrectMediaType_returnsNotAcceptable() throws Exception {

        // given
        final ClientRequest clientRequest = client.getClientRequestFactory().createRelativeRequest("/");
        clientRequest.accept(MediaType.APPLICATION_ATOM_XML_TYPE);

        // when
        final ClientResponse<?> resp = clientRequest.get();
        final RestfulResponse<JsonRepresentation> restfulResp = RestfulResponse.of(resp);
        
        final String entity = restfulResp.getEntity().toString();

        // then
        assertThat(restfulResp.getStatus(), is(HttpStatusCode.NOT_ACCEPTABLE));
    }

}
