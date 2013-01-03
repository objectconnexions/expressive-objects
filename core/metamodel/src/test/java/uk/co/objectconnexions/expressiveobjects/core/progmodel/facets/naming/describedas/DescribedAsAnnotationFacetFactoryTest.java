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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.naming.describedas;

import java.lang.reflect.Method;
import java.util.Collection;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.DescribedAs;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessParameterContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.describedas.DescribedAsFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.describedas.DescribedAsFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.describedas.annotation.DescribedAsAnnotationOnMemberFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.describedas.annotation.DescribedAsAnnotationOnTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.describedas.annotation.DescribedAsAnnotationOnParameterFacetFactory;

public class DescribedAsAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    public void testDescribedAsAnnotationPickedUpOnClass() {
        final DescribedAsAnnotationOnTypeFacetFactory facetFactory = new DescribedAsAnnotationOnTypeFacetFactory();

        @DescribedAs("some description")
        class Customer {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(DescribedAsFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DescribedAsFacetAbstract);
        final DescribedAsFacetAbstract describedAsFacetAbstract = (DescribedAsFacetAbstract) facet;
        assertEquals("some description", describedAsFacetAbstract.value());

        assertNoMethodsRemoved();
    }

    public void testDescribedAsAnnotationPickedUpOnProperty() {
        final DescribedAsAnnotationOnMemberFacetFactory facetFactory = new DescribedAsAnnotationOnMemberFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            @DescribedAs("some description")
            public int getNumberOfOrders() {
                return 0;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "getNumberOfOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(DescribedAsFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DescribedAsFacetAbstract);
        final DescribedAsFacetAbstract describedAsFacetAbstract = (DescribedAsFacetAbstract) facet;
        assertEquals("some description", describedAsFacetAbstract.value());

        assertNoMethodsRemoved();
    }

    public void testDescribedAsAnnotationPickedUpOnCollection() {
        final DescribedAsAnnotationOnMemberFacetFactory facetFactory = new DescribedAsAnnotationOnMemberFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            @DescribedAs("some description")
            public Collection<?> getOrders() {
                return null;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(DescribedAsFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DescribedAsFacetAbstract);
        final DescribedAsFacetAbstract describedAsFacetAbstract = (DescribedAsFacetAbstract) facet;
        assertEquals("some description", describedAsFacetAbstract.value());

        assertNoMethodsRemoved();
    }

    public void testDescribedAsAnnotationPickedUpOnAction() {
        final DescribedAsAnnotationOnMemberFacetFactory facetFactory = new DescribedAsAnnotationOnMemberFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            @DescribedAs("some description")
            public void someAction() {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(DescribedAsFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DescribedAsFacetAbstract);
        final DescribedAsFacetAbstract describedAsFacetAbstract = (DescribedAsFacetAbstract) facet;
        assertEquals("some description", describedAsFacetAbstract.value());

        assertNoMethodsRemoved();
    }

    public void testDescribedAsAnnotationPickedUpOnActionParameter() {
        final DescribedAsAnnotationOnParameterFacetFactory facetFactory = new DescribedAsAnnotationOnParameterFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            public void someAction(@DescribedAs("some description") final int x) {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction", new Class[] { int.class });

        facetFactory.processParams(new ProcessParameterContext(actionMethod, 0, facetedMethodParameter));

        final Facet facet = facetedMethodParameter.getFacet(DescribedAsFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DescribedAsFacetAbstract);
        final DescribedAsFacetAbstract describedAsFacetAbstract = (DescribedAsFacetAbstract) facet;
        assertEquals("some description", describedAsFacetAbstract.value());
    }

}
