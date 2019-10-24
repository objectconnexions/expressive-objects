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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.ordering.memberorder;

import java.lang.reflect.Method;
import java.util.Collection;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.members.order.MemberOrderFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.order.MemberOrderAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.order.MemberOrderFacetAnnotation;

public class MemberOrderAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    private MemberOrderAnnotationFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new MemberOrderAnnotationFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testMemberOrderAnnotationPickedUpOnProperty() {
        class Customer {
            @SuppressWarnings("unused")
            @MemberOrder(sequence = "1")
            public String getFirstName() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getFirstName");

        facetFactory.process(new ProcessMethodContext(Customer.class, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(MemberOrderFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MemberOrderFacetAnnotation);
        final MemberOrderFacetAnnotation memberOrderFacetAnnotation = (MemberOrderFacetAnnotation) facet;
        assertEquals("1", memberOrderFacetAnnotation.sequence());

        assertNoMethodsRemoved();
    }

    public void testMemberOrderAnnotationPickedUpOnCollection() {
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            @MemberOrder(sequence = "2")
            public Collection<Order> getOrders() {
                return null;
            }

            @SuppressWarnings("unused")
            public void addToOrders(final Order o) {
            }
        }
        final Method method = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(MemberOrderFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MemberOrderFacetAnnotation);
        final MemberOrderFacetAnnotation memberOrderFacetAnnotation = (MemberOrderFacetAnnotation) facet;
        assertEquals("2", memberOrderFacetAnnotation.sequence());

        assertNoMethodsRemoved();
    }

    public void testMemberOrderAnnotationPickedUpOnAction() {
        class Customer {
            @SuppressWarnings("unused")
            @MemberOrder(sequence = "3")
            public void someAction() {
            }
        }
        final Method method = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(MemberOrderFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MemberOrderFacetAnnotation);
        final MemberOrderFacetAnnotation memberOrderFacetAnnotation = (MemberOrderFacetAnnotation) facet;
        assertEquals("3", memberOrderFacetAnnotation.sequence());

        assertNoMethodsRemoved();
    }

}
