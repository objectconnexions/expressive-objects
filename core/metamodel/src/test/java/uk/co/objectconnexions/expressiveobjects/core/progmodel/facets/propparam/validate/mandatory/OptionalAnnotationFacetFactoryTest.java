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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.propparam.validate.mandatory;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Optional;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessParameterContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.mandatory.MandatoryFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.mandatory.annotation.MandatoryFacetInvertedByOptionalForParameter;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.mandatory.annotation.OptionalAnnotationForParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.mandatory.annotation.MandatoryFacetInvertedByOptionalForProperty;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.mandatory.annotation.OptionalAnnotationForPropertyFacetFactory;

public class OptionalAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    public void testOptionalAnnotationPickedUpOnProperty() {
        final OptionalAnnotationForPropertyFacetFactory facetFactory = new OptionalAnnotationForPropertyFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            @Optional
            public String getFirstName() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getFirstName");

        facetFactory.process(new ProcessMethodContext(Customer.class, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(MandatoryFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MandatoryFacetInvertedByOptionalForProperty);
    }

    public void testOptionalAnnotationPickedUpOnActionParameter() {
        final OptionalAnnotationForParameterFacetFactory facetFactory = new OptionalAnnotationForParameterFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            public void someAction(@Optional final String foo) {
            }
        }
        final Method method = findMethod(Customer.class, "someAction", new Class[] { String.class });

        facetFactory.processParams(new ProcessParameterContext(method, 0, facetedMethodParameter));

        final Facet facet = facetedMethodParameter.getFacet(MandatoryFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MandatoryFacetInvertedByOptionalForParameter);
    }

    public void testOptionalAnnotationIgnoredForPrimitiveOnProperty() {
        final OptionalAnnotationForPropertyFacetFactory facetFactory = new OptionalAnnotationForPropertyFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            @Optional
            public int getNumberOfOrders() {
                return 0;
            }
        }
        final Method method = findMethod(Customer.class, "getNumberOfOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, method, methodRemover, facetedMethod));

        assertNull(facetedMethod.getFacet(MandatoryFacet.class));
    }

    public void testOptionalAnnotationIgnoredForPrimitiveOnActionParameter() {
        final OptionalAnnotationForParameterFacetFactory facetFactory = new OptionalAnnotationForParameterFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            public void someAction(@Optional final int foo) {
            }
        }
        final Method method = findMethod(Customer.class, "someAction", new Class[] { int.class });

        facetFactory.processParams(new ProcessParameterContext(method, 0, facetedMethodParameter));

        assertNull(facetedMethod.getFacet(MandatoryFacet.class));
    }

}
