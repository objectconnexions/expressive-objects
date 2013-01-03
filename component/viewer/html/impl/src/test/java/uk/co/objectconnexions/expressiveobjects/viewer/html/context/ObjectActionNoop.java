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

package uk.co.objectconnexions.expressiveobjects.viewer.html.context;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.ActionSemantics;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Allow;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MultiTypedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ActionInvocationContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.UsabilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.VisibilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Instance;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionParameter;

/**
 * Has no functionality but makes it easier to write tests that require an
 * instance of an {@link Identifier}.
 * 
 * <p>
 * Was previously DummyAction, in the web viewer project. Only used by tests
 * there.
 */
public class ObjectActionNoop implements ObjectAction {

    public boolean[] canParametersWrap() {
        return null;
    }

    @Override
    public String debugData() {
        return null;
    }

    @Override
    public ObjectAdapter execute(final ObjectAdapter target, final ObjectAdapter[] parameters) {
        return null;
    }

    @Override
    public List<ObjectAction> getActions() {
        return null;
    }

    @Override
    public ObjectAdapter[] getDefaults(final ObjectAdapter target) {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean containsFacet(final Class<? extends Facet> facetType) {
        return false;
    }

    @Override
    public boolean containsDoOpFacet(final Class<? extends Facet> facetType) {
        return false;
    }

    @Override
    public <T extends Facet> T getFacet(final Class<T> cls) {
        return null;
    }

    @Override
    public Class<? extends Facet>[] getFacetTypes() {
        return new Class[0];
    }

    @Override
    public List<Facet> getFacets(final Filter<Facet> filter) {
        return null;
    }

    @Override
    public void addFacet(final Facet facet) {
    }

    @Override
    public void addFacet(final MultiTypedFacet facet) {
    }

    @Override
    public void removeFacet(final Facet facet) {
    }

    @Override
    public void removeFacet(final Class<? extends Facet> facetType) {
    }

    @Override
    public Identifier getIdentifier() {
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ObjectSpecification getOnType() {
        return null;
    }

    @Override
    public ObjectAdapter[][] getChoices(final ObjectAdapter target) {
        return null;
    }

    @Override
    public int getParameterCount() {
        return 0;
    }

    @Override
    public List<ObjectActionParameter> getParameters() {
        return null;
    }

    @Override
    public List<ObjectActionParameter> getParameters(final Filter<ObjectActionParameter> filter) {
        return null;
    }

    @Override
    public ObjectActionParameter getParameterById(final String paramId) {
        return null;
    }

    @Override
    public ObjectActionParameter getParameterByName(final String paramName) {
        return null;
    }

    @Override
    public ObjectSpecification getReturnType() {
        return null;
    }

    @Override
    public ActionType getType() {
        return null;
    }

    @Override
    public boolean hasReturn() {
        return false;
    }

    @Override
    public boolean isContributed() {
        return false;
    }

    @Override
    public boolean promptForParameters(final ObjectAdapter target) {
        return false;
    }

    @Override
    public VisibilityContext<?> createVisibleInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter targetObjectAdapter, Where where) {
        return null;
    }

    @Override
    public boolean isAlwaysHidden() {
        return false;
    }

    @Override
    public Consent isVisible(final AuthenticationSession session, final ObjectAdapter target, Where where) {
        return Allow.DEFAULT;
    }

    @Override
    public Consent isUsable(final AuthenticationSession session, final ObjectAdapter target, Where where) {
        return Allow.DEFAULT;
    }

    @Override
    public Consent isProposedArgumentSetValid(final ObjectAdapter object, final ObjectAdapter[] parameters) {
        return Allow.DEFAULT;
    }

    @Override
    public ObjectAdapter realTarget(final ObjectAdapter target) {
        return target;
    }

    @Override
    public ObjectSpecification getSpecification() {
        return null;
    }

    @Override
    public UsabilityContext<?> createUsableInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter target, Where where) {
        return null;
    }

    @Override
    public ActionInvocationContext createActionInvocationInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter object, final ObjectAdapter[] candidateArguments) {
        return null;
    }

    // /////////////////////////////////////////////////////////////
    // isAction, isAssociation
    // /////////////////////////////////////////////////////////////

    @Override
    public boolean isAction() {
        return true;
    }

    @Override
    public boolean isPropertyOrCollection() {
        return false;
    }

    @Override
    public boolean isOneToManyAssociation() {
        return false;
    }

    @Override
    public boolean isOneToOneAssociation() {
        return false;
    }

    // /////////////////////////////////////////////////////////////
    // getInstance
    // /////////////////////////////////////////////////////////////

    @Override
    public Instance getInstance(final ObjectAdapter adapter) {
        final ObjectAction specification = this;
        return adapter.getInstance(specification);
    }

    @Override
    public List<ObjectSpecification> getParameterTypes() {
        return null;
    }

    public RuntimeContext getRuntimeContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectFeature#getFeatureType
     * ()
     */
    @Override
    public FeatureType getFeatureType() {
        return FeatureType.ACTION;
    }

    @Override
    public ActionSemantics.Of getSemantics() {
        return ActionSemantics.Of.NON_IDEMPOTENT;
    }

}
