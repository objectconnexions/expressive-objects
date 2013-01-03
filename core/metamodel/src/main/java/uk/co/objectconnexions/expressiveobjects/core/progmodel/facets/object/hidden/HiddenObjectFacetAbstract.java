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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.hidden;

import uk.co.objectconnexions.expressiveobjects.applib.events.VisibilityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.hide.HiddenObjectFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.VisibilityContext;

public abstract class HiddenObjectFacetAbstract extends FacetAbstract implements HiddenObjectFacet {

    public static Class<? extends Facet> type() {
        return HiddenObjectFacet.class;
    }

    public HiddenObjectFacetAbstract(final FacetHolder holder) {
        super(type(), holder, Derivation.NOT_DERIVED);
    }

    @Override
    public String hides(final VisibilityContext<? extends VisibilityEvent> ic) {
        if (!(ic instanceof VisibilityContext)) {
            return null;
        }
        final ObjectAdapter toValidate = ic.getTarget();
        return toValidate != null ? hiddenReason(toValidate) : null;
    }

    protected abstract String hiddenReason(ObjectAdapter toHide);

}
