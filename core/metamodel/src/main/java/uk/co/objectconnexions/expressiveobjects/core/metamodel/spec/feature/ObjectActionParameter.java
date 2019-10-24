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

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ActionArgumentContext;

/**
 * Analogous to {@link ObjectAssociation}.
 */
public interface ObjectActionParameter extends ObjectFeature, CurrentHolder {

    /**
     * If true then can cast to a {@link OneToOneActionParameter}.
     * 
     * <p>
     * Either this or {@link #isCollection()} will be true.
     * 
     * <p>
     * Design note: modelled after {@link ObjectAssociation#isNotCollection()}
     */
    boolean isObject();

    /**
     * Only for symmetry with {@link ObjectAssociation}, however since the NOF
     * does not support collections as actions all implementations should return
     * <tt>false</tt>.
     */
    boolean isCollection();

    /**
     * Owning {@link ObjectAction}.
     */
    ObjectAction getAction();

    /**
     * Returns a flag indicating if it can be left unset when the action can be
     * invoked.
     */
    boolean isOptional();

    /**
     * Returns the 0-based index to this parameter.
     */
    int getNumber();

    /**
     * Returns the name of this parameter.
     * 
     * <p>
     * Because Java's reflection API does not allow us to access the code name
     * of the parameter, we have to do figure out the name of the parameter
     * ourselves:
     * <ul>
     * <li>If there is a {@link NamedFacet} associated with this parameter then
     * we infer a name from this, eg "First Name" becomes "firstName".
     * <li>Otherwise we use the type, eg "string".
     * <li>If there is more than one parameter of the same type, then we use a
     * numeric suffix (eg "string1", "string2"). Wrappers and primitives are
     * considered to be the same type.
     * </ul>
     */
    @Override
    String getName();

    ActionArgumentContext createProposedArgumentInteractionContext(AuthenticationSession session, InteractionInvocationMethod invocationMethod, ObjectAdapter targetObject, ObjectAdapter[] args, int position);

    /**
     * Whether proposed value for this parameter is valid.
     * 
     * @param adapter
     * @param proposedValue
     * @return
     */
    String isValid(ObjectAdapter adapter, Object proposedValue, Localization localization);

    ObjectAdapter[] getChoices(ObjectAdapter adapter);

    ObjectAdapter getDefault(ObjectAdapter adapter);

}
