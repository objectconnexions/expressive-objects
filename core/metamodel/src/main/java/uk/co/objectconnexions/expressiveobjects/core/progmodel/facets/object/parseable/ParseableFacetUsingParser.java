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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable;

import java.util.IllegalFormatException;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.ParsingException;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.util.AdapterUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionResultSet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.TextEntryParseException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.value.ValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.InteractionUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ObjectValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ParseValueContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;

public class ParseableFacetUsingParser extends FacetAbstract implements ParseableFacet {

    private final Parser<?> parser;
    private final DeploymentCategory deploymentCategory;
    private final AuthenticationSessionProvider authenticationSessionProvider;
    private final ServicesInjector dependencyInjector;
    private final AdapterManager adapterManager;

    public ParseableFacetUsingParser(
            final Parser<?> parser, final FacetHolder holder, 
            final DeploymentCategory deploymentCategory, final AuthenticationSessionProvider authenticationSessionProvider, final ServicesInjector dependencyInjector, final AdapterManager adapterManager) {
        super(ParseableFacet.class, holder, Derivation.NOT_DERIVED);
        this.parser = parser;
        this.deploymentCategory = deploymentCategory;
        this.authenticationSessionProvider = authenticationSessionProvider;
        this.dependencyInjector = dependencyInjector;
        this.adapterManager = adapterManager;
    }

    @Override
    protected String toStringValues() {
        dependencyInjector.injectServicesInto(parser);
        return parser.toString();
    }

    @Override
    public ObjectAdapter parseTextEntry(final ObjectAdapter contextAdapter, final String entry, Localization localization) {
        if (entry == null) {
            throw new IllegalArgumentException("An entry must be provided");
        }

        // check string is valid
        // (eg pick up any @RegEx on value type)
        if (getFacetHolder().containsFacet(ValueFacet.class)) {
            final ObjectAdapter entryAdapter = getAdapterManager().adapterFor(entry);
            final ParseValueContext parseValueContext = new ParseValueContext(deploymentCategory, getAuthenticationSessionProvider().getAuthenticationSession(), InteractionInvocationMethod.BY_USER, contextAdapter, getIdentified().getIdentifier(), entryAdapter);
            validate(parseValueContext);
        }

        final Object context = AdapterUtils.unwrap(contextAdapter);

        getDependencyInjector().injectServicesInto(parser);

        try {
            final Object parsed = parser.parseTextEntry(context, entry, localization);
            if (parsed == null) {
                return null;
            }

            // check resultant object is also valid
            // (eg pick up any validate() methods on it)
            final ObjectAdapter adapter = getAdapterManager().adapterFor(parsed);
            final ObjectSpecification specification = adapter.getSpecification();
            final ObjectValidityContext validateContext = specification.createValidityInteractionContext(deploymentCategory, getAuthenticationSessionProvider().getAuthenticationSession(), InteractionInvocationMethod.BY_USER, adapter);
            validate(validateContext);

            return adapter;
        } catch (final NumberFormatException e) {
            throw new TextEntryParseException(e.getMessage(), e);
        } catch (final IllegalFormatException e) {
            throw new TextEntryParseException(e.getMessage(), e);
        } catch (final ParsingException e) {
            throw new TextEntryParseException(e.getMessage(), e);
        }
    }

    private void validate(final ValidityContext<?> validityContext) {
        final InteractionResultSet resultSet = new InteractionResultSet();
        InteractionUtils.isValidResultSet(getFacetHolder(), validityContext, resultSet);
        if (resultSet.isVetoed()) {
            throw new IllegalArgumentException(resultSet.getInteractionResult().getReason());
        }
    }

    /**
     * TODO: need to fix genericity of using Parser<?>, for now suppressing
     * warnings.
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String parseableTitle(final ObjectAdapter contextAdapter) {
        final Object pojo = AdapterUtils.unwrap(contextAdapter);

        getDependencyInjector().injectServicesInto(parser);
        return ((Parser)parser).parseableTitleOf(pojo);
    }

    // /////////////////////////////////////////////////////////
    // Dependencies (from constructor)
    // /////////////////////////////////////////////////////////

    /**
     * @return the dependencyInjector
     */
    public ServicesInjector getDependencyInjector() {
        return dependencyInjector;
    }

    /**
     * @return the authenticationSessionProvider
     */
    public AuthenticationSessionProvider getAuthenticationSessionProvider() {
        return authenticationSessionProvider;
    }

    /**
     * @return the adapterManager
     */
    public AdapterManager getAdapterManager() {
        return adapterManager;
    }
}
