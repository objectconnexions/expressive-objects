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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.disabled.fromimmutable;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.immutable.ImmutableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;

/**
 * REVIEW: I'm not sure this {@link FacetFactory} actually makes sense. Just
 * because a type is immutable, doesn't imply that the property can't change the
 * instance that it refers to?
 */
public class DisabledFacetForPropertyDerivedFromImmutableTypeFacetFactory extends FacetFactoryAbstract {

    public DisabledFacetForPropertyDerivedFromImmutableTypeFacetFactory() {
        super(FeatureType.PROPERTIES_ONLY);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final ObjectSpecification spec = getSpecificationLoader().loadSpecification(processMethodContext.getMethod().getDeclaringClass());
        if (spec.containsDoOpFacet(ImmutableFacet.class)) {
            final ImmutableFacet immutableFacet = spec.getFacet(ImmutableFacet.class);
            final FacetedMethod facetHolder = processMethodContext.getFacetHolder();
            FacetUtil.addFacet(new DisabledFacetForPropertyDerivedFromImmutable(immutableFacet, facetHolder));
        }
    }

}
