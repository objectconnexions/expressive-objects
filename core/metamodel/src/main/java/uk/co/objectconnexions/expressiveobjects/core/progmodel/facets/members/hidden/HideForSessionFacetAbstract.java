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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden;

import uk.co.objectconnexions.expressiveobjects.applib.events.VisibilityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.VisibilityContext;

/**
 * Hide a property, collection or action based on the current session.
 * 
 * <p>
 * In the standard Expressive Objects Programming Model, corresponds to invoking the
 * <tt>hideXxx(UserMemento)</tt> support method for the member.
 */
public abstract class HideForSessionFacetAbstract extends FacetAbstract implements HideForSessionFacet {

    public static Class<? extends Facet> type() {
        return HideForSessionFacet.class;
    }

    public HideForSessionFacetAbstract(final FacetHolder holder) {
        super(type(), holder, Derivation.NOT_DERIVED);
    }

    @Override
    public String hides(final VisibilityContext<? extends VisibilityEvent> ic) {
        return hiddenReason(ic.getSession());
    }
}
