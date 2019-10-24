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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.ordering.fieldorder;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.FieldOrder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.orderfields.FieldOrderFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.orderfields.FieldOrderAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.orderfields.FieldOrderFacetAnnotation;

public class FieldOrderAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    private FieldOrderAnnotationFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new FieldOrderAnnotationFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testFieldOrderAnnotationPickedUpOnClass() {
        @FieldOrder("foo,bar")
        class Customer {
        }

        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(FieldOrderFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof FieldOrderFacetAnnotation);
        final FieldOrderFacetAnnotation fieldOrderFacetAnnotation = (FieldOrderFacetAnnotation) facet;
        assertEquals("foo,bar", fieldOrderFacetAnnotation.value());

        assertNoMethodsRemoved();
    }

}
