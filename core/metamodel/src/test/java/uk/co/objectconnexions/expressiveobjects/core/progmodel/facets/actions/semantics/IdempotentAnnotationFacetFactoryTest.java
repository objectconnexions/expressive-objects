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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.semantics;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.ActionSemantics;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Idempotent;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.QueryOnly;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.ActionSemantics.Of;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Debug;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.debug.DebugFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.semantics.ActionSemanticsFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.debug.annotation.DebugAnnotationFacetFactory;

public class IdempotentAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    private IdempotentAnnotationFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new IdempotentAnnotationFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testQueryOnlyAnnotationPickedUp() {
        class Customer {
            @Idempotent
            public void someAction() {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ActionSemanticsFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof IdempotentFacetAnnotation);

        assertNoMethodsRemoved();
    }

    public void testNoAnnotationPickedUp() {
        class Customer {
            @SuppressWarnings("unused")
            public void someAction() {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ActionSemanticsFacet.class);
        assertNull(facet);

        assertNoMethodsRemoved();
    }

}
