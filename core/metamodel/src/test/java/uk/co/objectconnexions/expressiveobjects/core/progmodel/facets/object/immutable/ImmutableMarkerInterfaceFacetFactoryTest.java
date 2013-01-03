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

import uk.co.objectconnexions.expressiveobjects.applib.annotation.When;
import uk.co.objectconnexions.expressiveobjects.applib.marker.AlwaysImmutable;
import uk.co.objectconnexions.expressiveobjects.applib.marker.ImmutableOncePersisted;
import uk.co.objectconnexions.expressiveobjects.applib.marker.ImmutableUntilPersisted;
import uk.co.objectconnexions.expressiveobjects.applib.marker.NeverImmutable;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.immutable.ImmutableFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.immutable.markerifc.ImmutableFacetMarkerInterface;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.immutable.markerifc.ImmutableMarkerInterfaceFacetFactory;

public class ImmutableMarkerInterfaceFacetFactoryTest extends AbstractFacetFactoryTest {

    private ImmutableMarkerInterfaceFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new ImmutableMarkerInterfaceFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testAlwaysImmutable() {
        class Customer implements AlwaysImmutable {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ImmutableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ImmutableFacetMarkerInterface);
        final ImmutableFacetMarkerInterface immutableFacetMarkerInterface = (ImmutableFacetMarkerInterface) facet;
        assertEquals(When.ALWAYS, immutableFacetMarkerInterface.when());

        assertNoMethodsRemoved();
    }

    public void testImmutableOncePersisted() {
        class Customer implements ImmutableOncePersisted {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ImmutableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ImmutableFacetMarkerInterface);
        final ImmutableFacetMarkerInterface immutableFacetMarkerInterface = (ImmutableFacetMarkerInterface) facet;
        assertEquals(When.ONCE_PERSISTED, immutableFacetMarkerInterface.when());

        assertNoMethodsRemoved();
    }

    public void testImmutableUntilPersisted() {
        class Customer implements ImmutableUntilPersisted {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ImmutableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ImmutableFacetMarkerInterface);
        final ImmutableFacetMarkerInterface immutableFacetMarkerInterface = (ImmutableFacetMarkerInterface) facet;
        assertEquals(When.UNTIL_PERSISTED, immutableFacetMarkerInterface.when());

        assertNoMethodsRemoved();
    }

    public void testNeverImmutable() {
        class Customer implements NeverImmutable {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(ImmutableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ImmutableFacetMarkerInterface);
        final ImmutableFacetMarkerInterface immutableFacetMarkerInterface = (ImmutableFacetMarkerInterface) facet;
        assertEquals(When.NEVER, immutableFacetMarkerInterface.when());

        assertNoMethodsRemoved();
    }

}
