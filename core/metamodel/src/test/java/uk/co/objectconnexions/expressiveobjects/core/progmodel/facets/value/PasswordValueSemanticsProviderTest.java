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

import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.value.Password;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.password.PasswordValueSemanticsProvider;

public class PasswordValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private PasswordValueSemanticsProvider adapter;
    private Object password;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        holder = new FacetHolderImpl();
        setValue(adapter = new PasswordValueSemanticsProvider(holder, mockConfiguration, mockContext));
        password = new Password("secret");
    }

    @Test
    public void testEncoding() {
        assertEquals("secret", adapter.toEncodedString(password));
    }
}
