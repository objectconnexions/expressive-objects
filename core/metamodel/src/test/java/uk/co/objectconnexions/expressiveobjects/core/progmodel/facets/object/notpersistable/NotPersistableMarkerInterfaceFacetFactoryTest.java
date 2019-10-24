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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.notpersistable;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.NotPersistable;
import uk.co.objectconnexions.expressiveobjects.applib.marker.NonPersistable;
import uk.co.objectconnexions.expressiveobjects.applib.marker.ProgramPersistable;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.notpersistable.NotPersistableFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;

public class NotPersistableMarkerInterfaceFacetFactoryTest extends AbstractFacetFactoryTest {

    private NotPersistableMarkerInterfacesFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new NotPersistableMarkerInterfacesFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testProgramPersistableMeansNotPersistableByUser() {
        class Customer implements ProgramPersistable {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NotPersistableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof NotPersistableFacetMarkerInterface);
        final NotPersistableFacetMarkerInterface notPersistableFacetMarkerInterface = (NotPersistableFacetMarkerInterface) facet;
        final NotPersistable.By value = notPersistableFacetMarkerInterface.value();
        assertEquals(NotPersistable.By.USER, value);

        assertNoMethodsRemoved();
    }

    public void testNotPersistable() {
        class Customer implements NonPersistable {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NotPersistableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof NotPersistableFacetMarkerInterface);
        final NotPersistableFacetMarkerInterface notPersistableFacetMarkerInterface = (NotPersistableFacetMarkerInterface) facet;
        final NotPersistable.By value = notPersistableFacetMarkerInterface.value();
        assertEquals(NotPersistable.By.USER_OR_PROGRAM, value);

        assertNoMethodsRemoved();
    }

}
