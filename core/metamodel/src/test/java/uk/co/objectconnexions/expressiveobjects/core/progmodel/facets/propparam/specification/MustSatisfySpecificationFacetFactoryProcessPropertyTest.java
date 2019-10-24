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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.propparam.specification;

import static uk.co.objectconnexions.expressiveobjects.core.commons.matchers.ExpressiveObjectsMatchers.anInstanceOf;

import java.lang.reflect.Method;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MethodRemover;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.validate.perspec.MustSatisfySpecificationOnPropertyFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.validate.perspec.MustSatisfySpecificationOnPropertyFacetFactory;

@RunWith(JMock.class)
public class MustSatisfySpecificationFacetFactoryProcessPropertyTest {

    private final Mockery mockery = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private MethodRemover mockMethodRemover;
    private FacetedMethod mockFacetHolder;

    private Class<DomainObjectWithoutMustSatisfyAnnotations> domainObjectClassWithoutAnnotation;
    private Class<DomainObjectWithMustSatisfyAnnotations> domainObjectClassWithAnnotation;
    private Method firstNameMethodWithout;
    private Method firstNameMethodWith;

    @Before
    public void setUp() throws Exception {
        mockMethodRemover = mockery.mock(MethodRemover.class);
        mockFacetHolder = mockery.mock(FacetedMethod.class);
        domainObjectClassWithoutAnnotation = DomainObjectWithoutMustSatisfyAnnotations.class;
        domainObjectClassWithAnnotation = DomainObjectWithMustSatisfyAnnotations.class;
        firstNameMethodWithout = domainObjectClassWithoutAnnotation.getMethod("getFirstName");
        firstNameMethodWith = domainObjectClassWithAnnotation.getMethod("getFirstName");
    }

    @After
    public void tearDown() throws Exception {
        mockMethodRemover = null;
        mockFacetHolder = null;
    }

    @Test
    public void addsAMustSatisfySpecificationFacetIfAnnotated() {
        final MustSatisfySpecificationOnPropertyFacetFactory facetFactory = new MustSatisfySpecificationOnPropertyFacetFactory();

        mockery.checking(new Expectations() {
            {
                one(mockFacetHolder).addFacet(with(anInstanceOf(MustSatisfySpecificationOnPropertyFacet.class)));
            }
        });
        facetFactory.process(new ProcessMethodContext(domainObjectClassWithAnnotation.getClass(), firstNameMethodWith, mockMethodRemover, mockFacetHolder));
    }

    @Test
    public void doesNotAddsAMustSatisfySpecificationFacetIfNotAnnotated() {
        final MustSatisfySpecificationOnPropertyFacetFactory facetFactory = new MustSatisfySpecificationOnPropertyFacetFactory();

        mockery.checking(new Expectations() {
            {
                never(mockFacetHolder).addFacet(with(anInstanceOf(MustSatisfySpecificationOnPropertyFacet.class)));
            }
        });
        facetFactory.process(new ProcessMethodContext(domainObjectClassWithAnnotation.getClass(), firstNameMethodWithout, mockMethodRemover, mockFacetHolder));
    }

}
