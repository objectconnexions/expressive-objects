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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.bounded;

import uk.co.objectconnexions.expressiveobjects.applib.events.UsabilityEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.ValidityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MarkerFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.bounded.BoundedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ObjectValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.UsabilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;

public abstract class BoundedFacetAbstract extends MarkerFacetAbstract implements BoundedFacet {

    public static Class<? extends Facet> type() {
        return BoundedFacet.class;
    }

    public BoundedFacetAbstract(final FacetHolder holder) {
        super(type(), holder);
    }

    /**
     * Hook method for subclasses to override.
     */
    public abstract String disabledReason(ObjectAdapter objectAdapter);

    @Override
    public String invalidates(final ValidityContext<? extends ValidityEvent> context) {
        if (!(context instanceof ObjectValidityContext)) {
            return null;
        }
        final ObjectAdapter target = context.getTarget();
        if(target == null) {
            return null;
        }
        
        // ensure that the target is of the correct type
        if(!(getFacetHolder() instanceof ObjectSpecification)) {
            // should never be the case
            return null;
        }
        
        final ObjectSpecification objectSpec = (ObjectSpecification) getFacetHolder();
        return objectSpec == target.getSpecification()? null: "Invalid type";
    }

    @Override
    public String disables(final UsabilityContext<? extends UsabilityEvent> context) {
        final ObjectAdapter target = context.getTarget();
        return disabledReason(target);
    }

}
