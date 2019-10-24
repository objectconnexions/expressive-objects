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

import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.applib.value.Color;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.TextEntryParseException;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.color.ColorValueSemanticsProvider;

public class ColorValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private ColorValueSemanticsProvider value;
    private Color color;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        color = new Color(0x3366ff);
        allowMockAdapterToReturn(color);
        holder = new FacetHolderImpl();

        setValue(value = new ColorValueSemanticsProvider(holder, mockConfiguration, mockContext));
    }

    @Test
    public void testParseValidString() throws Exception {
        final Object newValue = value.parseTextEntry(null, "#3366fF", null);
        assertEquals(color, newValue);
    }

    @Test
    public void testParseInvalidString() throws Exception {
        try {
            value.parseTextEntry(null, "red", null);
            fail();
        } catch (final TextEntryParseException expected) {
        }
    }

    @Test
    public void testTitleOf() {
        assertEquals("#3366FF", value.displayTitleOf(color, (Localization) null));
    }

    @Test
    public void testTitleOfBlack() {
        assertEquals("Black", value.displayTitleOf(new Color(0), (Localization) null));
    }

    @Test
    public void testTitleOfWhite() {
        assertEquals("White", value.displayTitleOf(new Color(0xffffff), (Localization) null));
    }

    @Test
    public void testEncode() {
        assertEquals("3366ff", value.toEncodedString(color));
    }

    @Test
    public void testDecode() throws Exception {
        final Object newValue = value.fromEncodedString("3366ff");
        assertEquals(color, newValue);
    }

}
