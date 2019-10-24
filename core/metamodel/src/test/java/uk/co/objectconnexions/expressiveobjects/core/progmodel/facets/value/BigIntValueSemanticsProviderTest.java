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

import java.math.BigInteger;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.TextEntryParseException;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.biginteger.BigIntegerValueSemanticsProvider;

public class BigIntValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private BigInteger bigInt;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        bigInt = new BigInteger("132199");
        allowMockAdapterToReturn(bigInt);

        context.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.value.format.int");
                will(returnValue(null));
            }
        });

        holder = new FacetHolderImpl();
        setValue(new BigIntegerValueSemanticsProvider(holder, mockConfiguration, mockContext));
    }

    @Test
    public void testParseValidString() throws Exception {
        final Object newValue = getValue().parseTextEntry(null, "2142342334", null);
        assertEquals(new BigInteger("2142342334"), newValue);
    }

    @Test
    public void testParseInvalidString() throws Exception {
        try {
            getValue().parseTextEntry(null, "214xxx2342334", null);
            fail();
        } catch (final TextEntryParseException expected) {
        }
    }

    @Test
    public void testTitle() throws Exception {
        assertEquals("132,199", getValue().displayTitleOf(bigInt, (Localization) null));
    }

    @Test
    public void testEncode() throws Exception {
        assertEquals("132199", getValue().toEncodedString(bigInt));
    }

    @Test
    public void testDecode() throws Exception {
        final Object newValue = getValue().fromEncodedString("432289991");
        assertEquals(new BigInteger("432289991"), newValue);
    }

}
