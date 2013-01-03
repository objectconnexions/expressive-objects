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

import uk.co.objectconnexions.expressiveobjects.applib.annotation.When;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionContextType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.hide.HiddenFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.AccessContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.InteractionContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.UsabilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.VisibilityContext;

/**
 * Provides reflective access to an action or a field on a domain object.
 */
public interface ObjectMember extends ObjectFeature {

    // /////////////////////////////////////////////////////////////
    // Name, Description, Help (convenience for facets)
    // /////////////////////////////////////////////////////////////

    /**
     * Return the help text for this member - the field or action - to
     * complement the description.
     * 
     * @see #getDescription()
     */
    String getHelp();

    // /////////////////////////////////////////////////////////////
    // Hidden (or visible)
    // /////////////////////////////////////////////////////////////

    /**
     * When the member is always hidden.
     * 
     * <p>
     * Determined as per the {@link HiddenFacet} being present and 
     * {@link HiddenFacet#when()} returning {@link When#ALWAYS}, and
     * {@link HiddenFacet#where()} NOT returning {@link When#NOWHERE}.
     */
    boolean isAlwaysHidden();

    /**
     * Create an {@link InteractionContext} to represent an attempt to view this
     * member (that is, to check if it is visible or not).
     * 
     * <p>
     * Typically it is easier to just call
     * {@link #isVisible(AuthenticationSession, ObjectAdapter, Where)} or
     * {@link #isVisibleResult(AuthenticationSession, ObjectAdapter)}; this is
     * provided as API for symmetry with interactions (such as
     * {@link AccessContext} accesses) have no corresponding vetoing methods.
     */
    VisibilityContext<?> createVisibleInteractionContext(AuthenticationSession session, InteractionInvocationMethod invocationMethod, ObjectAdapter targetObjectAdapter, Where where);

    /**
     * Determines if this member is visible, represented as a {@link Consent}.
     * @param target
     *            may be <tt>null</tt> if just checking for authorization.
     * @param where 
     *            the member is being rendered in the UI
     * @see #isVisibleResult(AuthenticationSession, ObjectAdapter)
     */
    Consent isVisible(AuthenticationSession session, ObjectAdapter target, Where where);

    // /////////////////////////////////////////////////////////////
    // Disabled (or enabled)
    // /////////////////////////////////////////////////////////////

    /**
     * Create an {@link InteractionContext} to represent an attempt to
     * {@link InteractionContextType#MEMBER_USABLE use this member} (that is, to
     * check if it is usable or not).
     * 
     * <p>
     * Typically it is easier to just call
     * {@link #isUsable(AuthenticationSession, ObjectAdapter, Where)} or
     * {@link #isUsableResult(AuthenticationSession, ObjectAdapter)}; this is
     * provided as API for symmetry with interactions (such as
     * {@link AccessContext} accesses) have no corresponding vetoing methods.
     */
    UsabilityContext<?> createUsableInteractionContext(AuthenticationSession session, InteractionInvocationMethod invocationMethod, ObjectAdapter target, Where where);

    /**
     * Determines whether this member is usable, represented as a
     * {@link Consent}.
     * @param target
     *            may be <tt>null</tt> if just checking for authorization.
     * @param where 
     *            the member is being rendered in the UI
     * 
     * @see #isUsableResult(AuthenticationSession, ObjectAdapter)
     */
    Consent isUsable(AuthenticationSession session, ObjectAdapter target, Where where);

    // /////////////////////////////////////////////////////////////
    // isAssociation, isAction
    // /////////////////////////////////////////////////////////////

    /**
     * Whether this member represents a {@link ObjectAssociation}.
     * 
     * <p>
     * If so, can be safely downcast to {@link ObjectAssociation}.
     */
    boolean isPropertyOrCollection();

    /**
     * Whether this member represents a {@link OneToManyAssociation}.
     * 
     * <p>
     * If so, can be safely downcast to {@link OneToManyAssociation}.
     */
    boolean isOneToManyAssociation();

    /**
     * Whether this member represents a {@link OneToOneAssociation}.
     * 
     * <p>
     * If so, can be safely downcast to {@link OneToOneAssociation}.
     */
    boolean isOneToOneAssociation();

    /**
     * Whether this member represents a {@link ObjectAction}.
     * 
     * <p>
     * If so, can be safely downcast to {@link ObjectAction}.
     */
    boolean isAction();

    // /////////////////////////////////////////////////////////////
    // Debugging
    // /////////////////////////////////////////////////////////////

    String debugData();

}
