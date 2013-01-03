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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugString;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionResult;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.accessor.PropertyOrCollectionAccessorFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionAddToFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionClearFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionRemoveFromFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.CollectionAddToContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.CollectionRemoveFromContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.CollectionUsabilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.CollectionVisibilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.InteractionUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.UsabilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.VisibilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Instance;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectMemberContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;

public class OneToManyAssociationImpl extends ObjectAssociationAbstract implements OneToManyAssociation {

    public OneToManyAssociationImpl(final FacetedMethod facetedMethod, final ObjectMemberContext objectMemberContext) {
        super(facetedMethod, FeatureType.COLLECTION, getSpecification(objectMemberContext.getSpecificationLookup(), facetedMethod.getType()), objectMemberContext);
    }

    @Override
    public CollectionSemantics getCollectionSemantics() {
        final Class<?> underlyingClass = getSpecification().getCorrespondingClass();
        return getCollectionTypeRegistry().semanticsOf(underlyingClass);
    }

    // /////////////////////////////////////////////////////////////
    // Hidden (or visible)
    // /////////////////////////////////////////////////////////////

    @Override
    public VisibilityContext<?> createVisibleInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter ownerAdapter, Where where) {
        return new CollectionVisibilityContext(getDeploymentCategory(), session, invocationMethod, ownerAdapter, getIdentifier(), where);
    }

    // /////////////////////////////////////////////////////////////
    // Disabled (or enabled)
    // /////////////////////////////////////////////////////////////

    @Override
    public UsabilityContext<?> createUsableInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter ownerAdapter, Where where) {
        return new CollectionUsabilityContext(getDeploymentCategory(), session, invocationMethod, ownerAdapter, getIdentifier(), where);
    }

    // /////////////////////////////////////////////////////////////
    // Validate Add
    // /////////////////////////////////////////////////////////////

    @Override
    public ValidityContext<?> createValidateAddInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter ownerAdapter, final ObjectAdapter proposedToAddAdapter) {
        return new CollectionAddToContext(getDeploymentCategory(), session, invocationMethod, ownerAdapter, getIdentifier(), proposedToAddAdapter);
    }

    /**
     * TODO: currently this method is hard-coded to assume all interactions are
     * initiated {@link InteractionInvocationMethod#BY_USER by user}.
     */
    @Override
    public Consent isValidToAdd(final ObjectAdapter ownerAdapter, final ObjectAdapter proposedToAddAdapter) {
        return isValidToAddResult(ownerAdapter, proposedToAddAdapter).createConsent();
    }

    private InteractionResult isValidToAddResult(final ObjectAdapter ownerAdapter, final ObjectAdapter proposedToAddAdapter) {
        final ValidityContext<?> validityContext = createValidateAddInteractionContext(getAuthenticationSession(), InteractionInvocationMethod.BY_USER, ownerAdapter, proposedToAddAdapter);
        return InteractionUtils.isValidResult(this, validityContext);
    }

    // /////////////////////////////////////////////////////////////
    // Validate Remove
    // /////////////////////////////////////////////////////////////

    @Override
    public ValidityContext<?> createValidateRemoveInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter ownerAdapter, final ObjectAdapter proposedToRemoveAdapter) {
        return new CollectionRemoveFromContext(getDeploymentCategory(), session, invocationMethod, ownerAdapter, getIdentifier(), proposedToRemoveAdapter);
    }

    /**
     * TODO: currently this method is hard-coded to assume all interactions are
     * initiated {@link InteractionInvocationMethod#BY_USER by user}.
     */
    @Override
    public Consent isValidToRemove(final ObjectAdapter ownerAdapter, final ObjectAdapter proposedToRemoveAdapter) {
        return isValidToRemoveResult(ownerAdapter, proposedToRemoveAdapter).createConsent();
    }

    private InteractionResult isValidToRemoveResult(final ObjectAdapter ownerAdapter, final ObjectAdapter proposedToRemoveAdapter) {
        final ValidityContext<?> validityContext = createValidateRemoveInteractionContext(getAuthenticationSession(), InteractionInvocationMethod.BY_USER, ownerAdapter, proposedToRemoveAdapter);
        return InteractionUtils.isValidResult(this, validityContext);
    }

    private boolean readWrite() {
        return !isNotPersisted();
    }

    // /////////////////////////////////////////////////////////////
    // get, isEmpty, add, clear
    // /////////////////////////////////////////////////////////////

    @Override
    public ObjectAdapter get(final ObjectAdapter ownerAdapter) {

        final PropertyOrCollectionAccessorFacet accessor = getFacet(PropertyOrCollectionAccessorFacet.class);
        final Object collection = accessor.getProperty(ownerAdapter);
        if (collection == null) {
            return null;
        }
        return getAdapterManager().adapterFor(collection, ownerAdapter, this);
    }

    @Override
    public boolean isEmpty(final ObjectAdapter parentAdapter) {
        // REVIEW should we be able to determine if a collection is empty
        // without loading it?
        final ObjectAdapter collection = get(parentAdapter);
        final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(collection);
        return facet.size(collection) == 0;
    }

    // /////////////////////////////////////////////////////////////
    // add, clear
    // /////////////////////////////////////////////////////////////

    @Override
    public void addElement(final ObjectAdapter ownerAdapter, final ObjectAdapter referencedAdapter) {
        if (referencedAdapter == null) {
            throw new IllegalArgumentException("Can't use null to add an item to a collection");
        }
        if (readWrite()) {
            if (ownerAdapter.representsPersistent() && referencedAdapter.isTransient()) {
                throw new ExpressiveObjectsException("can't set a reference to a transient object from a persistent one: " + ownerAdapter.titleString() + " (persistent) -> " + referencedAdapter.titleString() + " (transient)");
            }
            final CollectionAddToFacet facet = getFacet(CollectionAddToFacet.class);
            facet.add(ownerAdapter, referencedAdapter);
        }
    }

    @Override
    public void removeElement(final ObjectAdapter ownerAdapter, final ObjectAdapter referencedAdapter) {
        if (referencedAdapter == null) {
            throw new IllegalArgumentException("element should not be null");
        }
        if (readWrite()) {
            final CollectionRemoveFromFacet facet = getFacet(CollectionRemoveFromFacet.class);
            facet.remove(ownerAdapter, referencedAdapter);
        }
    }

    public void removeAllAssociations(final ObjectAdapter ownerAdapter) {
        final CollectionClearFacet facet = getFacet(CollectionClearFacet.class);
        facet.clear(ownerAdapter);
    }

    @Override
    public void clearCollection(final ObjectAdapter ownerAdapter) {
        if (readWrite()) {
            final CollectionClearFacet facet = getFacet(CollectionClearFacet.class);
            facet.clear(ownerAdapter);
        }
    }

    // /////////////////////////////////////////////////////////////
    // defaults
    // /////////////////////////////////////////////////////////////

    @Override
    public ObjectAdapter getDefault(final ObjectAdapter ownerAdapter) {
        return null;
    }

    @Override
    public void toDefault(final ObjectAdapter ownerAdapter) {
    }

    // /////////////////////////////////////////////////////////////
    // options (choices)
    // /////////////////////////////////////////////////////////////

    @Override
    public ObjectAdapter[] getChoices(final ObjectAdapter ownerAdapter) {
        return new ObjectAdapter[0];
    }

    @Override
    public boolean hasChoices() {
        return false;
    }

    // /////////////////////////////////////////////////////////////
    // getInstance
    // /////////////////////////////////////////////////////////////

    @Override
    public Instance getInstance(final ObjectAdapter adapter) {
        final OneToManyAssociation specification = this;
        return adapter.getInstance(specification);
    }

    // /////////////////////////////////////////////////////////////
    // debug, toString
    // /////////////////////////////////////////////////////////////

    @Override
    public String debugData() {
        final DebugString debugString = new DebugString();
        debugString.indent();
        debugString.indent();
        getFacetedMethod().debugData(debugString);
        return debugString.toString();
    }

    @Override
    public String toString() {
        final ToString str = new ToString(this);
        str.append(super.toString());
        str.append(",");
        str.append("persisted", !isNotPersisted());
        str.append("type", getSpecification() == null ? "unknown" : getSpecification().getShortIdentifier());
        return str.toString();
    }

}
