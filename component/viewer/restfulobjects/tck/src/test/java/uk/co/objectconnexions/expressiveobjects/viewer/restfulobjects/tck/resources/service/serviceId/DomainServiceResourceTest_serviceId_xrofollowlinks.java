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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.resources.service.serviceId;

import static uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.RepresentationMatchers.isArray;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
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
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse.HttpStatusCode;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.domainobjects.DomainObjectRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.domainobjects.DomainServiceResource;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.domainobjects.ListRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.ExpressiveObjectsWebServerRule;

public class DomainServiceResourceTest_serviceId_xrofollowlinks {

    @Rule
    public ExpressiveObjectsWebServerRule webServerRule = new ExpressiveObjectsWebServerRule();

    private RestfulClient client;

    @Before
    public void setUp() throws Exception {
        final WebServer webServer = webServerRule.getWebServer();
        client = new RestfulClient(webServer.getBase());
    }

    @Test
    public void withCriteria() throws Exception {

        final String href = givenHrefToService("simples");

        final RestfulRequest request = client.createRequest(HttpMethod.GET, href).withArg(RequestParameter.FOLLOW_LINKS, "members[id=%s].links[rel=details]", "list");
        final RestfulResponse<DomainObjectRepresentation> restfulResponse = request.executeT();

        assertThat(restfulResponse.getStatus(), is(HttpStatusCode.OK));
        final DomainObjectRepresentation repr = restfulResponse.getEntity();

        final JsonRepresentation membersList = repr.getMembers();
        assertThat(membersList, isArray());

        JsonRepresentation actionRepr;

        actionRepr = membersList.getRepresentation("[id=%s]", "list");
        assertThat(actionRepr.getRepresentation("links[rel=details]"), is(not(nullValue())));
        assertThat(actionRepr.getRepresentation("links[rel=details].value"), is(not(nullValue()))); // followed

        actionRepr = membersList.getRepresentation("[id=%s]", "newTransientEntity");
        assertThat(actionRepr.getRepresentation("links[rel=details]"), is(not(nullValue())));
        assertThat(actionRepr.getRepresentation("links[rel=details].value"), is(nullValue())); // not
                                                                                               // followed
    }

    private String givenHrefToService(final String serviceId) throws JsonParseException, JsonMappingException, IOException {
        final DomainServiceResource resource = client.getDomainServiceResource();
        final Response response = resource.services();
        final ListRepresentation services = RestfulResponse.<ListRepresentation> ofT(response).getEntity();

        return services.getRepresentation("values[id=%s]", serviceId).asLink().getHref();
    }

}
