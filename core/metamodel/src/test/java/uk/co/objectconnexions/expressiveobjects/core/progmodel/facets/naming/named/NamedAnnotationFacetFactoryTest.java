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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.naming.named;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Named;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessParameterContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryJUnit4TestCase;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.named.annotation.NamedAnnotationOnMemberFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.named.annotation.NamedAnnotationOnTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.named.annotation.NamedAnnotationOnParameterFacetFactory;

public class NamedAnnotationFacetFactoryTest extends AbstractFacetFactoryJUnit4TestCase {

    @Test
    public void testNamedAnnotationPickedUpOnClass() {

        final NamedAnnotationOnTypeFacetFactory facetFactory = new NamedAnnotationOnTypeFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        @Named("some name")
        class Customer {
        }
        
        expectNoMethodsRemoved();
        
        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NamedFacet.class);
        assertThat(facet, is(not(nullValue())));
        assertThat(facet instanceof NamedFacetAbstract, is(true));
        final NamedFacetAbstract namedFacetAbstract = (NamedFacetAbstract) facet;
        assertThat(namedFacetAbstract.value(), equalTo("some name"));
    }

    @Test
    public void testNamedAnnotationPickedUpOnProperty() {

        final NamedAnnotationOnMemberFacetFactory facetFactory = new NamedAnnotationOnMemberFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        class Customer {
            @SuppressWarnings("unused")
            @Named("some name")
            public int getNumberOfOrders() {
                return 0;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "getNumberOfOrders");

        expectNoMethodsRemoved();

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NamedFacet.class);
        assertThat(facet, is(not(nullValue())));
        assertThat(facet instanceof NamedFacetAbstract, is(true));
        final NamedFacetAbstract namedFacetAbstract = (NamedFacetAbstract) facet;
        assertThat(namedFacetAbstract.value(), equalTo("some name"));
    }

    public void testNamedAnnotationPickedUpOnCollection() {
        final NamedAnnotationOnMemberFacetFactory facetFactory = new NamedAnnotationOnMemberFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        class Customer {
            @SuppressWarnings("unused")
            @Named("some name")
            public Collection<?> getOrders() {
                return null;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "getOrders");

        expectNoMethodsRemoved();

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NamedFacet.class);
        assertThat(facet, is(not(nullValue())));
        assertThat(facet instanceof NamedFacetAbstract, is(true));
        final NamedFacetAbstract namedFacetAbstract = (NamedFacetAbstract) facet;
        assertThat(namedFacetAbstract.value(), equalTo("some name"));
    }

    public void testNamedAnnotationPickedUpOnAction() {
        final NamedAnnotationOnMemberFacetFactory facetFactory = new NamedAnnotationOnMemberFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        class Customer {
            @SuppressWarnings("unused")
            @Named("some name")
            public void someAction() {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        expectNoMethodsRemoved();

        facetFactory.process(new ProcessMethodContext(Customer.class, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NamedFacet.class);
        assertThat(facet, is(not(nullValue())));
        assertThat(facet instanceof NamedFacetAbstract, is(true));
        final NamedFacetAbstract namedFacetAbstract = (NamedFacetAbstract) facet;
        assertThat(namedFacetAbstract.value(), equalTo("some name"));
    }

    public void testNamedAnnotationPickedUpOnActionParameter() {

        final NamedAnnotationOnParameterFacetFactory facetFactory = new NamedAnnotationOnParameterFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        class Customer {
            @SuppressWarnings("unused")
            public void someAction(@Named("some name") final int x) {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction", new Class[] { int.class });

        expectNoMethodsRemoved();

        facetFactory.processParams(new ProcessParameterContext(actionMethod, 0, facetedMethodParameter));

        final Facet facet = facetedMethodParameter.getFacet(NamedFacet.class);
        assertThat(facet, is(not(nullValue())));
        assertThat(facet instanceof NamedFacetAbstract, is(true));
        final NamedFacetAbstract namedFacetAbstract = (NamedFacetAbstract) facet;
        assertThat(namedFacetAbstract.value(), equalTo("some name"));
    }

}
