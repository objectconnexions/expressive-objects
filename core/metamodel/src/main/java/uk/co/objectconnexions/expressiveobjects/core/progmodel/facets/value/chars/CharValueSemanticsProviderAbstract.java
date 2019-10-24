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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.chars;

import java.text.DecimalFormat;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.InvalidEntryException;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderAndFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public abstract class CharValueSemanticsProviderAbstract extends ValueSemanticsProviderAndFacetAbstract<Character> implements CharValueFacet {

    private static Class<? extends Facet> type() {
        return CharValueFacet.class;
    }

    private static final Character DEFAULT_VALUE = Character.valueOf((char) 0);
    private static final int TYPICAL_LENGTH = 1;

    public CharValueSemanticsProviderAbstract(final FacetHolder holder, final Class<Character> adaptedClass, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(type(), holder, adaptedClass, TYPICAL_LENGTH, Immutability.IMMUTABLE, EqualByContent.HONOURED, DEFAULT_VALUE, configuration, context);
    }

    // //////////////////////////////////////////////////////////////////
    // Parser
    // //////////////////////////////////////////////////////////////////

    @Override
    public Character doParse(final Object context, final String entry) {
        if (entry.length() > 1) {
            throw new InvalidEntryException("Only a single character is required");
        } else {
            return Character.valueOf(entry.charAt(0));
        }
    }

    @Override
    public String titleString(final Object value, final Localization localization) {
        return value == null ? "" : value.toString();
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
    protected Character doRestore(final String data) {
        return Character.valueOf(data.charAt(0));
    }

    // //////////////////////////////////////////////////////////////////
    // CharValueFacet
    // //////////////////////////////////////////////////////////////////

    @Override
    public Character charValue(final ObjectAdapter object) {
        return object == null ? null : (Character) object.getObject();
    }

    @Override
    public ObjectAdapter createValue(final Character value) {
        return getAdapterManager().adapterFor(value);
    }

    // /////// toString ///////

    @Override
    public String toString() {
        return "CharacterValueSemanticsProvider";
    }

}
