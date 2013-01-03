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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domaintypes;

import java.util.Collection;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.Rel;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.ResourceContext;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.LinkBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.LinkFollower;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRenderer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRendererAbstract;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRendererFactoryAbstract;

public class TypeListReprRenderer extends ReprRendererAbstract<TypeListReprRenderer, Collection<ObjectSpecification>> {

    private Collection<ObjectSpecification> specifications;

    public static class Factory extends ReprRendererFactoryAbstract {
        public Factory() {
            super(RepresentationType.TYPE_LIST);
        }

        @Override
        public ReprRenderer<?, ?> newRenderer(final ResourceContext resourceContext, final LinkFollower linkFollower, final JsonRepresentation representation) {
            return new TypeListReprRenderer(resourceContext, linkFollower, getRepresentationType(), representation);
        }
    }

    private TypeListReprRenderer(final ResourceContext resourceContext, final LinkFollower linkFollower, final RepresentationType representationType, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, representationType, representation);
    }

    @Override
    public TypeListReprRenderer with(final Collection<ObjectSpecification> specifications) {
        this.specifications = specifications;
        return this;
    }

    @Override
    public JsonRepresentation render() {

        // self
        if (includesSelf) {
            withSelf("domainTypes");
        }

        final JsonRepresentation specList = JsonRepresentation.newArray();
        for (final ObjectSpecification objectSpec : specifications) {
            final LinkBuilder linkBuilder = LinkBuilder.newBuilder(getResourceContext(), Rel.DOMAIN_TYPE, RepresentationType.DOMAIN_TYPE, "domainTypes/%s", objectSpec.getFullIdentifier());
            specList.arrayAdd(linkBuilder.build());
        }

        representation.mapPut("values", specList);

        getExtensions(); // empty

        return representation;
    }

}