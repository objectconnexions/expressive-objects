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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest.RequestParameter;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse.HttpStatusCode;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domainobjects.DomainResourceHelper;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ResourceContext {

    private final HttpHeaders httpHeaders;
    private final UriInfo uriInfo;
    private final Request request;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final SecurityContext securityContext;
    private final Localization localization;
    private final ExpressiveObjectsConfiguration configuration;
    private final AuthenticationSession authenticationSession;
    private final PersistenceSession persistenceSession;
    private final AdapterManager adapterManager;
    private final SpecificationLoader specificationLookup;

    private List<List<String>> followLinks;

    private final static Predicate<MediaType> MEDIA_TYPE_NOT_GENERIC_APPLICATION_JSON = new Predicate<MediaType>() {
        @Override
        public boolean apply(final MediaType mediaType) {
            return !mediaType.equals(MediaType.APPLICATION_JSON_TYPE);
        }
    };
    private final static Predicate<MediaType> MEDIA_TYPE_CONTAINS_PROFILE = new Predicate<MediaType>() {
        @Override
        public boolean apply(final MediaType mediaType) {
            return mediaType.getParameters().containsKey("profile");
        }
    };
    private final Where where;
    private JsonRepresentation readQueryStringAsMap;

    public ResourceContext(final RepresentationType representationType, final HttpHeaders httpHeaders, final UriInfo uriInfo, final Request request, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final SecurityContext securityContext,
            final Localization localization, final AuthenticationSession authenticationSession, final PersistenceSession persistenceSession, final AdapterManager objectAdapterLookup, final SpecificationLoader specificationLookup, ExpressiveObjectsConfiguration configuration, Where where) {

        this.httpHeaders = httpHeaders;
        this.uriInfo = uriInfo;
        this.request = request;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.securityContext = securityContext;
        this.localization = localization;
        this.configuration = configuration;
        this.authenticationSession = authenticationSession;
        this.persistenceSession = persistenceSession;
        this.adapterManager = objectAdapterLookup;
        this.specificationLookup = specificationLookup;
        this.where = where;

        init(representationType);
    }

    void init(final RepresentationType representationType) {
        ensureCompatibleAcceptHeader(representationType);
        this.followLinks = Collections.unmodifiableList(getArg(RequestParameter.FOLLOW_LINKS));
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getQueryString() {
        return getHttpServletRequest().getQueryString();
    }

    public JsonRepresentation getQueryStringAsJsonRepr() {
        if (readQueryStringAsMap == null) {
            readQueryStringAsMap = DomainResourceHelper.readQueryStringAsMap(getQueryString());
        }
        return readQueryStringAsMap;
    }

    public <Q> Q getArg(final RequestParameter<Q> requestParameter) {
        final JsonRepresentation queryStringJsonRepr = getQueryStringAsJsonRepr();
        return requestParameter.valueOf(queryStringJsonRepr);
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public Request getRequest() {
        return request;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getServletResponse() {
        return httpServletResponse;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    private void ensureCompatibleAcceptHeader(final RepresentationType representationType) {
        if (representationType == null) {
            return;
        }
        final MediaType producedType = representationType.getMediaType();
        final List<MediaType> acceptableMediaTypes = acceptableMediaTypes();
        for (final MediaType mediaType : acceptableMediaTypes) {
            if (compatible(mediaType, representationType)) {
                return;
            }
        }
        if (!acceptableMediaTypes.contains(producedType)) {
            throw RestfulObjectsApplicationException.create(HttpStatusCode.NOT_ACCEPTABLE, "Resource produces %s media type", representationType.getMediaTypeWithProfile());
        }
    }

    /**
     * If no media type has a profile parameter, then simply return all the
     * media types.
     * 
     * <p>
     * Otherwise, though, filter out the {@link MediaType#APPLICATION_JSON_TYPE
     * generic application/json} media type if it is present.
     */
    private List<MediaType> acceptableMediaTypes() {
        final List<MediaType> acceptableMediaTypes = getHttpHeaders().getAcceptableMediaTypes();
        if (Collections2.filter(acceptableMediaTypes, MEDIA_TYPE_CONTAINS_PROFILE).isEmpty()) {
            return acceptableMediaTypes;
        }
        return Lists.newArrayList(Iterables.filter(acceptableMediaTypes, MEDIA_TYPE_NOT_GENERIC_APPLICATION_JSON));
    }

    private boolean compatible(final MediaType acceptedMediaType, final RepresentationType representationType) {
        final MediaType producedMediaType = representationType.getMediaType();
        final String profile = acceptedMediaType.getParameters().get("profile");
        return profile == null ? acceptedMediaType.isCompatible(producedMediaType) : acceptedMediaType.equals(producedMediaType);
    }

    public List<List<String>> getFollowLinks() {
        return followLinks;
    }

    public String urlFor(final String url) {
        return getUriInfo().getBaseUri().toString() + url;
    }

    public Localization getLocalization() {
        return localization;
    }

    public AuthenticationSession getAuthenticationSession() {
        return authenticationSession;
    }

    public AdapterManager getAdapterManager() {
        return adapterManager;
    }

    public PersistenceSession getPersistenceSession() {
        return persistenceSession;
    }

    public List<ObjectAdapter> getServiceAdapters() {
        return persistenceSession.getServices();
    }

    public SpecificationLoader getSpecificationLookup() {
        return specificationLookup;
    }

    public Where getWhere() {
        return where;
    }
    
}
