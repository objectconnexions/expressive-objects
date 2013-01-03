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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.objecttype;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.objecttype.ObjectSpecIdFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutor;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryJUnit4TestCase;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;

public class ObjectTypeDerivedFromClassNameFacetFactoryTest extends AbstractFacetFactoryJUnit4TestCase {

    @Mock
    private ClassSubstitutor classSubstitutor;

    private ObjectTypeDerivedFromClassNameFacetFactory facetFactory;

    @Before
    public void setUp() throws Exception {
        facetFactory = new ObjectTypeDerivedFromClassNameFacetFactory();
        facetFactory.setSpecificationLookup(reflector);
        facetFactory.setClassSubstitutor(classSubstitutor);
    }

    static class Customer {
    }
    
    static class CustomerAsManufacturedByCglibByteCodeEnhancer extends Customer {
    }

    @Test
    public void installsFacet_andDelegatesToClassSubstitutor() {

        
        expectNoMethodsRemoved();
        context.checking(new Expectations() {
            {
                one(classSubstitutor).getClass(CustomerAsManufacturedByCglibByteCodeEnhancer.class);
                will(returnValue(Customer.class));
            }
        });
        
        facetFactory.process(new ProcessClassContext(CustomerAsManufacturedByCglibByteCodeEnhancer.class, methodRemover, facetHolderImpl));

        final ObjectSpecIdFacet facet = facetHolderImpl.getFacet(ObjectSpecIdFacet.class);
        
        assertThat(facet, is(not(nullValue())));
        assertThat(facet instanceof ObjectSpecIdFacetDerivedFromClassName, is(true));
        assertThat(facet.value(), is(ObjectSpecId.of(Customer.class.getCanonicalName())));
    }

}

