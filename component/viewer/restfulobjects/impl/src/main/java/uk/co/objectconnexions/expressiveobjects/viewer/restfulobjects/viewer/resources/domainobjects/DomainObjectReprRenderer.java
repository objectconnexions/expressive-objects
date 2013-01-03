/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domainobjects;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.title.TitleFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.value.ValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServiceUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectActionSet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionContainer.Contributed;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.Rel;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.ResourceContext;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.LinkBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.LinkFollower;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.RendererFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.RendererFactoryRegistry;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRenderer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRendererAbstract;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRendererFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domaintypes.DomainTypeReprRenderer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.util.OidUtils;

public class DomainObjectReprRenderer extends ReprRendererAbstract<DomainObjectReprRenderer, ObjectAdapter> {

    public static class Factory extends ReprRendererFactoryAbstract {

        public Factory() {
            super(RepresentationType.DOMAIN_OBJECT);
        }

        @Override
        public ReprRenderer<?, ?> newRenderer(final ResourceContext resourceContext, final LinkFollower linkFollower, final JsonRepresentation representation) {
            return new DomainObjectReprRenderer(resourceContext, linkFollower, getRepresentationType(), representation);
        }
    }

    public static LinkBuilder newLinkToBuilder(final ResourceContext resourceContext, final Rel rel, final ObjectAdapter elementAdapter) {
        final String oidStr = ((RootOid) elementAdapter.getOid()).enString(getOidMarshaller());
        final String url = "objects/" + oidStr;
        final LinkBuilder builder = LinkBuilder.newBuilder(resourceContext, rel, RepresentationType.DOMAIN_OBJECT, url).withTitle(elementAdapter.titleString());
        return builder;
    }

    private static enum Mode {
        REGULAR(false, true), PERSIST_LINK_ARGUMENTS(true, true), MODIFY_PROPERTIES_LINK_ARGUMENTS(true, false);

        private final boolean cutDown;
        private final boolean describedBy;

        private Mode(final boolean cutDown, final boolean describedBy) {
            this.cutDown = cutDown;
            this.describedBy = describedBy;
        }

        public boolean isCutDown() {
            return cutDown;
        }

        public boolean includesDescribedBy() {
            return describedBy;
        }
    }

    private ObjectAdapterLinkTo linkToBuilder;
    private ObjectAdapter objectAdapter;
    private Mode mode = Mode.REGULAR;

