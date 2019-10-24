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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callback;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.RemovedCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.RemovingCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.remove.RemoveCallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.remove.RemovedCallbackFacetViaMethod;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.remove.RemovingCallbackFacetViaMethod;

public class RemoveCallbackFacetFactoryTest extends AbstractFacetFactoryTest {

    private RemoveCallbackFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new RemoveCallbackFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testRemovingLifecycleMethodPickedUpOn() {
        class Customer {
            @SuppressWarnings("unused")
            public void removing() {
            };
        }
        final Method method = findMethod(Customer.class, "removing");

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(RemovingCallbackFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof RemovingCallbackFacetViaMethod);
        final RemovingCallbackFacetViaMethod removingCallbackFacetViaMethod = (RemovingCallbackFacetViaMethod) facet;
        assertEquals(method, removingCallbackFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(method));
    }

    public void testRemovedLifecycleMethodPickedUpOn() {
        class Customer {
            @SuppressWarnings("unused")
            public void removed() {
            };
        }
        final Method method = findMethod(Customer.class, "removed");

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(RemovedCallbackFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof RemovedCallbackFacetViaMethod);
        final RemovedCallbackFacetViaMethod removedCallbackFacetViaMethod = (RemovedCallbackFacetViaMethod) facet;
        assertEquals(method, removedCallbackFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(method));
    }

}
