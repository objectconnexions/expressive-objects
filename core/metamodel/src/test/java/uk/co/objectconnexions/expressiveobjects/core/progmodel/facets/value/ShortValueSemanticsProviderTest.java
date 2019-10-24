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

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.TextEntryParseException;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.shortint.ShortValueSemanticsProviderAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.shortint.ShortWrapperValueSemanticsProvider;

public class ShortValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private ShortValueSemanticsProviderAbstract value;
    private Short short1;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        context.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.value.format.short");
                will(returnValue(null));
            }
        });

        short1 = Short.valueOf((short) 32);
        allowMockAdapterToReturn(short1);

        holder = new FacetHolderImpl();

        setValue(value = new ShortWrapperValueSemanticsProvider(holder, mockConfiguration, mockContext));
    }

    @Test
    public void testInvalidParse() throws Exception {
        try {
            value.parseTextEntry(null, "one", null);
            fail();
        } catch (final TextEntryParseException expected) {
        }
    }

    @Test
    public void testTitleOfForPositiveValue() {
        assertEquals("32", value.displayTitleOf(short1, (Localization) null));
    }

    @Test
    public void testTitleOfForLargestNegativeValue() {
        assertEquals("-128", value.displayTitleOf(Short.valueOf((short) -128), (Localization) null));
    }

    @Test
    public void testParse() throws Exception {
        final Object newValue = value.parseTextEntry(null, "120", null);
        assertEquals(Short.valueOf((short) 120), newValue);
    }

    @Test
    public void testParseOfOddEntry() throws Exception {
        final Object newValue = value.parseTextEntry(null, "1,20.0", null);
        assertEquals(Short.valueOf((short) 120), newValue);
    }

}