    private DomainObjectReprRenderer(final ResourceContext resourceContext, final LinkFollower linkFollower, final RepresentationType representationType, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, representationType, representation);
        usingLinkToBuilder(new DomainObjectLinkTo());
    }

    /**
     * Override the default {@link ObjectAdapterLinkTo} (that is used for
     * generating links in {@link #linkTo(ObjectAdapter)}).
     */
    public DomainObjectReprRenderer usingLinkToBuilder(final ObjectAdapterLinkTo objectAdapterLinkToBuilder) {
        this.linkToBuilder = objectAdapterLinkToBuilder.usingResourceContext(resourceContext);
        return this;
    }

    @Override
    public DomainObjectReprRenderer with(final ObjectAdapter objectAdapter) {
        this.objectAdapter = objectAdapter;
        return this;
    }

    @Override
    public JsonRepresentation render() {

        // self, oid
        if (!mode.isCutDown()) {
            if (objectAdapter.representsPersistent()) {
                if (includesSelf) {
                    final JsonRepresentation self = linkToBuilder.with(objectAdapter).builder(Rel.SELF).build();
                    getLinks().arrayAdd(self);
                }
                representation.mapPut("oid", getOidStr());
            }
        }

        // title
        if (!mode.isCutDown()) {
            final String title = objectAdapter.titleString();
            representation.mapPut("title", title);

        }

        // serviceId
        if (!mode.isCutDown()) {
            final boolean isService = objectAdapter.getSpecification().isService();
            if (isService) {
                representation.mapPut("serviceId", ServiceUtil.id(objectAdapter.getObject()));
            }
        }

        // members
        withMembers(objectAdapter);

        // described by
        if (mode.includesDescribedBy()) {
            getLinks().arrayAdd(DomainTypeReprRenderer.newLinkToBuilder(getResourceContext(), Rel.DESCRIBEDBY, objectAdapter.getSpecification()).build());
        }

        if (!mode.isCutDown()) {
            // update/persist
            addPersistLinkIfTransient();
            addUpdatePropertiesLinkIfPersistent();

            // extensions
            final boolean isService = objectAdapter.getSpecification().isService();
            getExtensions().mapPut("isService", isService);
            getExtensions().mapPut("isPersistent", objectAdapter.representsPersistent());
        }

        return representation;
    }

    private String getOidStr() {
        return OidUtils.getOidStr(resourceContext, objectAdapter);
    }

    private DomainObjectReprRenderer withMembers(final ObjectAdapter objectAdapter) {
        final JsonRepresentation members = JsonRepresentation.newArray();
        final List<ObjectAssociation> associations = objectAdapter.getSpecification().getAssociations();
        addAssociations(objectAdapter, members, associations);

        if (!mode.isCutDown()) {
            final List<ObjectAction> actions = objectAdapter.getSpecification().getObjectActions(Contributed.INCLUDED);
            addActions(objectAdapter, actions, members);
        }
        representation.mapPut("members", members);
        return this;
    }

    private void addAssociations(final ObjectAdapter objectAdapter, final JsonRepresentation members, final List<ObjectAssociation> associations) {
        final LinkFollower linkFollower = getLinkFollower().follow("members");
        for (final ObjectAssociation assoc : associations) {

            if (!mode.isCutDown()) {
                final Consent visibility = assoc.isVisible(getSession(), objectAdapter, resourceContext.getWhere());
                if (!visibility.isAllowed()) {
                    continue;
                }
            }
            if (assoc instanceof OneToOneAssociation) {
                final OneToOneAssociation property = (OneToOneAssociation) assoc;

                final RendererFactory factory = getRendererFactoryRegistry().find(RepresentationType.OBJECT_PROPERTY);
                final ObjectPropertyReprRenderer renderer = (ObjectPropertyReprRenderer) factory.newRenderer(getResourceContext(), linkFollower, JsonRepresentation.newMap());

                renderer.with(new ObjectAndProperty(objectAdapter, property)).usingLinkTo(linkToBuilder);

                if (mode.isCutDown()) {
                    renderer.asArguments();
                }

                members.arrayAdd(renderer.render());
            }

            if (mode.isCutDown()) {
                // don't include collections
                continue;
            }
            if (assoc instanceof OneToManyAssociation) {
                final OneToManyAssociation collection = (OneToManyAssociation) assoc;

                final RendererFactory factory = getRendererFactoryRegistry().find(RepresentationType.OBJECT_COLLECTION);
                final ObjectCollectionReprRenderer renderer = (ObjectCollectionReprRenderer) factory.newRenderer(getResourceContext(), linkFollower, JsonRepresentation.newMap());

                renderer.with(new ObjectAndCollection(objectAdapter, collection)).usingLinkTo(linkToBuilder);

                members.arrayAdd(renderer.render());
            }
        }
    }

    private void addActions(final ObjectAdapter objectAdapter, final List<ObjectAction> actions, final JsonRepresentation members) {
        final LinkFollower linkFollower = getLinkFollower().follow("members");
        for (final ObjectAction action : actions) {
            final Consent visibility = action.isVisible(getSession(), objectAdapter, resourceContext.getWhere());
            if (!visibility.isAllowed()) {
                continue;
            }
            if (action.getType().isSet()) {
                final ObjectActionSet objectActionSet = (ObjectActionSet) action;
                final List<ObjectAction> subactions = objectActionSet.getActions();
                addActions(objectAdapter, subactions, members);

            } else {

                final RendererFactory factory = getRendererFactoryRegistry().find(RepresentationType.OBJECT_ACTION);
                final ObjectActionReprRenderer renderer = (ObjectActionReprRenderer) factory.newRenderer(getResourceContext(), linkFollower, JsonRepresentation.newMap());

                renderer.with(new ObjectAndAction(objectAdapter, action)).usingLinkTo(linkToBuilder);

                members.arrayAdd(renderer.render());
            }
        }
    }

    private void addPersistLinkIfTransient() {
        if (objectAdapter.representsPersistent()) {
            return;
        }
        final RendererFactory rendererFactory = getRendererFactoryRegistry().find(RepresentationType.DOMAIN_OBJECT);
        final DomainObjectReprRenderer renderer = (DomainObjectReprRenderer) rendererFactory.newRenderer(getResourceContext(), null, JsonRepresentation.newMap());
        final JsonRepresentation domainObjectRepr = renderer.with(objectAdapter).asPersistLinkArguments().render();

        final LinkBuilder persistLinkBuilder = LinkBuilder.newBuilder(getResourceContext(), Rel.PERSIST, RepresentationType.DOMAIN_OBJECT, "objects/").withHttpMethod(HttpMethod.POST).withArguments(domainObjectRepr);
        getLinks().arrayAdd(persistLinkBuilder.build());
    }

    private DomainObjectReprRenderer asPersistLinkArguments() {
        this.mode = Mode.PERSIST_LINK_ARGUMENTS;
        return this;
    }

    private DomainObjectReprRenderer asModifyPropertiesLinkArguments() {
        this.mode = Mode.MODIFY_PROPERTIES_LINK_ARGUMENTS;
        return this;
    }

    private void addUpdatePropertiesLinkIfPersistent() {
        if (!objectAdapter.representsPersistent()) {
            return;
        }

        final RendererFactory rendererFactory = getRendererFactoryRegistry().find(RepresentationType.DOMAIN_OBJECT);
        final DomainObjectReprRenderer renderer = (DomainObjectReprRenderer) rendererFactory.newRenderer(getResourceContext(), null, JsonRepresentation.newMap());
        final JsonRepresentation domainObjectRepr = renderer.with(objectAdapter).asModifyPropertiesLinkArguments().render();

        final LinkBuilder persistLinkBuilder = LinkBuilder.newBuilder(getResourceContext(), Rel.MODIFY, RepresentationType.DOMAIN_OBJECT, "objects/%s", getOidStr()).withHttpMethod(HttpMethod.PUT).withArguments(domainObjectRepr);
        getLinks().arrayAdd(persistLinkBuilder.build());
    }

    protected RendererFactoryRegistry getRendererFactoryRegistry() {
        return RendererFactoryRegistry.instance;
    }

    // ///////////////////////////////////////////////////////////////////
    //
    // ///////////////////////////////////////////////////////////////////

    public static Object valueOrRef(final ResourceContext resourceContext, final ObjectAdapter objectAdapter, final ObjectSpecification objectSpec) {
        final ValueFacet valueFacet = objectSpec.getFacet(ValueFacet.class);
        if (valueFacet != null) {
            return new JsonValueEncoder().asObject(objectAdapter);
        }
        final TitleFacet titleFacet = objectSpec.getFacet(TitleFacet.class);
        final String title = titleFacet.title(objectAdapter, resourceContext.getLocalization());
        return DomainObjectReprRenderer.newLinkToBuilder(resourceContext, Rel.OBJECT, objectAdapter).withTitle(title).build();
    }

 
    
    // ///////////////////////////////////////////////////////////////////
    // dependencies (from context)
    // ///////////////////////////////////////////////////////////////////

    protected static OidMarshaller getOidMarshaller() {
		return ExpressiveObjectsContext.getOidMarshaller();
	}

}