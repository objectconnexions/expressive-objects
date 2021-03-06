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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.validprops;

import uk.co.objectconnexions.expressiveobjects.applib.events.ValidityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ObjectValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;

public abstract class ObjectValidPropertiesFacetAbstract extends FacetAbstract implements ObjectValidPropertiesFacet {

    public static Class<? extends Facet> type() {
        return ObjectValidPropertiesFacet.class;
    }

    public ObjectValidPropertiesFacetAbstract(final FacetHolder holder) {
        super(type(), holder, Derivation.NOT_DERIVED);
    }

    @Override
    public String invalidates(final ValidityContext<? extends ValidityEvent> ic) {
        if (!(ic instanceof ObjectValidityContext)) {
            return null;
        }
        final ObjectValidityContext validityContext = (ObjectValidityContext) ic;
        return invalidReason(validityContext);
    }

}
