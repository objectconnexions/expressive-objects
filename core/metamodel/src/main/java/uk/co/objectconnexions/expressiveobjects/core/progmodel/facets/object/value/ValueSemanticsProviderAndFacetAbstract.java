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

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.DefaultsProvider;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.ValueSemanticsProvider;
import uk.co.objectconnexions.expressiveobjects.applib.clock.Clock;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnexpectedCallException;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnknownTypeException;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.LocaleUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.InvalidEntryException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.defaults.PropertyDefaultFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;

public abstract class ValueSemanticsProviderAndFacetAbstract<T> extends FacetAbstract implements ValueSemanticsProvider<T>, EncoderDecoder<T>, Parser<T>, DefaultsProvider<T> {

    private final Class<T> adaptedClass;
    private final int typicalLength;
    private final boolean immutable;
    private final boolean equalByContent;
    private final T defaultValue;
    
    public enum Immutability {
        IMMUTABLE,
        NOT_IMMUTABLE;

        public static Immutability of(boolean immutable) {
            return immutable? IMMUTABLE: NOT_IMMUTABLE;
        }
    }

    public enum EqualByContent {
        HONOURED,
        NOT_HONOURED;

        public static EqualByContent of(boolean equalByContent) {
            return equalByContent? HONOURED: NOT_HONOURED;
        }
    }

    /**
     * Lazily looked up per {@link #getSpecification()}.
     */
    private ObjectSpecification specification;

    private final ExpressiveObjectsConfiguration configuration;
    private final ValueSemanticsProviderContext context;

    public ValueSemanticsProviderAndFacetAbstract(final Class<? extends Facet> adapterFacetType, final FacetHolder holder, final Class<T> adaptedClass, final int typicalLength, final Immutability immutability, final EqualByContent equalByContent, final T defaultValue, final ExpressiveObjectsConfiguration configuration,
            final ValueSemanticsProviderContext context) {
        super(adapterFacetType, holder, Derivation.NOT_DERIVED);
        this.adaptedClass = adaptedClass;
        this.typicalLength = typicalLength;
        this.immutable = (immutability == Immutability.IMMUTABLE);
        this.equalByContent = (equalByContent == EqualByContent.HONOURED);
        this.defaultValue = defaultValue;

        this.configuration = configuration;
        this.context = context;
    }

    public ObjectSpecification getSpecification() {
        if (specification == null) {
            specification = getSpecificationLookup().loadSpecification(getAdaptedClass());
        }
        return specification;
    }

    /**
     * The underlying class that has been adapted.
     * 
     * <p>
     * Used to determine whether an empty string can be parsed, (for primitive
     * types a non-null entry is required, see {@link #mustHaveEntry()}), and
     * potentially useful for debugging.
     */
    public final Class<T> getAdaptedClass() {
        return adaptedClass;
    }

