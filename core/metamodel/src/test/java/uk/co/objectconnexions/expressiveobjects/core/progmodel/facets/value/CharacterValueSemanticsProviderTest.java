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
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.InvalidEntryException;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.chars.CharValueSemanticsProviderAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.chars.CharWrapperValueSemanticsProvider;

public class CharacterValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private CharValueSemanticsProviderAbstract value;

    private Character character;

    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        character = Character.valueOf('r');
        holder = new FacetHolderImpl();
        setValue(value = new CharWrapperValueSemanticsProvider(holder, mockConfiguration, mockContext));
    }

    @Test
    public void testParseLongString() throws Exception {
        try {
            value.parseTextEntry(null, "one", null);
            fail();
        } catch (final InvalidEntryException expected) {
        }
    }

    @Test
    public void testTitleOf() {
        assertEquals("r", value.displayTitleOf(character, (Localization) null));
    }

    @Test
    public void testValidParse() throws Exception {
        final Object parse = value.parseTextEntry(null, "t", null);
        assertEquals(Character.valueOf('t'), parse);
    }

    @Test
    public void testEncode() throws Exception {
        assertEquals("r", value.toEncodedString(character));
    }

    @Test
    public void testDecode() throws Exception {
        final Object restore = value.fromEncodedString("Y");
        assertEquals(Character.valueOf('Y'), restore);
    }
}
