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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.resources.home;

import static uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.RepresentationMatchers.isArray;
import static uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.RepresentationMatchers.isMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.webserver.WebServer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulClient;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest.RequestParameter;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.homepage.HomePageRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.tck.ExpressiveObjectsWebServerRule;

public class HomePageResourceTest_xrofollowlinks {

    @Rule
    public ExpressiveObjectsWebServerRule webServerRule = new ExpressiveObjectsWebServerRule();

    private RestfulClient client;

    private RestfulRequest request;
    private RestfulResponse<HomePageRepresentation> restfulResponse;
    private HomePageRepresentation repr;

    @Before
    public void setUp() throws Exception {
        final WebServer webServer = webServerRule.getWebServer();
        client = new RestfulClient(webServer.getBase());

        request = client.createRequest(HttpMethod.GET, "");
        restfulResponse = request.executeT();
        repr = restfulResponse.getEntity();

        // given
        assertThat(repr.getUser().getValue(), is(nullValue()));
        assertThat(repr.getVersion().getValue(), is(nullValue()));
        assertThat(repr.getServices().getValue(), is(nullValue()));
    }

    @Test
    public void canFollowUser() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=user]");

        assertThat(repr.getUser().getValue(), is(not(nullValue())));
    }

    @Test
    public void canFollowServices() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=services]");

        assertThat(repr.getServices().getValue(), is(not(nullValue())));
    }

    @Test
    public void canFollowVersion() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=version]");

        assertThat(repr.getVersion().getValue(), is(not(nullValue())));
    }

    @Ignore("broken... (did this ever work, not sure)")
    @Test
    public void canFollowAll() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=user],links[rel=services],links[rel=version]");

        assertThat(repr.getUser().getValue(), is(not(nullValue())));
        assertThat(repr.getVersion().getValue(), is(not(nullValue())));
        assertThat(repr.getServices().getValue(), is(not(nullValue())));
    }

    @Test
    public void servicesValues() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=services].values");

        final JsonRepresentation servicesValue = repr.getServices().getValue();
        assertThat(servicesValue, is(not(nullValue())));
        assertThat(servicesValue, isMap());
        final JsonRepresentation serviceLinkList = servicesValue.getArray("values");
        assertThat(serviceLinkList, isArray());

        JsonRepresentation service;

        service = serviceLinkList.getRepresentation("[id=%s]", "simples");
        assertThat(service, isMap());
        assertThat(service.getString("id"), is("simples"));
        assertThat(service.getRepresentation("value"), is(not(nullValue())));

        service = serviceLinkList.getRepresentation("[id=%s]", "applibValuedEntities");
        assertThat(service, isMap());
        assertThat(service.getString("id"), is("applibValuedEntities"));
        assertThat(service.getRepresentation("value"), is(not(nullValue())));
    }

    @Test
    public void servicesValuesWithCriteria() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=services].values[id=simples]");

        final JsonRepresentation servicesValue = repr.getServices().getValue();
        assertThat(servicesValue, is(not(nullValue())));
        assertThat(servicesValue, isMap());
        final JsonRepresentation serviceLinkList = servicesValue.getArray("values");
        assertThat(serviceLinkList, isArray());

        JsonRepresentation service;

        service = serviceLinkList.getRepresentation("[id=%s]", "simples");
        assertThat(service, isMap());
        assertThat(service.getString("id"), is("simples"));
        assertThat(service.getRepresentation("value"), is(not(nullValue())));

        service = serviceLinkList.getRepresentation("[id=%s]", "applibValuedEntities");
        assertThat(service.getRepresentation("value"), is(nullValue()));
    }

    private HomePageRepresentation whenExecuteAndFollowLinksUsing(final String uriTemplate, final String followLinks) throws JsonParseException, JsonMappingException, IOException {
        request = client.createRequest(HttpMethod.GET, uriTemplate).withArg(RequestParameter.FOLLOW_LINKS, followLinks);
        restfulResponse = request.executeT();
        return restfulResponse.getEntity();
    }

}
