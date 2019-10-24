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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.mandatory;

import uk.co.objectconnexions.expressiveobjects.applib.events.ValidityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MarkerFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ActionArgumentContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.PropertyModifyContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ProposedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;

public abstract class MandatoryFacetAbstract extends MarkerFacetAbstract implements MandatoryFacet {

    public static Class<? extends Facet> type() {
        return MandatoryFacet.class;
    }

    public MandatoryFacetAbstract(final FacetHolder holder) {
        super(type(), holder);
    }

    @Override
    public String invalidates(final ValidityContext<? extends ValidityEvent> context) {
        if (!(context instanceof PropertyModifyContext) && !(context instanceof ActionArgumentContext)) {
            return null;
        }
        if (!(context instanceof ProposedHolder)) {
            // shouldn't happen, since both the above should hold a proposed
            // value/argument
            return null;
        }
        final ProposedHolder proposedHolder = (ProposedHolder) context;
        return isRequiredButNull(proposedHolder.getProposed()) ? "Mandatory" : null;
    }
}
