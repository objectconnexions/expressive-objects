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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.DefaultsProvider;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.ValueSemanticsProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ClassUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MultipleValueFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.value.ValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.defaults.DefaultedFacetUsingDefaultsProvider;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodeable.EncodableFacetUsingEncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable.ParseableFacetUsingParser;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.title.TitleFacetUsingParser;

public abstract class ValueFacetAbstract extends MultipleValueFacetAbstract implements ValueFacet {

    public static Class<? extends Facet> type() {
        return ValueFacet.class;
    }

    private static ValueSemanticsProvider<?> newValueSemanticsProviderOrNull(final Class<?> semanticsProviderClass, final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        if (semanticsProviderClass == null) {
            return null;
        }
        return (ValueSemanticsProvider<?>) ClassUtil.newInstance(semanticsProviderClass, new Class<?>[] { FacetHolder.class, ExpressiveObjectsConfiguration.class, ValueSemanticsProviderContext.class }, new Object[] { holder, configuration, context });
    }

    // to look after the facets (since MultiTyped)
    private final FacetHolder facetHolder = new FacetHolderImpl();

    private final ValueSemanticsProvider<?> semanticsProvider;

    private final ValueSemanticsProviderContext context;

    public enum AddFacetsIfInvalidStrategy {
        DO_ADD(true), DONT_ADD(false);
        private boolean addFacetsIfInvalid;

        private AddFacetsIfInvalidStrategy(final boolean addFacetsIfInvalid) {
            this.addFacetsIfInvalid = addFacetsIfInvalid;
        }

        public boolean shouldAddFacetsIfInvalid() {
            return addFacetsIfInvalid;
        }
    }

    public ValueFacetAbstract(final Class<?> semanticsProviderClass, final AddFacetsIfInvalidStrategy addFacetsIfInvalid, final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        this(newValueSemanticsProviderOrNull(semanticsProviderClass, holder, configuration, context), addFacetsIfInvalid, holder, context);
    }

    public ValueFacetAbstract(final ValueSemanticsProvider<?> semanticsProvider, final AddFacetsIfInvalidStrategy addFacetsIfInvalid, final FacetHolder holder, final ValueSemanticsProviderContext context) {
        super(type(), holder);

        this.semanticsProvider = semanticsProvider;
        this.context = context;

        // note: we can't use the runtimeContext to inject dependencies into the
        // semanticsProvider,
        // because there won't be any PersistenceSession when initially building
        // the metamodel.
        // so, we defer until we use the parser.

        if (!isValid() && !addFacetsIfInvalid.shouldAddFacetsIfInvalid()) {
            return;
        }

        // we now figure add all the facets supported. Note that we do not use
        // FacetUtil.addFacet,
        // because we need to add them explicitly to our delegate facetholder
        // but have the
        // facets themselves reference this value's holder.

        facetHolder.addFacet((Facet) this); // add just ValueFacet.class
                                            // initially.

        // we used to add aggregated here, but this was wrong.
        // An immutable value is not aggregated, it is shared.

        // ImmutableFacet, if appropriate
        final boolean immutable = semanticsProvider != null ? semanticsProvider.isImmutable() : true;
        if (immutable) {
            facetHolder.addFacet(new ImmutableFacetViaValueSemantics(holder));
        }

        // EqualByContentFacet, if appropriate
        final boolean equalByContent = semanticsProvider != null ? semanticsProvider.isEqualByContent() : true;
        if (equalByContent) {
            facetHolder.addFacet(new EqualByContentFacetViaValueSemantics(holder));
        }

        if (semanticsProvider != null) {

            // install the EncodeableFacet if we've been given an EncoderDecoder
            final EncoderDecoder<?> encoderDecoder = semanticsProvider.getEncoderDecoder();
            if (encoderDecoder != null) {
                facetHolder.addFacet(new EncodableFacetUsingEncoderDecoder(encoderDecoder, holder, getAdapterMap(), getDependencyInjector()));
            }

            // install the ParseableFacet and other facets if we've been given a
            // Parser
            final Parser<?> parser = semanticsProvider.getParser();
            if (parser != null) {
                facetHolder.addFacet(new ParseableFacetUsingParser(parser, holder, getDeploymentCategory(context), getAuthenticationSessionProvider(), getDependencyInjector(), getAdapterMap()));
                facetHolder.addFacet(new TitleFacetUsingParser(parser, holder, getDependencyInjector()));
                facetHolder.addFacet(new TypicalLengthFacetUsingParser(parser, holder, getDependencyInjector()));
            }

            // install the DefaultedFacet if we've been given a DefaultsProvider
            final DefaultsProvider<?> defaultsProvider = semanticsProvider.getDefaultsProvider();
            if (defaultsProvider != null) {
                facetHolder.addFacet(new DefaultedFacetUsingDefaultsProvider(defaultsProvider, holder, getDependencyInjector()));
            }
        }
    }

    public boolean isValid() {
        return this.semanticsProvider != null;
    }

    // /////////////////////////////
    // MultiTypedFacet impl
    // /////////////////////////////
    @Override
    public Class<? extends Facet>[] facetTypes() {
        return facetHolder.getFacetTypes();
    }

    @Override
    public <T extends Facet> T getFacet(final Class<T> facetType) {
        return facetHolder.getFacet(facetType);
    }

    // /////////////////////////////////////////
    // Dependencies (from constructor)
    // /////////////////////////////////////////

    protected DeploymentCategory getDeploymentCategory(final ValueSemanticsProviderContext context) {
        return context.getDeploymentCategory();
    }

    public AdapterManager getAdapterMap() {
        return context.getAdapterManager();
    }

    public ServicesInjector getDependencyInjector() {
        return context.getDependencyInjector();
    }

    public AuthenticationSessionProvider getAuthenticationSessionProvider() {
        return context.getAuthenticationSessionProvider();
    }

}
