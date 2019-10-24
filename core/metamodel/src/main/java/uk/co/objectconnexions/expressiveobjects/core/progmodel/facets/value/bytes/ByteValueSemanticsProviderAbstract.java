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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.bytes;

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

public abstract class ByteValueSemanticsProviderAbstract extends ValueSemanticsProviderAndFacetAbstract<Byte> implements ByteValueFacet {

    private static Class<? extends Facet> type() {
        return ByteValueFacet.class;
    }

    private static final Byte DEFAULT_VALUE = Byte.valueOf((byte) 0);
    private static final int TYPICAL_LENGTH = 4;

    private final NumberFormat format;

    public ByteValueSemanticsProviderAbstract(final FacetHolder holder, final Class<Byte> adaptedClass, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(type(), holder, adaptedClass, TYPICAL_LENGTH, Immutability.IMMUTABLE, EqualByContent.HONOURED, DEFAULT_VALUE, configuration, context);
        format = determineNumberFormat("value.format.byte");
    }

    // //////////////////////////////////////////////////////////////////
    // Parser
    // //////////////////////////////////////////////////////////////////

    @Override
    protected Byte doParse(final Object context, final String entry) {
        try {
            return Byte.valueOf(format.parse(entry).byteValue());
        } catch (final ParseException e) {
            throw new TextEntryParseException("Not a number " + entry, e);
        }
    }

    @Override
    public String titleString(final Object value, final Localization localization) {
        return titleString(format, value);
    }

    // //////////////////////////////////////////////////////////////////
    // EncoderDecoder
    // //////////////////////////////////////////////////////////////////

    @Override
    public String doEncode(final Object object) {
        return object.toString();
    }

    @Override
    protected Byte doRestore(final String data) {
        return new Byte(data);
    }

    @Override
    public String titleStringWithMask(final Object value, final String usingMask) {
        return titleString(new DecimalFormat(usingMask), value);
    }

    // //////////////////////////////////////////////////////////////////
    // ByteValueFacet
    // //////////////////////////////////////////////////////////////////

    @Override
    public Byte byteValue(final ObjectAdapter object) {
        return (Byte) object.getObject();
    }

    @Override
    public ObjectAdapter createValue(final Byte value) {
        return getAdapterManager().adapterFor(value);
    }

    // ///// toString ///////

    @Override
    public String toString() {
        return "ByteValueSemanticsProvider: " + format;
    }

}
