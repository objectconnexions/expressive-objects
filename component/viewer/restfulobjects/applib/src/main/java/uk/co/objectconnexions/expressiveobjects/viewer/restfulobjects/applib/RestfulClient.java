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

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.domaintypes.DomainTypeResource;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.homepage.HomePageResource;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.LinkRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.user.UserResource;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.version.VersionResource;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.domainobjects.DomainObjectResource;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.domainobjects.DomainServiceResource;

public class RestfulClient {

    private final HomePageResource homePageResource;
    private final UserResource userResource;
    private final VersionResource versionResource;
    private final DomainObjectResource domainObjectResource;
    private final DomainServiceResource domainServiceResource;
    private final DomainTypeResource domainTypeResource;

    private final ClientExecutor executor;
    private final ClientRequestFactory clientRequestFactory;

    public RestfulClient(final URI baseUri) {
        this(baseUri, new ApacheHttpClientExecutor(new HttpClient()));
    }

    public RestfulClient(final URI baseUri, final ClientExecutor clientExecutor) {
        this.executor = clientExecutor;
        this.clientRequestFactory = new ClientRequestFactory(clientExecutor, baseUri);

        this.homePageResource = clientRequestFactory.createProxy(HomePageResource.class);
        this.userResource = clientRequestFactory.createProxy(UserResource.class);
        this.domainTypeResource = clientRequestFactory.createProxy(DomainTypeResource.class);
        this.domainServiceResource = clientRequestFactory.createProxy(DomainServiceResource.class);
        this.domainObjectResource = clientRequestFactory.createProxy(DomainObjectResource.class);
        this.versionResource = clientRequestFactory.createProxy(VersionResource.class);
    }

    // ///////////////////////////////////////////////////////////////
    // resources
    // ///////////////////////////////////////////////////////////////

    public HomePageResource getHomePageResource() {
        return homePageResource;
    }

    public UserResource getUserResource() {
        return userResource;
    }

    public VersionResource getVersionResource() {
        return versionResource;
    }

    public DomainTypeResource getDomainTypeResource() {
        return domainTypeResource;
    }

    public DomainObjectResource getDomainObjectResource() {
        return domainObjectResource;
    }

    public DomainServiceResource getDomainServiceResource() {
        return domainServiceResource;
    }

    // ///////////////////////////////////////////////////////////////
    // resource walking support
    // ///////////////////////////////////////////////////////////////

    public RepresentationWalker createWalker(final Response response) {
        return new RepresentationWalker(this, response);
    }

    public RestfulResponse<JsonRepresentation> follow(final LinkRepresentation link) throws Exception {
        return followT(link);
    }

    public <T extends JsonRepresentation> RestfulResponse<T> followT(final LinkRepresentation link) throws Exception {
        return followT(link, JsonRepresentation.newMap());
    }

    public RestfulResponse<JsonRepresentation> follow(final LinkRepresentation link, final JsonRepresentation requestArgs) throws Exception {
        return followT(link, requestArgs);
    }

    public <T extends JsonRepresentation> RestfulResponse<T> followT(final LinkRepresentation link, final JsonRepresentation requestArgs) throws Exception {
        return link.<T> follow(executor, requestArgs);
    }

    public RestfulRequest createRequest(final HttpMethod httpMethod, final String uriTemplate) {

        final boolean includesScheme = uriTemplate.startsWith("http:") || uriTemplate.startsWith("https:");
        final String base = clientRequestFactory.getBase().toString();
        final String uri = (includesScheme ? "" : base) + uriTemplate;

        final ClientRequestConfigurer clientRequestConfigurer = ClientRequestConfigurer.create(executor, uri);

        clientRequestConfigurer.accept(MediaType.APPLICATION_JSON_TYPE);
        clientRequestConfigurer.setHttpMethod(httpMethod);

        return new RestfulRequest(clientRequestConfigurer);
    }

    /**
     * exposed for testing purposes only.
     */
    public ClientRequestFactory getClientRequestFactory() {
        return clientRequestFactory;
    }

}