    /**
     * We don't replace any (none no-op) facets.
     * 
     * <p>
     * For example, if there is already a {@link PropertyDefaultFacet} then we
     * shouldn't replace it.
     */
    @Override
    public boolean alwaysReplace() {
        return false;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ValueSemanticsProvider implementation
    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public EncoderDecoder<T> getEncoderDecoder() {
        return this;
    }

    @Override
    public Parser<T> getParser() {
        return this;
    }

    @Override
    public DefaultsProvider<T> getDefaultsProvider() {
        return this;
    }

    @Override
    public boolean isEqualByContent() {
        return equalByContent;
    }

    @Override
    public boolean isImmutable() {
        return immutable;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Parser implementation
    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public T parseTextEntry(final Object context, final String entry, final Localization localization) {
        if (entry == null) {
            throw new IllegalArgumentException();
        }
        if (entry.trim().equals("")) {
            if (mustHaveEntry()) {
                throw new InvalidEntryException("An entry is required");
            } else {
                return null;
            }
        }
        return doParse(context, entry, localization);
    }

    /**
     * @param context
     *            - the underlying object, or <tt>null</tt>.
     * @param entry
     *            - the proposed new object, as a string representation to be
     *            parsed
     */
    protected T doParse(Object context, String entry) { 
        throw new UnexpectedCallException(); 
    } 

    protected T doParse(Object context, String entry, Localization localization) { 
        return doParse(context, entry); 
    } 

    /**
     * Whether a non-null entry is required, used by parsing.
     * 
     * <p>
     * Adapters for primitives will return <tt>true</tt>.
     */
    private final boolean mustHaveEntry() {
        return adaptedClass.isPrimitive();
    }

    @Override
    public String displayTitleOf(final Object object, final Localization localization) {
        if (object == null) {
            return "";
        }
        return titleString(object, localization);
    }

    @Override
    public String displayTitleOf(final Object object, final String usingMask) {
        if (object == null) {
            return "";
        }
        return titleStringWithMask(object, usingMask);
    }

    /**
     * Defaults to {@link #displayTitleOf(Object, Localization)}.
     */
    @Override
    public String parseableTitleOf(final Object existing) {
        return displayTitleOf(existing, (Localization) null);
    }

    protected String titleString(final Format formatter, final Object object) {
        return object == null ? "" : formatter.format(object);
    }

    /**
     * Return a string representation of aforesaid object.
     */
    protected abstract String titleString(Object object, Localization localization);

    public abstract String titleStringWithMask(final Object value, final String usingMask);

    @Override
    public final int typicalLength() {
        return this.typicalLength;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // DefaultsProvider implementation
    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public T getDefaultValue() {
        return this.defaultValue;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EncoderDecoder implementation
    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public String toEncodedString(final Object object) {
        return doEncode(object);
    }

    @Override
    public T fromEncodedString(final String data) {
        return doRestore(data);
    }

    /**
     * Hook method to perform the actual encoding.
     */
    protected abstract String doEncode(Object object);

    /**
     * Hook method to perform the actual restoring.
     */
    protected abstract T doRestore(String data);

    // ///////////////////////////////////////////////////////////////////////////
    // Helper: Locale handling
    // ///////////////////////////////////////////////////////////////////////////

    protected NumberFormat determineNumberFormat(final String suffix) {
        final String formatRequired = getConfiguration().getString(ConfigurationConstants.ROOT + suffix);
        if (formatRequired != null) {
            return new DecimalFormat(formatRequired);
        } else {
            return NumberFormat.getNumberInstance(findLocale());
        }
    }

    private Locale findLocale() {
        final String localeStr = getConfiguration().getString(ConfigurationConstants.ROOT + "locale");

        final Locale findLocale = LocaleUtils.findLocale(localeStr);
        return findLocale != null ? findLocale : Locale.getDefault();
    }

    // //////////////////////////////////////////////////////////
    // Helper: createAdapter
    // //////////////////////////////////////////////////////////

    protected ObjectAdapter createAdapter(final Class<?> type, final Object object) {
        final ObjectSpecification specification = getSpecificationLookup().loadSpecification(type);
        if (specification.isNotCollection()) {
            return getAdapterManager().adapterFor(object);
        } else {
            throw new UnknownTypeException("not an object, is this a collection?");
        }
    }

    // //////////////////////////////////////////////////////////
    // Dependencies (from constructor)
    // //////////////////////////////////////////////////////////

    protected ExpressiveObjectsConfiguration getConfiguration() {
        return configuration;
    }

    protected ValueSemanticsProviderContext getContext() {
        return context;
    }

    /**
     * From {@link #getContext() context.}
     */
    protected AdapterManager getAdapterManager() {
        return context.getAdapterManager();
    }

    /**
     * From {@link #getContext() context.}
     */
    protected SpecificationLoader getSpecificationLookup() {
        return context.getSpecificationLookup();
    }

    /**
     * From {@link #getContext() context.}
     */
    protected ServicesInjector getDependencyInjector() {
        return context.getDependencyInjector();
    }

    /**
     * From {@link #getContext() context.}
     */
    protected AuthenticationSessionProvider getAuthenticationSessionProvider() {
        return context.getAuthenticationSessionProvider();
    }

    // //////////////////////////////////////////////////////////
    // Dependencies (from singleton)
    // //////////////////////////////////////////////////////////

    protected static Clock getClock() {
        return Clock.getInstance();
    }

}
