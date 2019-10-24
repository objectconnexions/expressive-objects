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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.integer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.TextEntryParseException;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderAndFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public abstract class IntValueSemanticsProviderAbstract extends ValueSemanticsProviderAndFacetAbstract<Integer> implements IntegerValueFacet {

    public static Class<? extends Facet> type() {
        return IntegerValueFacet.class;
    }

    private static final Integer DEFAULT_VALUE = Integer.valueOf(0);
    private static final int TYPICAL_LENGTH = 9;

    private final NumberFormat format;

    public IntValueSemanticsProviderAbstract(final FacetHolder holder, final Class<Integer> adaptedClass, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(type(), holder, adaptedClass, TYPICAL_LENGTH, Immutability.IMMUTABLE, EqualByContent.HONOURED, DEFAULT_VALUE, configuration, context);
        format = determineNumberFormat("value.format.int");
    }

    // //////////////////////////////////////////////////////////////////
    // Parser
    // //////////////////////////////////////////////////////////////////

    @Override
    protected Integer doParse(final Object context, final String entry) {
        try {
            return Integer.valueOf(format.parse(entry).intValue());
        } catch (final ParseException e) {
            throw new TextEntryParseException("Not an whole number " + entry, e);
        }
    }

    @Override
    public String titleString(final Object value, final Localization localization) {
        return titleString(format, value);
    }

    @Override
    public String titleStringWithMask(final Object value, final String usingMask) {
        return titleString(new DecimalFormat(usingMask), value);
    }

    // //////////////////////////////////////////////////////////////////
    // EncoderDecoder
    // //////////////////////////////////////////////////////////////////

    @Override
    protected String doEncode(final Object object) {
        return object.toString();
    }

    @Override
    protected Integer doRestore(final String data) {
        return new Integer(data);
    }

    // //////////////////////////////////////////////////////////////////
    // IntegerValueFacet
    // //////////////////////////////////////////////////////////////////

    @Override
    public Integer integerValue(final ObjectAdapter object) {
        return (Integer) (object == null ? null : object.getObject());
    }

    @Override
    public ObjectAdapter createValue(final Integer value) {
        return value == null ? null : getAdapterManager().adapterFor(value);
    }

    // /////// toString ///////

    @Override
    public String toString() {
        return "IntegerValueSemanticsProvider: " + format;
    }

}
