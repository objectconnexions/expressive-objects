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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.choices;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.param.choices.ActionParameterChoicesFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;

public abstract class ActionParameterChoicesFacetAbstract extends FacetAbstract implements ActionParameterChoicesFacet {

    public static Class<? extends Facet> type() {
        return ActionParameterChoicesFacet.class;
    }

    private final SpecificationLoader specificationLookup;
    private final AdapterManager adapterManager;

    public ActionParameterChoicesFacetAbstract(final FacetHolder holder, final SpecificationLoader specificationLookup, final AdapterManager adapterManager) {
        super(type(), holder, Derivation.NOT_DERIVED);
        this.specificationLookup = specificationLookup;
        this.adapterManager = adapterManager;
    }

    protected ObjectSpecification getSpecification(final Class<?> type) {
        return type != null ? getSpecificationLookup().loadSpecification(type) : null;
    }

    // /////////////////////////////////////////////////////////
    // Dependencies
    // /////////////////////////////////////////////////////////

    protected SpecificationLoader getSpecificationLookup() {
        return specificationLookup;
    }

    protected AdapterManager getAdapterManager() {
        return adapterManager;
    }

}
