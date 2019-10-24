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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.junit.Ignore;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.javalang.IteratorFilteringFacetFactory;

public class IteratorFilteringFacetFactoryTest extends AbstractFacetFactoryTest {

    private IteratorFilteringFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new IteratorFilteringFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testRequestsRemoverToRemoveIteratorMethods() {
        class Customer {
            @SuppressWarnings("unused")
            public void someAction() {
            }
        }
        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        assertEquals(1, methodRemover.getRemoveMethodArgsCalls().size());
    }

    public void testNoIteratorMethodFiltered() {
        class Customer {
            @SuppressWarnings("unused")
            public void someAction() {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        assertFalse(facetFactory.recognizes(actionMethod));
    }

    /**
     * Not tested; this facet factory is needed, I think, but only filters out
     * stuff when generics are in use.
     */
    @Ignore
    public void xxxtestIterableIteratorMethodFiltered() {
        class Customer implements Iterable<Customer> {
            @SuppressWarnings("unused")
            public void someAction() {
            }

            @Override
            public Iterator<Customer> iterator() {
                return null;
            }
        }
        final Method iteratorMethod = findMethod(Customer.class, "iterator");

        assertTrue(facetFactory.recognizes(iteratorMethod));
    }

}
