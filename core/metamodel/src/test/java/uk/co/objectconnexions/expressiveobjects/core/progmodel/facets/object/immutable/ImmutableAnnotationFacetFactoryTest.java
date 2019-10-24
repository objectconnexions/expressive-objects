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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.immutable;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Immutable;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.When;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.immutable.ImmutableFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.immutable.annotation.ImmutableAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.immutable.annotation.ImmutableFacetAnnotation;

public class ImmutableAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    private ImmutableAnnotationFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new ImmutableAnnotationFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testImmutableAnnotationPickedUpOnClassAndDefaultsToAlways() {
        @Immutable
        class Customer {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ImmutableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ImmutableFacetAnnotation);
        final ImmutableFacetAnnotation immutableFacetAnnotation = (ImmutableFacetAnnotation) facet;
        assertEquals(When.ALWAYS, immutableFacetAnnotation.when());

        assertNoMethodsRemoved();
    }

    public void testImmutableAnnotationAlwaysPickedUpOnClass() {
        @Immutable(When.ALWAYS)
        class Customer {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ImmutableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ImmutableFacetAnnotation);
        final ImmutableFacetAnnotation immutableFacetAnnotation = (ImmutableFacetAnnotation) facet;
        assertEquals(When.ALWAYS, immutableFacetAnnotation.when());

        assertNoMethodsRemoved();
    }

    public void testImmutableAnnotationNeverPickedUpOnClass() {
        @Immutable(When.NEVER)
        class Customer {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ImmutableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ImmutableFacetAnnotation);
        final ImmutableFacetAnnotation immutableFacetAnnotation = (ImmutableFacetAnnotation) facet;
        assertEquals(When.NEVER, immutableFacetAnnotation.when());

        assertNoMethodsRemoved();
    }

    public void testImmutableAnnotationOncePersistedPickedUpOnClass() {
        @Immutable(When.ONCE_PERSISTED)
        class Customer {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ImmutableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ImmutableFacetAnnotation);
        final ImmutableFacetAnnotation immutableFacetAnnotation = (ImmutableFacetAnnotation) facet;
        assertEquals(When.ONCE_PERSISTED, immutableFacetAnnotation.when());

        assertNoMethodsRemoved();
    }

    public void testImmutableAnnotationUntilPersistedPickedUpOnClass() {
        @Immutable(When.UNTIL_PERSISTED)
        class Customer {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ImmutableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ImmutableFacetAnnotation);
        final ImmutableFacetAnnotation immutableFacetAnnotation = (ImmutableFacetAnnotation) facet;
        assertEquals(When.UNTIL_PERSISTED, immutableFacetAnnotation.when());

        assertNoMethodsRemoved();
    }

}
