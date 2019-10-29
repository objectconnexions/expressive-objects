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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.TypedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Instance;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionParameter;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class ObjectActionParameterAbstractTest_getId_and_getName {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ObjectActionImpl parentAction;
    @Mock
    private TypedHolder actionParamPeer;
    @Mock
    private NamedFacet namedFacet;

    @Mock
    private ObjectSpecification stubSpecForString;
    @Mock
    private ObjectActionParameter stubObjectActionParameterString;
    @Mock
    private ObjectActionParameter stubObjectActionParameterString2;


    private final static class ObjectActionParameterAbstractToTest extends ObjectActionParameterAbstract {
        private ObjectActionParameterAbstractToTest(final int number, final ObjectActionImpl objectAction, final TypedHolder peer) {
            super(number, objectAction, peer);
        }

        private ObjectSpecification objectSpec;

        @Override
        public ObjectAdapter get(final ObjectAdapter owner) {
            return null;
        }

        @Override
        public Instance getInstance(final ObjectAdapter adapter) {
            return null;
        }

        @Override
        public FeatureType getFeatureType() {
            return null;
        }

        @Override
        public String isValid(final ObjectAdapter adapter, final Object proposedValue, final Localization localization) {
            return null;
        }

        @Override
        public ObjectSpecification getSpecification() {
            return objectSpec;
        }

        public void setSpecification(final ObjectSpecification objectSpec) {
            this.objectSpec = objectSpec;
        }
    }

    private ObjectActionParameterAbstractToTest objectActionParameter;

    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations() {
            {
                allowing(stubSpecForString).getSingularName();
                will(returnValue("string"));

                allowing(stubObjectActionParameterString).getSpecification();
                will(returnValue(stubSpecForString));

                allowing(stubObjectActionParameterString2).getSpecification();
                will(returnValue(stubSpecForString));
            }
        });

    }

    @Test
    public void getId_whenNamedFacetPresent() throws Exception {

        objectActionParameter = new ObjectActionParameterAbstractToTest(0, parentAction, actionParamPeer);

        context.checking(new Expectations() {
            {
                one(actionParamPeer).getFacet(NamedFacet.class);
                will(returnValue(namedFacet));

                atLeast(1).of(namedFacet).value();
                will(returnValue("Some parameter name"));
            }
        });

        assertThat(objectActionParameter.getId(), is("someParameterName"));
    }

    @Test
    public void getName_whenNamedFacetPresent() throws Exception {

        objectActionParameter = new ObjectActionParameterAbstractToTest(0, parentAction, actionParamPeer);

        context.checking(new Expectations() {
            {
                one(actionParamPeer).getFacet(NamedFacet.class);
                will(returnValue(namedFacet));

                atLeast(1).of(namedFacet).value();
                will(returnValue("Some parameter name"));
            }
        });

        assertThat(objectActionParameter.getName(), is("Some parameter name"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whenNamedFaceNotPresentAndOnlyOneParamOfType() throws Exception {

        objectActionParameter = new ObjectActionParameterAbstractToTest(0, parentAction, actionParamPeer);
        objectActionParameter.setSpecification(stubSpecForString);

        context.checking(new Expectations() {
            {
                one(actionParamPeer).getFacet(NamedFacet.class);
                will(returnValue(null));

                one(parentAction).getParameters(with(any(Filter.class)));
                will(returnValue(Lists.newArrayList(objectActionParameter)));
            }
        });

        assertThat(objectActionParameter.getName(), is("string"));
    }

    @Test
    public void getId_whenNamedFaceNotPresentAndMultipleParamsOfSameType() throws Exception {

        objectActionParameter = new ObjectActionParameterAbstractToTest(2, parentAction, actionParamPeer);
        objectActionParameter.setSpecification(stubSpecForString);

        context.checking(new Expectations() {
            {
                one(actionParamPeer).getFacet(NamedFacet.class);
                will(returnValue(null));

                one(parentAction).getParameters(with(any(Filter.class)));
                will(returnValue(Lists.newArrayList(stubObjectActionParameterString, objectActionParameter, stubObjectActionParameterString2)));
            }
        });

        assertThat(objectActionParameter.getId(), is("string2"));
    }

    @Test
    public void getName_whenNamedFaceNotPresentAndMultipleParamsOfSameType() throws Exception {

        objectActionParameter = new ObjectActionParameterAbstractToTest(2, parentAction, actionParamPeer);
        objectActionParameter.setSpecification(stubSpecForString);

        context.checking(new Expectations() {
            {
                one(actionParamPeer).getFacet(NamedFacet.class);
                will(returnValue(null));

                one(parentAction).getParameters(with(any(Filter.class)));
                will(returnValue(Lists.newArrayList(stubObjectActionParameterString, objectActionParameter, stubObjectActionParameterString2)));
            }
        });

        assertThat(objectActionParameter.getName(), is("string 2"));
    }

}
