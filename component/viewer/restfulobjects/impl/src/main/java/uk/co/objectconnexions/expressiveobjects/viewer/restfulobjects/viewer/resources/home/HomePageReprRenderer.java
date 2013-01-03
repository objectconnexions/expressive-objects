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

import java.util.Collection;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
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
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domainobjects.DomainServiceLinkTo;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domainobjects.ListReprRenderer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domaintypes.TypeListReprRenderer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.user.UserReprRenderer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.version.VersionReprRenderer;

public class HomePageReprRenderer extends ReprRendererAbstract<HomePageReprRenderer, Void> {

    public static class Factory extends ReprRendererFactoryAbstract {
        public Factory() {
            super(RepresentationType.HOME_PAGE);
        }

        @Override
        public ReprRenderer<?, ?> newRenderer(final ResourceContext resourceContext, final LinkFollower linkFollower, final JsonRepresentation representation) {
            return new HomePageReprRenderer(resourceContext, linkFollower, getRepresentationType(), representation);
        }
    }

    private HomePageReprRenderer(final ResourceContext resourceContext, final LinkFollower linkFollower, final RepresentationType representationType, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, representationType, representation);
    }

    @Override
    public HomePageReprRenderer with(final Void t) {
        return this;
    }

    @Override
    public JsonRepresentation render() {

        // self
        if (includesSelf) {
            addLinkToSelf(representation);
        }

        addLinkToUser();
        addLinkToServices();
        addLinkToVersion();
        addLinkToDomainTypes();

        // inks and extensions
        representation.mapPut("extensions", JsonRepresentation.newMap());

        return representation;
    }

    private void addLinkToSelf(final JsonRepresentation representation) {
        final JsonRepresentation link = LinkBuilder.newBuilder(resourceContext, Rel.SELF, getRepresentationType(), "").build();

        final LinkFollower linkFollower = getLinkFollower().follow("links[rel=self]");
        if (linkFollower.matches(link)) {

            final RendererFactory factory = RendererFactoryRegistry.instance.find(RepresentationType.HOME_PAGE);
            final HomePageReprRenderer renderer = (HomePageReprRenderer) factory.newRenderer(getResourceContext(), linkFollower, JsonRepresentation.newMap());

            link.mapPut("value", renderer.render());
        }
        getLinks().arrayAdd(link);
    }

    private void addLinkToVersion() {
        final JsonRepresentation link = LinkBuilder.newBuilder(getResourceContext(), Rel.VERSION, RepresentationType.VERSION, "version").build();

        final LinkFollower linkFollower = getLinkFollower().follow("links[rel=version]");
        if (linkFollower.matches(link)) {

            final RendererFactory factory = RendererFactoryRegistry.instance.find(RepresentationType.VERSION);
            final VersionReprRenderer renderer = (VersionReprRenderer) factory.newRenderer(getResourceContext(), linkFollower, JsonRepresentation.newMap());

            link.mapPut("value", renderer.render());
        }

        getLinks().arrayAdd(link);
    }

    private void addLinkToServices() {

        final JsonRepresentation link = LinkBuilder.newBuilder(getResourceContext(), Rel.SERVICES, RepresentationType.LIST, "services").build();

        final LinkFollower linkFollower = getLinkFollower().follow("links[rel=services]");
        if (linkFollower.matches(link)) {

            final List<ObjectAdapter> serviceAdapters = getResourceContext().getPersistenceSession().getServices();

            final RendererFactory factory = RendererFactoryRegistry.instance.find(RepresentationType.LIST);

            final ListReprRenderer renderer = (ListReprRenderer) factory.newRenderer(getResourceContext(), linkFollower, JsonRepresentation.newMap());
            renderer.usingLinkToBuilder(new DomainServiceLinkTo()).withSelf("services").with(serviceAdapters);

            link.mapPut("value", renderer.render());
        }

        getLinks().arrayAdd(link);
    }

    private void addLinkToUser() {
        final JsonRepresentation link = LinkBuilder.newBuilder(getResourceContext(), Rel.USER, RepresentationType.USER, "user").build();

        final LinkFollower linkFollower = getLinkFollower().follow("links[rel=user]");
        if (linkFollower.matches(link)) {
            final RendererFactory factory = RendererFactoryRegistry.instance.find(RepresentationType.USER);
            final UserReprRenderer renderer = (UserReprRenderer) factory.newRenderer(getResourceContext(), linkFollower, JsonRepresentation.newMap());
            renderer.with(getResourceContext().getAuthenticationSession());

            link.mapPut("value", renderer.render());
        }

        getLinks().arrayAdd(link);
    }

    private void addLinkToDomainTypes() {

        final JsonRepresentation link = LinkBuilder.newBuilder(getResourceContext(), Rel.TYPES, RepresentationType.TYPE_LIST, "domainTypes").build();

        final LinkFollower linkFollower = getLinkFollower().follow("links[rel=types]");
        if (linkFollower.matches(link)) {

            final RendererFactory factory = RendererFactoryRegistry.instance.find(RepresentationType.TYPE_LIST);

            final TypeListReprRenderer renderer = (TypeListReprRenderer) factory.newRenderer(getResourceContext(), linkFollower, JsonRepresentation.newMap());

            final Collection<ObjectSpecification> specifications = getResourceContext().getSpecificationLookup().allSpecifications();

            renderer.withSelf("domainTypes").with(specifications);

            link.mapPut("value", renderer.render());
        }

        getLinks().arrayAdd(link);
    }

}