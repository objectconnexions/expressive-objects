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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.Version;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServiceUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.OidGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse.HttpStatusCode;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.util.JsonMapper;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.ResourceContext;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.RestfulObjectsApplicationException;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.RendererFactoryRegistry;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRenderer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.util.OidUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.util.UrlDecoderUtils;

public abstract class ResourceAbstract {

    protected final static JsonMapper jsonMapper = JsonMapper.instance();

    public enum Caching {
        ONE_DAY(24 * 60 * 60), ONE_HOUR(60 * 60), NONE(0);

        private final CacheControl cacheControl;

        private Caching(final int maxAge) {
            this.cacheControl = new CacheControl();
            if (maxAge > 0) {
                cacheControl.setMaxAge(maxAge);
            } else {
                cacheControl.setNoCache(true);
            }
        }

        public CacheControl getCacheControl() {
            return cacheControl;
        }
    }

    // nb: SET is excluded; we simply flatten contributed actions.
    public final static ActionType[] ACTION_TYPES = { ActionType.USER, ActionType.DEBUG, ActionType.EXPLORATION };

    // TODO: should inject this instead...
    protected final static RendererFactoryRegistry rendererFactoryRegistry = RendererFactoryRegistry.instance;

    @Context
    HttpHeaders httpHeaders;

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Context
    HttpServletRequest httpServletRequest;

    @Context
    HttpServletResponse httpServletResponse;

    @Context
    SecurityContext securityContext;

    private ResourceContext resourceContext;

    protected void init(Where where) {
        init(RepresentationType.GENERIC, where);
    }

    protected void init(final RepresentationType representationType, Where where) {
        if (!ExpressiveObjectsContext.inSession()) {
            throw RestfulObjectsApplicationException.create(HttpStatusCode.UNAUTHORIZED);
        } 
        if (getAuthenticationSession() == null) {
            throw RestfulObjectsApplicationException.create(HttpStatusCode.UNAUTHORIZED);
        }

        this.resourceContext = new ResourceContext(representationType, httpHeaders, uriInfo, request, httpServletRequest, httpServletResponse, securityContext, getLocalization(), getAuthenticationSession(), getPersistenceSession(), getAdapterManager(), getSpecificationLoader(), getConfiguration(), where);
    }

    protected ResourceContext getResourceContext() {
        return resourceContext;
    }

    // //////////////////////////////////////////////////////////////
    // Rendering
    // //////////////////////////////////////////////////////////////

    protected static String jsonFor(final Object object) {
        try {
            return jsonMapper.write(object);
        } catch (final JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (final JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    // //////////////////////////////////////////////////////////////
    // Expressive Objects integration
    // //////////////////////////////////////////////////////////////

    protected ObjectSpecification getSpecification(final String specFullName) {
        return getSpecificationLoader().loadSpecification(specFullName);
    }

    protected ObjectAdapter getObjectAdapter(final String oidEncodedStr) {

        final ObjectAdapter objectAdapter = OidUtils.getObjectAdapter(resourceContext, oidEncodedStr);

        if (objectAdapter == null) {
            final String oidStr = UrlDecoderUtils.urlDecode(oidEncodedStr);
            throw RestfulObjectsApplicationException.create(HttpStatusCode.NOT_FOUND, "could not determine adapter for OID: '%s'", oidStr);
        }
        return objectAdapter;
    }

    protected ObjectAdapter getServiceAdapter(final String serviceId) {
        final List<ObjectAdapter> serviceAdapters = getPersistenceSession().getServices();
        for (final ObjectAdapter serviceAdapter : serviceAdapters) {
            final Object servicePojo = serviceAdapter.getObject();
            final String id = ServiceUtil.id(servicePojo);
            if (serviceId.equals(id)) {
                return serviceAdapter;
            }
        }
        throw RestfulObjectsApplicationException.create(HttpStatusCode.NOT_FOUND, "Could not locate service '%s'", serviceId);
    }

    protected String getOidStr(final ObjectAdapter objectAdapter) {
        return OidUtils.getOidStr(resourceContext, objectAdapter);
    }

    // //////////////////////////////////////////////////////////////
    // Responses
    // //////////////////////////////////////////////////////////////

    public static ResponseBuilder responseOfNoContent() {
        return responseOf(HttpStatusCode.NO_CONTENT);
    }

    public static ResponseBuilder responseOfOk(final ReprRenderer<?, ?> renderer, final Caching caching) {
        return responseOfOk(renderer, caching, null);
    }

    public static ResponseBuilder responseOfOk(final ReprRenderer<?, ?> renderer, final Caching caching, final Version version) {
        final RepresentationType representationType = renderer.getRepresentationType();
        final ResponseBuilder response = responseOf(HttpStatusCode.OK).type(representationType.getMediaType()).cacheControl(caching.getCacheControl()).entity(jsonFor(renderer.render()));
        return addLastModifiedAndETagIfAvailable(response, version);
    }

    private static ResponseBuilder responseOf(final HttpStatusCode httpStatusCode) {
        return Response.status(httpStatusCode.getJaxrsStatusType()).type(MediaType.APPLICATION_JSON_TYPE);
    }

    public static ResponseBuilder addLastModifiedAndETagIfAvailable(final ResponseBuilder responseBuilder, final Version version) {
        if (version != null && version.getTime() != null) {
            final Date time = version.getTime();
            responseBuilder.lastModified(time);
            responseBuilder.tag("" + time);
        }
        return responseBuilder;
    }

    // //////////////////////////////////////////////////////////////
    // Dependencies (from singletons)
    // //////////////////////////////////////////////////////////////

    protected ExpressiveObjectsConfiguration getConfiguration () {
        return ExpressiveObjectsContext.getConfiguration();
    }

    protected AuthenticationSession getAuthenticationSession() {
        return ExpressiveObjectsContext.getAuthenticationSession();
    }

    protected SpecificationLoaderSpi getSpecificationLoader() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }

    protected AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }

    protected PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    private OidGenerator getOidGenerator() {
        return getPersistenceSession().getOidGenerator();
    }

    protected Localization getLocalization() {
        return ExpressiveObjectsContext.getLocalization();
    }

    // //////////////////////////////////////////////////////////////
    // Dependencies (injected via @Context)
    // //////////////////////////////////////////////////////////////

    protected HttpServletRequest getServletRequest() {
        return getResourceContext().getHttpServletRequest();
    }

}
