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
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.longs.LongValueSemanticsProviderAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.longs.LongWrapperValueSemanticsProvider;

public class LongValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private LongValueSemanticsProviderAbstract value;

    private Object longObj;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        longObj = new Long(367322);
        allowMockAdapterToReturn(longObj);
        holder = new FacetHolderImpl();

        context.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.value.format.long");
                will(returnValue(null));
            }
        });

        setValue(value = new LongWrapperValueSemanticsProvider(holder, mockConfiguration, mockContext));
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
    public void testOutputAsString() {
        assertEquals("367,322", value.displayTitleOf(longObj, (Localization) null));
    }

    @Test
    public void testParse() throws Exception {
        final Object parsed = value.parseTextEntry(null, "120", null);
        assertEquals("120", parsed.toString());
    }

    @Test
    public void testParseWithBadlyFormattedEntry() throws Exception {
        final Object parsed = value.parseTextEntry(null, "1,20.0", null);
        assertEquals("120", parsed.toString());
    }

    @Test
    public void testEncode() throws Exception {
        assertEquals("367322", value.toEncodedString(longObj));
    }

    @Test
    public void test() throws Exception {
        final Object parsed = value.fromEncodedString("234");
        assertEquals("234", parsed.toString());
    }
}
