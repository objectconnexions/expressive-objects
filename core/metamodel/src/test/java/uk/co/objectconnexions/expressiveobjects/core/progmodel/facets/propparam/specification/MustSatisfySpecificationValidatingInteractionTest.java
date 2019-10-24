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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.IdentifiedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.PropertyModifyContext;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.validperspec.MustSatisfySpecificationOnTypeFacet;

@RunWith(JMock.class)
public class MustSatisfySpecificationValidatingInteractionTest {

    private final Mockery mockery = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private MustSatisfySpecificationOnTypeFacet facetForSpecificationAlwaysSatisfied;
    private MustSatisfySpecificationOnTypeFacet facetForSpecificationNeverSatisfied;
    private FacetHolder mockHolder;

    private PropertyModifyContext mockContext;

    private ObjectAdapter mockProposedObjectAdapter;
    private Object mockProposedObject;

    private SpecificationAlwaysSatisfied specificationAlwaysSatisfied;
    private SpecificationNeverSatisfied specificationNeverSatisfied;

    @Before
    public void setUp() throws Exception {
        mockHolder = mockery.mock(IdentifiedHolder.class);
        specificationAlwaysSatisfied = new SpecificationAlwaysSatisfied();
        specificationNeverSatisfied = new SpecificationNeverSatisfied();

        facetForSpecificationAlwaysSatisfied = new MustSatisfySpecificationOnTypeFacet(Utils.listOf(specificationAlwaysSatisfied), mockHolder);
        facetForSpecificationNeverSatisfied = new MustSatisfySpecificationOnTypeFacet(Utils.listOf(specificationNeverSatisfied), mockHolder);

        mockContext = mockery.mock(PropertyModifyContext.class);
        mockProposedObjectAdapter = mockery.mock(ObjectAdapter.class, "proposed");
        mockProposedObject = mockery.mock(Object.class, "proposedObject");

        mockery.checking(new Expectations() {
            {
                one(mockContext).getProposed();
                will(returnValue(mockProposedObjectAdapter));

                one(mockProposedObjectAdapter).getObject();
                will(returnValue(mockProposedObject));
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        mockHolder = null;
        facetForSpecificationAlwaysSatisfied = null;
        facetForSpecificationNeverSatisfied = null;
        mockContext = null;
    }

    @Test
    public void validatesWhenSpecificationDoesNotVeto() {
        final String reason = facetForSpecificationAlwaysSatisfied.invalidates(mockContext);
        assertThat(reason, is(nullValue()));
    }

    @Test
    public void invalidatesWhenSpecificationVetoes() {
        final String reason = facetForSpecificationNeverSatisfied.invalidates(mockContext);
        assertThat(reason, is(not(nullValue())));
        assertThat(reason, is("not satisfied"));
    }

}
