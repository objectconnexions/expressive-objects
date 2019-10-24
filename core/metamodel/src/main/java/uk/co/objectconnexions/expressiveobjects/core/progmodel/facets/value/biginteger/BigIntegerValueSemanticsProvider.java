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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.biginteger;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.TextEntryParseException;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderAndFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public class BigIntegerValueSemanticsProvider extends ValueSemanticsProviderAndFacetAbstract<BigInteger> implements BigIntegerValueFacet {

    private static final int TYPICAL_LENGTH = 10;

    private static Class<? extends Facet> type() {
        return BigIntegerValueFacet.class;
    }

    private static final BigInteger DEFAULT_VALUE = BigInteger.valueOf(0);

    private final NumberFormat format;

    /**
     * Required because implementation of {@link Parser} and
     * {@link EncoderDecoder}.
     */
    public BigIntegerValueSemanticsProvider() {
        this(null, null, null);
    }

    public BigIntegerValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {

        super(type(), holder, BigInteger.class, TYPICAL_LENGTH, Immutability.IMMUTABLE, EqualByContent.HONOURED, DEFAULT_VALUE, configuration, context);
        format = determineNumberFormat("value.format.int");
    }

    // //////////////////////////////////////////////////////////////////
    // Parser
    // //////////////////////////////////////////////////////////////////

    @Override
    protected BigInteger doParse(final Object context, final String entry) {
        try {
            return new BigInteger(entry);
        } catch (final NumberFormatException e) {
            throw new TextEntryParseException("Not an integer " + entry, e);
        }
    }

    @Override
    public String titleString(final Object object, final Localization localization) {
        return titleString(format, object);
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
    protected BigInteger doRestore(final String data) {
        return new BigInteger(data);
    }

    // /////// toString ///////

    @Override
    public String toString() {
        return "BigIntegerValueSemanticsProvider: " + format;
    }

}
