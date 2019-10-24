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
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.UpdatedCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.UpdatingCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.update.UpdateCallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.update.UpdatedCallbackFacetViaMethod;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.update.UpdatingCallbackFacetViaMethod;

public class UpdateCallbackFacetFactoryTest extends AbstractFacetFactoryTest {

    private UpdateCallbackFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new UpdateCallbackFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testUpdatingLifecycleMethodPickedUpOn() {
        class Customer {
            @SuppressWarnings("unused")
            public void updating() {
            };
        }
        final Method method = findMethod(Customer.class, "updating");

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(UpdatingCallbackFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof UpdatingCallbackFacetViaMethod);
        final UpdatingCallbackFacetViaMethod updatingCallbackFacetViaMethod = (UpdatingCallbackFacetViaMethod) facet;
        assertEquals(method, updatingCallbackFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(method));
    }

    public void testUpdatedLifecycleMethodPickedUpOn() {
        class Customer {
            @SuppressWarnings("unused")
            public void updated() {
            };
        }
        final Method method = findMethod(Customer.class, "updated");

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(UpdatedCallbackFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof UpdatedCallbackFacetViaMethod);
        final UpdatedCallbackFacetViaMethod updatedCallbackFacetViaMethod = (UpdatedCallbackFacetViaMethod) facet;
        assertEquals(method, updatedCallbackFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(method));
    }

}
