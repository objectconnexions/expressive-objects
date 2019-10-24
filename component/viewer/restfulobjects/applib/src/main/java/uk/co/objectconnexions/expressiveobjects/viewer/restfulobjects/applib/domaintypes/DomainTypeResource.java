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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.domaintypes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.ClientResponseType;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulMediaType;

@Path("/domainTypes")
public interface DomainTypeResource {

    // /////////////////////////////////////////////////////
    // domainTypes (list of all )
    // /////////////////////////////////////////////////////

    @GET
    @Path("/")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_TYPE_LIST })
    @ClientResponseType(entityType = String.class)
    public abstract Response domainTypes();

    // /////////////////////////////////////////////////////
    // domainType + member description
    // /////////////////////////////////////////////////////

    @GET
    @Path("/{domainType}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_DOMAIN_TYPE })
    @ClientResponseType(entityType = String.class)
    public abstract Response domainType(@PathParam("domainType") final String domainType);

    @GET
    @Path("/{domainType}/properties/{propertyId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_PROPERTY_DESCRIPTION })
    @ClientResponseType(entityType = String.class)
    public abstract Response typeProperty(@PathParam("domainType") final String domainType, @PathParam("propertyId") final String propertyId);

    @GET
    @Path("/{domainType}/collections/{collectionId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_COLLECTION_DESCRIPTION })
    @ClientResponseType(entityType = String.class)
    public abstract Response typeCollection(@PathParam("domainType") final String domainType, @PathParam("collectionId") final String collectionId);

    @GET
    @Path("/{domainType}/actions/{actionId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ACTION_DESCRIPTION })
    @ClientResponseType(entityType = String.class)
    public abstract Response typeAction(@PathParam("domainType") final String domainType, @PathParam("actionId") final String actionId);

    @GET
    @Path("/{domainType}/actions/{actionId}/params/{paramNum}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ACTION_PARAMETER_DESCRIPTION })
    @ClientResponseType(entityType = String.class)
    public abstract Response typeActionParam(@PathParam("domainType") final String domainType, @PathParam("actionId") final String actionId, @PathParam("paramNum") final String paramName);

    // //////////////////////////////////////////////////////////
    // domain type actions
    // //////////////////////////////////////////////////////////

    @GET
    @Path("/{domainType}/isSubtypeOf/invoke")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_TYPE_ACTION_RESULT, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public abstract Response domainTypeIsSubtypeOf(@PathParam("domainType") final String domainType, @QueryParam("supertype") String superType, // simple
                                                                                                                                                // style
            @QueryParam("args") final String argumentsQueryString // formal
                                                                  // style
    );

    @GET
    @Path("/{domainType}/isSupertypeOf/invoke")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_TYPE_ACTION_RESULT, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public abstract Response domainTypeIsSupertypeOf(@PathParam("domainType") final String domainType, @QueryParam("supertype") String superType, // simple
                                                                                                                                                  // style
            @QueryParam("args") final String argumentsQueryString // formal
                                                                  // style
    );

    @GET
    @Path("/{domainType}/newTransientInstance/invoke")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_TYPE_ACTION_RESULT, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response newTransientInstance(@QueryParam("domainType") final String domainType, @QueryParam("args") final String args);

}