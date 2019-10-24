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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.applib.value.Money;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.TextEntryParseException;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.money.MoneyValueSemanticsProvider;

public class MoneyValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private static final String POUND_SYMBOL = "\u00A3";
    private static final String EURO_SYMBOL = "\u20AC";
    private MoneyValueSemanticsProvider adapter;
    private Money originalMoney;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        Locale.setDefault(Locale.UK);
        holder = new FacetHolderImpl();
        setValue(adapter = new MoneyValueSemanticsProvider(holder, mockConfiguration, mockContext));
    }

    private Money createMoney(final double amount, final String currency) {
        return new Money(amount, currency);
    }

    @Test
    public void testLocale() {
        assertEquals(Locale.UK, Locale.getDefault());
    }

    @Test
    public void testEncoding() {
        originalMoney = new Money(10.5, "gbp");
        final String data = adapter.toEncodedString(originalMoney);
        assertEquals("10.5 GBP", data);
    }

    @Test
    public void testDecoding() {
        final Object restored = adapter.fromEncodedString("23.77 FFR");
        final Money expected = new Money(23.77, "FFR");
        assertEquals(expected, restored);
    }

    @Test
    public void testTitleOfWithPounds() {
        originalMoney = new Money(10.5, "gbp");
        assertEquals(POUND_SYMBOL + "10.50", adapter.displayTitleOf(originalMoney, (Localization) null));
    }

    @Test
    public void testTitleOfWithNonLocalCurrency() {
        assertEquals("10.50 USD", adapter.displayTitleOf(createMoney(10.50, "usd"), (Localization) null));
    }

    @Test
    public void testTitleWithUnknownCurrency() {
        assertEquals("10.50 UNK", adapter.displayTitleOf(createMoney(10.50, "UNK"), (Localization) null));
    }

    @Test
    public void testUserEntryWithCurrency() {
        final Money money = createMoney(10.5, "gbp");
        final Money parsed = adapter.parseTextEntry(money, "22.45 USD", null);
        assertEquals(new Money(22.45, "usd"), parsed);
    }

    @Test
    public void testNewUserEntryUsesPreviousCurrency() {
        originalMoney = new Money(10.5, "gbp");
        final Object parsed = adapter.parseTextEntry(originalMoney, "22.45", null);
        assertEquals(new Money(22.45, "gbp"), parsed);
    }

    @Test
    public void testReplacementEntryForDefaultCurrency() {
        // MoneyValueSemanticsProvider adapter = new
        // MoneyValueSemanticsProvider(new Money(10.3, "gbp"));
        final Object parsed = adapter.parseTextEntry(originalMoney, POUND_SYMBOL + "80.90", null);
        assertEquals(new Money(80.90, "gbp"), parsed);
    }

    @Test
    public void testSpecifyingCurrencyInEntry() {
        final Object parsed = adapter.parseTextEntry(originalMoney, "3021.50 EUr", null);
        assertEquals("3,021.50 EUR", adapter.displayTitleOf(parsed, (Localization) null));
    }

    @Test
    public void testUsingLocalCurrencySymbol() {
        // MoneyValueSemanticsProvider adapter = new
        // MoneyValueSemanticsProvider(new Money(0L, "gbp"));
        final Object parsed = adapter.parseTextEntry(originalMoney, POUND_SYMBOL + "3021.50", null);
        assertEquals(POUND_SYMBOL + "3,021.50", adapter.titleString(parsed, null));
    }

    @Test
    public void testInvalidCurrencyCodeIsRejected() throws Exception {
        try {
            adapter.parseTextEntry(originalMoney, "3021.50  XYZ", null);
            fail("invalid code accepted " + adapter);
        } catch (final TextEntryParseException expected) {
        }
    }

    @Test
    public void testInvalidCurrencySymbolIsRejected() throws Exception {
        try {
            adapter.parseTextEntry(originalMoney, EURO_SYMBOL + "3021.50", null);
            fail("invalid code accepted " + adapter);
        } catch (final TextEntryParseException expected) {
        }
    }

    @Test
    public void testNewValueDefaultsToLocalCurrency() throws Exception {
        final Money parsed = adapter.parseTextEntry(originalMoney, "3021.50", null);
        assertEquals(POUND_SYMBOL + "3,021.50", adapter.displayTitleOf(parsed, (Localization) null));
    }

    @Test
    public void testUnrelatedCurrencySymbolIsRejected() throws Exception {
        final Money money = createMoney(1, "eur");
        try {
            adapter.parseTextEntry(money, "$3021.50", null);
            fail("invalid code accepted " + adapter);
        } catch (final TextEntryParseException expected) {
        }
    }

}
