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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.user;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulMediaType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.user.UserResource;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.RendererFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.ResourceAbstract;

public class UserResourceServerside extends ResourceAbstract implements UserResource {

    @Override
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_USER })
    public Response user() {
        final RepresentationType user = RepresentationType.USER;
        init(user, Where.NOWHERE);

        final RendererFactory factory = rendererFactoryRegistry.find(user);
        final UserReprRenderer renderer = (UserReprRenderer) factory.newRenderer(getResourceContext(), null, JsonRepresentation.newMap());
        renderer.includesSelf().with(getAuthenticationSession());

        return responseOfOk(renderer, Caching.ONE_HOUR).build();
    }

}
