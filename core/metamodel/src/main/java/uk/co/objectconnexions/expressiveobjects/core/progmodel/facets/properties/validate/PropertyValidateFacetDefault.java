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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.validate;

import uk.co.objectconnexions.expressiveobjects.applib.events.ValidityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;

/**
 * Non checking property validation facet that provides default behaviour for
 * all properties.
 */
public class PropertyValidateFacetDefault extends FacetAbstract implements PropertyValidateFacet {

    @Override
    public String invalidates(final ValidityContext<? extends ValidityEvent> ic) {
        return null;
    }

    public PropertyValidateFacetDefault(final FacetHolder holder) {
        super(PropertyValidateFacet.class, holder, Derivation.NOT_DERIVED);
    }

    @Override
    public String invalidReason(final ObjectAdapter target, final ObjectAdapter proposedValue) {
        return null;
    }

}
