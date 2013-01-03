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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.notinrepositorymenu;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.notinservicemenu.NotInServiceMenuFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.notinservicemenu.NotInServiceMenuFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.notinservicemenu.method.NotInServiceMenuMethodFacetFactory;

public class NotInRepositoryMenuMethodFacetFactoryTest extends AbstractFacetFactoryTest {

    private NotInServiceMenuMethodFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new NotInServiceMenuMethodFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testSupportingMethodForActionPickedUp() {
        // given
        class CustomerRepository {
            @SuppressWarnings("unused")
            public void someAction() {
            }

            @SuppressWarnings("unused")
            public boolean notInServiceMenuSomeAction() {
                return true;
            }
        }
        final Method actionMethod = findMethod(CustomerRepository.class, "someAction");

        // when
        facetFactory.process(new ProcessMethodContext(CustomerRepository.class, actionMethod, methodRemover, facetedMethod));

        // then
        final Facet facet = facetedMethod.getFacet(NotInServiceMenuFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof NotInServiceMenuFacetAbstract);

        // assertNoMethodsRemoved();
        assertEquals(1, methodRemover.getRemovedMethodMethodCalls().size());
    }

}
