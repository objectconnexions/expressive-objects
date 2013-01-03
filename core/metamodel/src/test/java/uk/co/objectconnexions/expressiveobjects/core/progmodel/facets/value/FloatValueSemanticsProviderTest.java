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
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.floats.FloatValueSemanticsProviderAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.floats.FloatWrapperValueSemanticsProvider;

public class FloatValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private FloatValueSemanticsProviderAbstract value;
    private Float float1;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        context.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.value.format.float");
                will(returnValue(null));
            }
        });

        float1 = new Float(32.5f);
        allowMockAdapterToReturn(float1);

        holder = new FacetHolderImpl();
        setValue(value = new FloatWrapperValueSemanticsProvider(holder, mockConfiguration, mockContext));
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
    public void testTitleOf() {
        assertEquals("32.5", value.displayTitleOf(float1, (Localization) null));
    }

    @Test
    public void testParse() throws Exception {
        final Object parsed = value.parseTextEntry(null, "120.50", null);
        assertEquals(120.5f, ((Float) parsed).floatValue(), 0.0);
    }

    @Test
    public void testParseBadlyFormatedEntry() throws Exception {
        final Object parsed = value.parseTextEntry(null, "1,20.0", null);
        assertEquals(120.0f, ((Float) parsed).floatValue(), 0.0);
    }

    @Test
    public void testEncode() throws Exception {
        assertEquals("32.5", getEncodeableFacet().toEncodedString(createAdapter(float1)));
    }

    @Test
    public void testDecode() throws Exception {
        final Object restored = value.fromEncodedString("10.25");
        assertEquals(10.25, ((Float) restored).floatValue(), 0.0);
    }

}
