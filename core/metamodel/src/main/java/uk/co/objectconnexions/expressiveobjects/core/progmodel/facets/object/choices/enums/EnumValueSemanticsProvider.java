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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.choices.enums;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.TextEntryParseException;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderAndFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public class EnumValueSemanticsProvider<T extends Enum<T>> extends ValueSemanticsProviderAndFacetAbstract<T> implements EnumFacet {

    private static final int TYPICAL_LENGTH = 8;

    private static <T> T defaultFor(final Class<T> adaptedClass) {
        return adaptedClass.getEnumConstants()[0];
    }

    private static Class<? extends Facet> type() {
        return EnumFacet.class;
    }
    
    /**
     * Required because {@link Parser} and {@link EncoderDecoder}.
     */
    public EnumValueSemanticsProvider() {
        this(null, null, null, null);
    }

    public EnumValueSemanticsProvider(final FacetHolder holder, final Class<T> adaptedClass, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(type(), holder, adaptedClass, TYPICAL_LENGTH, Immutability.IMMUTABLE, EqualByContent.HONOURED, defaultFor(adaptedClass), configuration, context);
    }

    @Override
    protected T doParse(final Object context, final String entry) {
        final T[] enumConstants = getAdaptedClass().getEnumConstants();
        for (final T enumConstant : enumConstants) {
            if (enumConstant.toString().equals(entry)) {
                return enumConstant;
            }
        }
        throw new TextEntryParseException("Unknown enum constant '" + entry + "'");
    }

    @Override
    protected String doEncode(final Object object) {
        return ((Enum) object).name();
    }

    @Override
    protected T doRestore(final String data) {
        final T[] enumConstants = getAdaptedClass().getEnumConstants();
        for (final T enumConstant : enumConstants) {
            if (enumConstant.name().equals(data)) {
                return enumConstant;
            }
        }
        throw new TextEntryParseException("Unknown enum constant '" + data + "'");
    }

    @Override
    protected String titleString(final Object object, final Localization localization) {
        return object.toString();
    }

    @Override
    public String titleStringWithMask(final Object value, final String usingMask) {
        return titleString(value, null);
    }

}
