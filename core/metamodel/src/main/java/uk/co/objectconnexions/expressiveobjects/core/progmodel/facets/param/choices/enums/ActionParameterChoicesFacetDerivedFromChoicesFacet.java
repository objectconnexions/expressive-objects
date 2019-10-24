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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.choices.enums;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.TypedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.choices.ChoicesFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.choices.ActionParameterChoicesFacetAbstract;

public class ActionParameterChoicesFacetDerivedFromChoicesFacet extends ActionParameterChoicesFacetAbstract {

    public ActionParameterChoicesFacetDerivedFromChoicesFacet(final FacetHolder holder, final SpecificationLoader specificationLookup, final AdapterManager adapterManager) {
        super(holder, specificationLookup, adapterManager);
    }

    @Override
    public Object[] getChoices(final ObjectAdapter adapter) {
        final FacetHolder facetHolder = getFacetHolder();
        final TypedHolder paramPeer = (TypedHolder) facetHolder;
        final ObjectSpecification noSpec = getSpecification(paramPeer.getType());
        final ChoicesFacet choicesFacet = noSpec.getFacet(ChoicesFacet.class);
        if (choicesFacet == null) {
            return new Object[0];
        }
        return choicesFacet.getChoices(adapter);
    }

}
