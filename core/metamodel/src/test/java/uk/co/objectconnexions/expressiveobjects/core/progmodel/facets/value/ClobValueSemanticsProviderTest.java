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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.applib.value.Clob;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.clobs.ClobValueSemanticsProvider;
import org.junit.Before;
import org.junit.Test;

public class ClobValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private ClobValueSemanticsProvider value;
    private Clob clob;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        clob = new Clob("myfile1.xml", "application", "xml", "abcdef");
        allowMockAdapterToReturn(clob);
        holder = new FacetHolderImpl();

        setValue(value = new ClobValueSemanticsProvider(holder, mockConfiguration, mockContext));
    }

    @Test
    public void testTitleOf() {
        assertEquals("myfile1.xml", value.displayTitleOf(clob, (Localization) null));
    }

    @Test
    public void testEncode_and_decode() {
        String encoded = value.toEncodedString(clob);
        assertEquals("myfile1.xml:application/xml:abcdef", encoded);
        Clob decoded = value.fromEncodedString(encoded);
        assertThat(decoded.getName(), is("myfile1.xml"));
        assertThat(decoded.getMimeType().getPrimaryType(), is("application"));
        assertThat(decoded.getMimeType().getSubType(), is("xml"));
        assertThat(decoded.getChars(), is((CharSequence)"abcdef"));
    }

}
