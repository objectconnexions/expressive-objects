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

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.AccessContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.InteractionContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.PropertyAccessContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;

/**
 * Provides reflective access to a field on a domain object that is used to
 * reference another domain object.
 */
public interface OneToOneAssociation extends ObjectAssociation, OneToOneFeature, MutableCurrentHolder {

    /**
     * Initialise this field in the specified object with the specified
     * reference - this call should only affect the specified object, and not
     * any related objects. It should also not be distributed. This is strictly
     * for re-initialising the object and not specifying an association, which
     * is only done once.
     */
    void initAssociation(ObjectAdapter inObject, ObjectAdapter associate);

    /**
     * Creates an {@link InteractionContext} that represents access to this
     * property.
     */
    public PropertyAccessContext createAccessInteractionContext(AuthenticationSession session, InteractionInvocationMethod interactionMethod, ObjectAdapter targetObjectAdapter);

    /**
     * Creates an {@link InteractionContext} that represents validation of a
     * proposed new value for the property.
     * 
     * <p>
     * Typically it is easier to just call
     * {@link #isAssociationValid(ObjectAdapter, ObjectAdapter)} or
     * {@link #isAssociationValidResult(ObjectAdapter, ObjectAdapter)}; this is
     * provided as API for symmetry with interactions (such as
     * {@link AccessContext} accesses) have no corresponding vetoing methods.
     */
    public ValidityContext<?> createValidateInteractionContext(AuthenticationSession session, InteractionInvocationMethod interactionMethod, ObjectAdapter targetObjectAdapter, ObjectAdapter proposedValue);

    /**
     * Determines if the specified reference is valid for setting this field in
     * the specified object, represented as a {@link Consent}.
     */
    Consent isAssociationValid(ObjectAdapter inObject, ObjectAdapter associate);

    /**
     * Set up the association represented by this field in the specified object
     * with the specified reference - this call sets up the logical state of the
     * object and might affect other objects that share this association (such
     * as back-links or bidirectional association). To initialise a recreated
     * object to this logical state the <code>initAssociation</code> method
     * should be used on each of the objects.
     * 
     * @deprecated - see {@link #set(ObjectAdapter, ObjectAdapter)}
     * @see #initAssociation(ObjectAdapter, ObjectAdapter)
     */
    @Deprecated
    void setAssociation(ObjectAdapter inObject, ObjectAdapter associate);

    /**
     * Clear this reference field (make it <code>null</code>) in the specified
     * object, and remove any association back-link.
     * 
     * @see #setAssociation(ObjectAdapter, ObjectAdapter)
     * @deprecated - see {@link #set(ObjectAdapter, ObjectAdapter)}
     */
    @Deprecated
    void clearAssociation(ObjectAdapter inObject);

}
