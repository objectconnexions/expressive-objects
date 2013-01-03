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

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.encodeable.EncodableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse.HttpStatusCode;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.Rel;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.RestfulObjectsApplicationException;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.ResourceContext;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.LinkFollower;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRenderer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRendererAbstract;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRendererFactoryAbstract;

public class ScalarValueReprRenderer extends ReprRendererAbstract<ScalarValueReprRenderer, ObjectAdapter> {

    private final JsonValueEncoder jsonValueEncoder = new JsonValueEncoder();
    private ObjectSpecification returnType;

    public static class Factory extends ReprRendererFactoryAbstract {
        public Factory() {
            super(RepresentationType.SCALAR_VALUE);
        }

        @Override
        public ReprRenderer<?, ?> newRenderer(final ResourceContext resourceContext, final LinkFollower linkFollower, final JsonRepresentation representation) {
            return new ScalarValueReprRenderer(resourceContext, linkFollower, getRepresentationType(), representation);
        }
    }

    private ScalarValueReprRenderer(final ResourceContext resourceContext, final LinkFollower linkFollower, final RepresentationType representationType, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, representationType, representation);
    }

    @Override
    public ScalarValueReprRenderer with(final ObjectAdapter objectAdapter) {
        final EncodableFacet facet = objectAdapter.getSpecification().getFacet(EncodableFacet.class);
        if (facet == null) {
            throw RestfulObjectsApplicationException.create(HttpStatusCode.INTERNAL_SERVER_ERROR, "Not an (encodable) value", objectAdapter.titleString());
        }
        final Object value = jsonValueEncoder.asObject(objectAdapter);

        representation.mapPut("value", value);
        return this;
    }

    @Override
    public JsonRepresentation render() {

        addLinkToReturnType();

        getExtensions();

        return representation;
    }

    public ScalarValueReprRenderer withReturnType(final ObjectSpecification returnType) {
        this.returnType = returnType;
        return this;
    }

    private void addLinkToReturnType() {
        addLink(Rel.RETURN_TYPE, returnType);
    }

}