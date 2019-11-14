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


package uk.co.objectconnexions.expressiveobjects.metamodel.examples.facets.jsr303;

import java.util.Set;

import javax.validation.InvalidConstraint;
import javax.validation.ValidationProviderFactory;
import javax.xml.validation.Validator;

import uk.co.objectconnexions.expressiveobjects.applib.events.ValidityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ProposedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidatingInteractionAdvisor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;
import uk.co.objectconnexions.expressiveobjects.metamodel.spec.identifier.Identified;


public class Jsr303PropertyValidationFacet extends FacetAbstract implements ValidatingInteractionAdvisor {

    public static Class<? extends Facet> type() {
        return Jsr303PropertyValidationFacet.class;
    }

    private final Identified identifierHolder;
    
    public Jsr303PropertyValidationFacet(final FacetHolder holder) {
        super(type(), holder, false);
        identifierHolder = (holder instanceof Identified) ? (Identified)holder : null;
    }

    @SuppressWarnings("unchecked")
    public String invalidates(final ValidityContext<? extends ValidityEvent> validityContext) {
        final Validator validator = getTargetValidator(validityContext);
        final Object proposed = getProposed(validityContext);
        
        if (validator == null || proposed == null || identifierHolder == null) {
            return null;
        }
        
        final String memberName = identifierHolder.getIdentifier().getMemberName();
        Set<InvalidConstraint<?>> constraints = validator.validateValue(memberName, proposed);
        return asString(memberName, constraints);
    }

    private Validator<?> getTargetValidator(final ValidityContext<? extends ValidityEvent> validityContext) {
        final ObjectAdapter targetNO = validityContext.getTarget();
        final Object targetObject = targetNO.getObject();
        final Class<?> cls = targetObject.getClass();
        return ValidationProviderFactory.createValidator(cls);
    }

    private Object getProposed(final ValidityContext<? extends ValidityEvent> validityContext) {
        if (!(validityContext instanceof ProposedHolder)) {
            return null;
        }
        final ProposedHolder propertyModifyContext = (ProposedHolder) validityContext;
        final ObjectAdapter proposedNO = propertyModifyContext.getProposed();
        if (proposedNO == null) {
            return null;
        }
        return proposedNO.getObject();
    }


    private String asString(final String memberName, final Set<InvalidConstraint<?>> constraints) {
        if (constraints.isEmpty()) {
            return null;
        }
        final StringBuilder buf = new StringBuilder(memberName + " is invalid: ");
        boolean first = true;
        for (final InvalidConstraint<?> constraint : constraints) {
            if (!first) {
                buf.append("; ");
            } else {
                first = false;
            }
            buf.append(constraint.getMessage());
        }
        return buf.toString();
    }

}
