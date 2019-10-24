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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.typicallen.fromtype;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethodParameter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typicallen.TypicalLengthFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;

public class TypicalLengthFacetForParameterDerivedFromTypeFacetFactory extends FacetFactoryAbstract {

    public TypicalLengthFacetForParameterDerivedFromTypeFacetFactory() {
        super(FeatureType.PARAMETERS_ONLY);
    }

    @Override
    public void processParams(final ProcessParameterContext processParameterContext) {
        final Class<?> type = processParameterContext.getMethod().getParameterTypes()[processParameterContext.getParamNum()];
        final FacetedMethodParameter facetHolder = processParameterContext.getFacetHolder();
        addFacetDerivedFromTypeIfPresent(facetHolder, type);
    }

    private void addFacetDerivedFromTypeIfPresent(final FacetHolder holder, final Class<?> type) {
        final TypicalLengthFacet facet = getTypicalLengthFacet(type);
        if (facet != null) {
            FacetUtil.addFacet(new TypicalLengthFacetForParameterDerivedFromType(facet, holder));
        }
    }

    private TypicalLengthFacet getTypicalLengthFacet(final Class<?> type) {
        final ObjectSpecification paramTypeSpec = getSpecificationLoader().loadSpecification(type);
        return paramTypeSpec.getFacet(TypicalLengthFacet.class);
    }

}
