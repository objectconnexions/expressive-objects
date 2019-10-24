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
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.LoadedCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.LoadingCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.load.LoadCallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.load.LoadedCallbackFacetViaMethod;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.load.LoadingCallbackFacetViaMethod;

public class LoadCallbackFacetFactoryTest extends AbstractFacetFactoryTest {

    private LoadCallbackFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new LoadCallbackFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testLoadingLifecycleMethodPickedUpOn() {
        class Customer {
            @SuppressWarnings("unused")
            public void loading() {
            };
        }
        final Method method = findMethod(Customer.class, "loading");

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(LoadingCallbackFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof LoadingCallbackFacetViaMethod);
        final LoadingCallbackFacetViaMethod loadingCallbackFacetViaMethod = (LoadingCallbackFacetViaMethod) facet;
        assertEquals(method, loadingCallbackFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(method));
    }

    public void testLoadedLifecycleMethodPickedUpOn() {
        class Customer {
            @SuppressWarnings("unused")
            public void loaded() {
            };
        }
        final Method method = findMethod(Customer.class, "loaded");

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(LoadedCallbackFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof LoadedCallbackFacetViaMethod);
        final LoadedCallbackFacetViaMethod loadedCallbackFacetViaMethod = (LoadedCallbackFacetViaMethod) facet;
        assertEquals(method, loadedCallbackFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(method));
    }

}
