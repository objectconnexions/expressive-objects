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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.home;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulMediaType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse.HttpStatusCode;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.homepage.HomePageResource;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.RestfulObjectsApplicationException;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.RendererFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.ResourceAbstract;

/**
 * Implementation note: it seems to be necessary to annotate the implementation
 * with {@link Path} rather than the interface (at least under RestEasy 1.0.2
 * and 1.1-RC2).
 */
public class HomePageResourceServerside extends ResourceAbstract implements HomePageResource {

    @Override
    @Produces({ RestfulMediaType.APPLICATION_JSON_HOME_PAGE })
    public Response homePage() {
        final RepresentationType representationType = RepresentationType.HOME_PAGE;
        init(representationType, Where.NOWHERE);

        final RendererFactory factory = rendererFactoryRegistry.find(representationType);
        final HomePageReprRenderer renderer = (HomePageReprRenderer) factory.newRenderer(getResourceContext(), null, JsonRepresentation.newMap());
        renderer.includesSelf();

        return responseOfOk(renderer, Caching.ONE_DAY).build();
    }

    @Override
    @GET
    @Path("/notAuthenticated")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response notAuthenticated() {

        throw RestfulObjectsApplicationException.create(HttpStatusCode.UNAUTHORIZED);
    }

}