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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.defaults.fromtype;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManagerAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.defaults.PropertyDefaultFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.defaults.DefaultedFacet;

public class PropertyDefaultDerivedFromTypeFacetFactory extends FacetFactoryAbstract implements AdapterManagerAware {

    private AdapterManager adapterManager;

    public PropertyDefaultDerivedFromTypeFacetFactory() {
        super(FeatureType.PROPERTIES_ONLY);
    }

    /**
     * If there is a {@link DefaultedFacet} on the properties return type, then
     * installs a {@link PropertyDefaultFacet} for the property with the same
     * default.
     */
    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        // don't overwrite any defaults that might already picked up
        final PropertyDefaultFacet existingDefaultFacet = processMethodContext.getFacetHolder().getFacet(PropertyDefaultFacet.class);
        if (existingDefaultFacet != null && !existingDefaultFacet.isNoop()) {
            return;
        }

        // try to infer defaults from the underlying return type
        final Class<?> returnType = processMethodContext.getMethod().getReturnType();
        final DefaultedFacet returnTypeDefaultedFacet = getDefaultedFacet(returnType);
        if (returnTypeDefaultedFacet != null) {
            final PropertyDefaultFacetDerivedFromDefaultedFacet propertyFacet = new PropertyDefaultFacetDerivedFromDefaultedFacet(returnTypeDefaultedFacet, processMethodContext.getFacetHolder(), getAdapterManager());
            FacetUtil.addFacet(propertyFacet);
        }
    }

    private DefaultedFacet getDefaultedFacet(final Class<?> paramType) {
        final ObjectSpecification paramTypeSpec = getSpecificationLoader().loadSpecification(paramType);
        return paramTypeSpec.getFacet(DefaultedFacet.class);
    }

    // ////////////////////////////////////////////////////////////////////
    // Injected
    // ////////////////////////////////////////////////////////////////////

    public AdapterManager getAdapterManager() {
        return adapterManager;
    }

    @Override
    public void setAdapterManager(final AdapterManager adapterMap) {
        this.adapterManager = adapterMap;
    }

}
