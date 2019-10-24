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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.defaults.fromtype;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.defaults.ActionDefaultsFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.defaults.DefaultedFacet;

public class ActionParameterDefaultFacetDerivedFromTypeFacets extends ActionDefaultsFacetAbstract {

    private final DefaultedFacet[] defaultedFacets;

    public ActionParameterDefaultFacetDerivedFromTypeFacets(final DefaultedFacet[] defaultedFacets, final FacetHolder holder) {
        super(holder, Derivation.DERIVED);
        this.defaultedFacets = defaultedFacets;
    }

    /**
     * Return the defaults.
     * 
     * <p>
     * Note that we get the defaults fresh each time in case the defaults might
     * conceivably change.
     */
    @Override
    public Object[] getDefaults(final ObjectAdapter inObject) {
        final Object[] defaults = new Object[defaultedFacets.length];
        for (int i = 0; i < defaults.length; i++) {
            if (defaultedFacets[i] != null) {
                defaults[i] = defaultedFacets[i].getDefault();
            }
        }
        return defaults;
    }

}
