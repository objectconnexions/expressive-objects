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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.propcoll.notpersisted;

import java.lang.reflect.Method;
import java.util.Collection;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.NotPersisted;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.notpersisted.NotPersistedFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.notpersisted.annotation.NotPersistedAnnotationForCollectionFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.notpersisted.annotation.NotPersistedFacetAnnotationForCollection;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.notpersisted.annotation.NotPersistedAnnotationForPropertyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.notpersisted.annotation.NotPersistedFacetAnnotationForProperty;

public class NotPersistedAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    public void testNotPersistedAnnotationPickedUpOnProperty() {
        final NotPersistedAnnotationForPropertyFacetFactory facetFactory = new NotPersistedAnnotationForPropertyFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            @NotPersisted()
            public String getFirstName() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getFirstName");

        facetFactory.process(new ProcessMethodContext(Customer.class, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NotPersistedFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof NotPersistedFacetAnnotationForProperty);

        assertNoMethodsRemoved();
    }

    public void testNotPersistedAnnotationPickedUpOnCollection() {
        final NotPersistedAnnotationForCollectionFacetFactory facetFactory = new NotPersistedAnnotationForCollectionFacetFactory();

        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            @NotPersisted()
            public Collection<Order> getOrders() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NotPersistedFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof NotPersistedFacetAnnotationForCollection);

        assertNoMethodsRemoved();
    }

}
