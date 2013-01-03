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

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.MutableProposedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionResultSet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.TypedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.maxlen.MaxLengthFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.multiline.MultiLineFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typicallen.TypicalLengthFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.InteractionUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Instance;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionParameter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneActionParameter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ParseableEntryActionParameter;

public class ObjectActionParameterParseable extends ObjectActionParameterAbstract implements ParseableEntryActionParameter {

    public ObjectActionParameterParseable(final int index, final ObjectActionImpl action, final TypedHolder peer) {
        super(index, action, peer);
    }

    @Override
    public int getNoLines() {
        final MultiLineFacet facet = getFacet(MultiLineFacet.class);
        return facet.numberOfLines();
    }

    @Override
    public boolean canWrap() {
        final MultiLineFacet facet = getFacet(MultiLineFacet.class);
        return !facet.preventWrapping();
    }

    @Override
    public int getMaximumLength() {
        final MaxLengthFacet facet = getFacet(MaxLengthFacet.class);
        return facet.value();
    }

    @Override
    public int getTypicalLineLength() {
        final TypicalLengthFacet facet = getFacet(TypicalLengthFacet.class);
        return facet.value();
    }

    /**
     * Invoked when tab away, disables the OK button.
     * 
     * <p>
     * Assumed to be invoked {@link InteractionInvocationMethod#BY_USER by user}.
     */
    @Override
    public String isValid(final ObjectAdapter adapter, final Object proposedValue, final Localization localization) {

        if (!(proposedValue instanceof String)) {
            return null;
        }
        final String proposedString = (String) proposedValue;

        final ObjectActionParameter objectActionParameter = getAction().getParameters().get(getNumber());
        if (!(objectActionParameter instanceof ParseableEntryActionParameter)) {
            return null;
        }
        final ParseableEntryActionParameter parameter = (ParseableEntryActionParameter) objectActionParameter;

        final ObjectSpecification parameterSpecification = parameter.getSpecification();
        final ParseableFacet p = parameterSpecification.getFacet(ParseableFacet.class);
        final ObjectAdapter newValue = p.parseTextEntry(null, proposedString, localization);

        final ValidityContext<?> ic = parameter.createProposedArgumentInteractionContext(getAuthenticationSession(), InteractionInvocationMethod.BY_USER, adapter, arguments(newValue), getNumber());

        final InteractionResultSet buf = new InteractionResultSet();
        InteractionUtils.isValidResultSet(parameter, ic, buf);
        if (buf.isVetoed()) {
            return buf.getInteractionResult().getReason();
        }
        return null;

    }

    /**
     * TODO: this is not ideal, because we can only populate the array for
     * single argument, rather than the entire argument set. Instead, we ought
     * to do this in two passes, one to build up the argument set as a single
     * unit, and then validate each in turn.
     * 
     * @param proposedValue
     * @return
     */
    private ObjectAdapter[] arguments(final ObjectAdapter proposedValue) {
        final int parameterCount = getAction().getParameterCount();
        final ObjectAdapter[] arguments = new ObjectAdapter[parameterCount];
        arguments[getNumber()] = proposedValue;
        return arguments;
    }

    // /////////////////////////////////////////////////////////////
    // getInstance
    // /////////////////////////////////////////////////////////////

    @Override
    public Instance getInstance(final ObjectAdapter adapter) {
        final OneToOneActionParameter specification = this;
        return adapter.getInstance(specification);
    }

    // //////////////////////////////////////////////////////////////////////
    // get, set
    // //////////////////////////////////////////////////////////////////////

    /**
     * Gets the proposed value of the {@link Instance} (downcast as a
     * {@link MutableProposed}, wrapping the proposed value into a
     * {@link ObjectAdapter}.
     */
    @Override
    public ObjectAdapter get(final ObjectAdapter owner) {
        final MutableProposedHolder proposedHolder = getProposedHolder(owner);
        final Object proposed = proposedHolder.getProposed();
        return getAdapterMap().adapterFor(proposed);
    }

    /**
     * Sets the proposed value of the {@link Instance} (downcast as a
     * {@link MutableProposed}, unwrapped the proposed value from a
     * {@link ObjectAdapter}.
     */
    public void set(final ObjectAdapter owner, final ObjectAdapter newValue) {
        final MutableProposedHolder proposedHolder = getProposedHolder(owner);
        final Object newValuePojo = newValue.getObject();
        proposedHolder.setProposed(newValuePojo);
    }

    private MutableProposedHolder getProposedHolder(final ObjectAdapter owner) {
        final Instance instance = getInstance(owner);
        if (!(instance instanceof MutableProposedHolder)) {
            throw new IllegalArgumentException("Instance should implement MutableProposedHolder");
        }
        final MutableProposedHolder proposedHolder = (MutableProposedHolder) instance;
        return proposedHolder;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.ACTION_PARAMETER;
    }

}
