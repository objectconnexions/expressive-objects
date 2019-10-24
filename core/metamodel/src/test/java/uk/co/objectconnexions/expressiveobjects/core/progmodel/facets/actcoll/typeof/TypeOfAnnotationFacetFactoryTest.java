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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actcoll.typeof;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.TypeOf;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typeof.TypeOfFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typeof.TypeOfFacetInferredFromArray;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typeof.TypeOfFacetInferredFromGenerics;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistryDefault;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.typeof.TypeOfAnnotationForCollectionsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.typeof.TypeOfFacetAnnotationForCollection;

public class TypeOfAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    private TypeOfAnnotationForCollectionsFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new TypeOfAnnotationForCollectionsFacetFactory();
        facetFactory.setCollectionTypeRegistry(new CollectionTypeRegistryDefault());
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testTypeOfFacetInferredForActionWithJavaUtilCollectionReturnType() {
        class Order {
        }
        class Customer {
            // rawtypes are intention for this test
            @SuppressWarnings({ "unused", "rawtypes" }) 
            @TypeOf(Order.class)
            public Collection someAction() {
                return null;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetAnnotationForCollection);
        final TypeOfFacetAnnotationForCollection typeOfFacetAnnotationForCollection = (TypeOfFacetAnnotationForCollection) facet;
        assertEquals(Order.class, typeOfFacetAnnotationForCollection.value());

        assertNoMethodsRemoved();
    }

    public void testTypeOfFacetInferredForCollectionWithJavaUtilCollectionReturnType() {
        class Order {
        }
        class Customer {
            // rawtypes are intention for this test
            @SuppressWarnings({ "unused", "rawtypes" })
            @TypeOf(Order.class)
            public Collection getOrders() {
                return null;
            }
        }
        final Method accessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, accessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetAnnotationForCollection);
        final TypeOfFacetAnnotationForCollection typeOfFacetAnnotationForCollection = (TypeOfFacetAnnotationForCollection) facet;
        assertEquals(Order.class, typeOfFacetAnnotationForCollection.value());

        assertNoMethodsRemoved();
    }

    public void testTypeOfFacetInferredForActionWithGenericCollectionReturnType() {
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> someAction() {
                return null;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetInferredFromGenerics);
        final TypeOfFacetInferredFromGenerics typeOfFacetInferredFromGenerics = (TypeOfFacetInferredFromGenerics) facet;
        assertEquals(Order.class, typeOfFacetInferredFromGenerics.value());

    }

    public void testTypeOfFacetInferredForCollectionWithGenericCollectionReturnType() {
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetInferredFromGenerics);
        final TypeOfFacetInferredFromGenerics typeOfFacetInferredFromGenerics = (TypeOfFacetInferredFromGenerics) facet;
        assertEquals(Order.class, typeOfFacetInferredFromGenerics.value());

    }

    public void testTypeOfFacetInferredForActionWithJavaUtilListReturnType() {
        class Order {
        }
        class Customer {
            // rawTypes is intentional here
            @SuppressWarnings({ "unused", "rawtypes" })
            @TypeOf(Order.class)
            public List someAction() {
                return null;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetAnnotationForCollection);
        final TypeOfFacetAnnotationForCollection typeOfFacetAnnotationForCollection = (TypeOfFacetAnnotationForCollection) facet;
        assertEquals(Order.class, typeOfFacetAnnotationForCollection.value());

        assertNoMethodsRemoved();
    }

    public void testTypeOfFacetInferredForCollectionWithJavaUtilListReturnType() {
        class Order {
        }
        class Customer {
            // rawTypes is intentional here
            @SuppressWarnings({ "unused", "rawtypes" })
            @TypeOf(Order.class)
            public List getOrders() {
                return null;
            }
        }
        final Method accessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, accessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetAnnotationForCollection);
        final TypeOfFacetAnnotationForCollection typeOfFacetAnnotationForCollection = (TypeOfFacetAnnotationForCollection) facet;
        assertEquals(Order.class, typeOfFacetAnnotationForCollection.value());

        assertNoMethodsRemoved();
    }

    public void testTypeOfFacetInferredForActionWithJavaUtilSetReturnType() {
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            @TypeOf(Order.class)
            public Set someAction() {
                return null;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetAnnotationForCollection);
        final TypeOfFacetAnnotationForCollection typeOfFacetAnnotationForCollection = (TypeOfFacetAnnotationForCollection) facet;
        assertEquals(Order.class, typeOfFacetAnnotationForCollection.value());

        assertNoMethodsRemoved();
    }

    public void testTypeOfFacetInferredForCollectionWithJavaUtilSetReturnType() {
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            @TypeOf(Order.class)
            public Set getOrders() {
                return null;
            }
        }
        final Method accessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, accessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetAnnotationForCollection);
        final TypeOfFacetAnnotationForCollection typeOfFacetAnnotationForCollection = (TypeOfFacetAnnotationForCollection) facet;
        assertEquals(Order.class, typeOfFacetAnnotationForCollection.value());

        assertNoMethodsRemoved();
    }

    public void testTypeOfFacetInferredForActionWithArrayReturnType() {
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Order[] someAction() {
                return null;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetInferredFromArray);
        final TypeOfFacetInferredFromArray typeOfFacetInferredFromArray = (TypeOfFacetInferredFromArray) facet;
        assertEquals(Order.class, typeOfFacetInferredFromArray.value());

        assertNoMethodsRemoved();
    }

    public void testTypeOfFacetIsInferredForCollectionFromOrderArray() {
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Order[] getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetInferredFromArray);
        final TypeOfFacetInferredFromArray typeOfFacetInferredFromArray = (TypeOfFacetInferredFromArray) facet;
        assertEquals(Order.class, typeOfFacetInferredFromArray.value());

    }

    public void testTypeOfAnnotationIgnoredForActionIfReturnTypeIsntACollectionType() {
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            @TypeOf(Order.class)
            public Customer someAction() {
                return null;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        assertNull(facetedMethod.getFacet(TypeOfFacet.class));

        assertNoMethodsRemoved();
    }

}
