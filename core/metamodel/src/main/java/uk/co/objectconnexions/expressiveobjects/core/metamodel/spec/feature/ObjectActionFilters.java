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
package uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetFilters;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.DisablingInteractionAdvisor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.HidingInteractionAdvisor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidatingInteractionAdvisor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;

public class ObjectActionFilters {

    public static Filter<ObjectAction> dynamicallyVisible(final AuthenticationSession session, final ObjectAdapter target, final Where where) {
        return new Filter<ObjectAction>() {
            @Override
            public boolean accept(final ObjectAction objectAction) {
                final Consent visible = objectAction.isVisible(session, target, where);
                return visible.isAllowed();
            }
        };
    }

    public static Filter<ObjectAction> withId(final String actionId) {
        return new Filter<ObjectAction>(){
            @Override
            public boolean accept(ObjectAction objectAction) {
                return objectAction.getId().equals(actionId);
            }
        };
    }

    public static Filter<ObjectAction> withNoBusinessRules() {
        return new Filter<ObjectAction>(){
            @Override
            public boolean accept(final ObjectAction objectAction) {
                final List<Facet> hidingFacets = objectAction.getFacets(FacetFilters.isA(HidingInteractionAdvisor.class));
                final List<Facet> disablingFacets = objectAction.getFacets(FacetFilters.isA(DisablingInteractionAdvisor.class));
                final List<Facet> validatingFacets = objectAction.getFacets(FacetFilters.isA(ValidatingInteractionAdvisor.class));
                return hidingFacets.isEmpty() && disablingFacets.isEmpty() && validatingFacets.isEmpty();
            }};
    }

    public static Filter<ObjectAction> contributedAnd1ParamAndVoid() {
        return new Filter<ObjectAction>(){
            @Override
            public boolean accept(final ObjectAction objectAction) {
                boolean contributed = objectAction.isContributed();
                boolean has1Param = objectAction.getParameterCount() == 1;
                boolean hasReturn = objectAction.hasReturn();
                return contributed && has1Param && !hasReturn;
            }
        };
    }

    public static Filter<ObjectAction> filterOfType(final ActionType type) {
        return new Filter<ObjectAction>(){
            @Override
            public boolean accept(ObjectAction oa) {
                return oa.getType() == type;
            }
        };
    }
}
