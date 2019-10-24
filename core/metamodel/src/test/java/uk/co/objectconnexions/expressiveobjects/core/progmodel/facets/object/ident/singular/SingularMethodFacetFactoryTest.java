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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ident.singular;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.named.staticmethod.NamedFacetViaSingularNameStaticMethod;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.named.staticmethod.NamedFacetViaSingularNameStaticMethodFacetFactory;

public class SingularMethodFacetFactoryTest extends AbstractFacetFactoryTest {

    private NamedFacetViaSingularNameStaticMethodFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new NamedFacetViaSingularNameStaticMethodFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public static class Customer {
        public static String singularName() {
            return "Some name";
        }
    }

    public void testSingularNameMethodPickedUpOnClassAndMethodRemoved() {
        final Method singularNameMethod = findMethod(Customer.class, "singularName");

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NamedFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof NamedFacetViaSingularNameStaticMethod);
        final NamedFacetViaSingularNameStaticMethod namedFacetViaSingularNameStaticMethod = (NamedFacetViaSingularNameStaticMethod) facet;
        assertEquals("Some name", namedFacetViaSingularNameStaticMethod.value());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(singularNameMethod));
    }

}
