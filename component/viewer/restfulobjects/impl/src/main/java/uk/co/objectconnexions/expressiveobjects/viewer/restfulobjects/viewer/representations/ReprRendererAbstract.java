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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.Persistor;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.UpdateNotifier;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.Rel;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.ResourceContext;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domainobjects.DomainObjectReprRenderer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domaintypes.DomainTypeReprRenderer;

public abstract class ReprRendererAbstract<R extends ReprRendererAbstract<R, T>, T> implements ReprRenderer<R, T> {

    protected final ResourceContext resourceContext;
    private final LinkFollower linkFollower;
    private final RepresentationType representationType;
    protected final JsonRepresentation representation;

    protected boolean includesSelf;

    public ReprRendererAbstract(final ResourceContext resourceContext, final LinkFollower linkFollower, final RepresentationType representationType, final JsonRepresentation representation) {
        this.resourceContext = resourceContext;
        this.linkFollower = asProvidedElseCreate(linkFollower);
        this.representationType = representationType;
        this.representation = representation;
    }

    public ResourceContext getResourceContext() {
        return resourceContext;
    }

    public LinkFollower getLinkFollower() {
        return linkFollower;
    }

    private LinkFollower asProvidedElseCreate(final LinkFollower linkFollower) {
        if (linkFollower != null) {
            return linkFollower;
        }
        return LinkFollower.create(resourceContext.getFollowLinks());
    }

    @Override
    public RepresentationType getRepresentationType() {
        return representationType;
    }

    @SuppressWarnings("unchecked")
    public R includesSelf() {
        this.includesSelf = true;
        return (R) this;
    }

    public R withSelf(final String href) {
        if (href != null) {
            getLinks().arrayAdd(LinkBuilder.newBuilder(resourceContext, Rel.SELF, representationType, href).build());
        }
        return cast(this);
    }

    public R withSelf(final JsonRepresentation link) {
        final String rel = link.getString("rel");
        if (rel == null || !rel.equals(Rel.SELF.getName())) {
            throw new IllegalArgumentException("Provided link does not have a 'rel' of 'self'; was: " + link);
        }
        if (link != null) {
            getLinks().arrayAdd(link);
        }
        return cast(this);
    }

    /**
     * Will lazily create links array as required
     */
    protected JsonRepresentation getLinks() {
        JsonRepresentation links = representation.getArray("links");
        if (links == null) {
            links = JsonRepresentation.newArray();
            representation.mapPut("links", links);
        }
        return links;
    }

    protected void addLink(final Rel rel, final ObjectSpecification objectSpec) {
        if (objectSpec == null) {
            return;
        }
        final LinkBuilder linkBuilder = DomainTypeReprRenderer.newLinkToBuilder(getResourceContext(), rel, objectSpec);
        getLinks().arrayAdd(linkBuilder.build());
    }

    /**
     * Will lazily create extensions map as required
     */
    protected JsonRepresentation getExtensions() {
        JsonRepresentation extensions = representation.getMap("extensions");
        if (extensions == null) {
            extensions = JsonRepresentation.newMap();
            representation.mapPut("extensions", extensions);
        }
        return extensions;
    }

    public R withExtensions(final JsonRepresentation extensions) {
        if (!extensions.isMap()) {
            throw new IllegalArgumentException("extensions must be a map");
        }
        representation.mapPut("extensions", extensions);
        return cast(this);
    }

    @SuppressWarnings("unchecked")
    protected static <R extends ReprRendererAbstract<R, T>, T> R cast(final ReprRendererAbstract<R, T> builder) {
        return (R) builder;
    }

    @Override
    public abstract JsonRepresentation render();

    /**
     * Convenience for representations that are returned from objects that
     * mutate state.
     */
    protected final void addExtensionsExpressiveObjectsProprietaryChangedObjects() {
        final UpdateNotifier updateNotifier = getUpdateNotifier();

        addToExtensions("changed", updateNotifier.getChangedObjects());
        addToExtensions("disposed", updateNotifier.getDisposedObjects());
    }

    private void addToExtensions(final String key, final List<ObjectAdapter> adapters) {
        final JsonRepresentation adapterList = JsonRepresentation.newArray();
        getExtensions().mapPut(key, adapterList);
        for (final ObjectAdapter adapter : adapters) {
            adapterList.arrayAdd(DomainObjectReprRenderer.newLinkToBuilder(getResourceContext(), Rel.OBJECT, adapter).build());
        }
    }

    protected List<ObjectAdapter> getServiceAdapters() {
        return ExpressiveObjectsContext.getPersistenceSession().getServices();
    }

    protected Persistor getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    protected AuthenticationSession getSession() {
        return ExpressiveObjectsContext.getAuthenticationSession();
    }

    protected Localization getLocalization() {
        return ExpressiveObjectsContext.getLocalization();
    }

    protected UpdateNotifier getUpdateNotifier() {
        return ExpressiveObjectsContext.getCurrentTransaction().getUpdateNotifier();
    }

}
