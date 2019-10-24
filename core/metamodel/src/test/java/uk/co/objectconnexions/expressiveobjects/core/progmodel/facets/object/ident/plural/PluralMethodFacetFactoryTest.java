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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ident.plural;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.plural.PluralFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.plural.staticmethod.PluralFacetViaMethod;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.plural.staticmethod.PluralMethodFacetFactory;

public class PluralMethodFacetFactoryTest extends AbstractFacetFactoryTest {

    private PluralMethodFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new PluralMethodFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public static class Customer {
        public static String pluralName() {
            return "Some plural name";
        }
    }

    public void testPluralNameMethodPickedUpOnClassAndMethodRemoved() {
        final Method pluralNameMethod = findMethod(Customer.class, "pluralName");

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PluralFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PluralFacetViaMethod);
        final PluralFacetViaMethod pluralFacet = (PluralFacetViaMethod) facet;
        assertEquals("Some plural name", pluralFacet.value());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(pluralNameMethod));
    }

}
