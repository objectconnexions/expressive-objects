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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.content;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Allow;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Veto;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.InvalidEntryException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidatingInteractionAdvisor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.TextParseableFieldImpl;

public class TextParseableField_ParseTextEntry {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ObjectAdapter mockParent;
    @Mock
    private ObjectAdapter mockChild;
    @Mock
    private OneToOneAssociation mockOtoa;
    @Mock
    private ObjectSpecification mockOtoaSpec;
    @Mock
    private ParseableFacet mockParseableFacet;
    @Mock
    private ObjectAdapter mockParsedText;
    @Mock
    private ValidatingInteractionAdvisor mockValidatingInteractionAdvisorFacet;

    private TextParseableFieldImpl fieldImpl;

    @Before
    public void setUp() throws Exception {

        context.checking(new Expectations() {
            {
                allowing(mockOtoa).getIdentifier();

                allowing(mockOtoa).getSpecification();
                will(returnValue(mockOtoaSpec));

                one(mockOtoaSpec).getFacet(ParseableFacet.class);
                will(returnValue(mockParseableFacet));
            }
        });

        fieldImpl = new TextParseableFieldImpl(mockParent, mockChild, mockOtoa);
    }


    @Test
    public void parsedTextIsValidForSpecAndCorrespondingObjectValidAsAssociation() {

        context.checking(new Expectations() {
            {
                one(mockParseableFacet).parseTextEntry(mockChild, "foo", null);
                will(returnValue(mockParsedText));

                atLeast(1).of(mockOtoa).isAssociationValid(mockParent, mockParsedText);
                will(returnValue(Allow.DEFAULT));

                one(mockOtoa).isMandatory();
            }
        });

        fieldImpl.parseTextEntry("foo");
    }

    @Test(expected = InvalidEntryException.class)
    public void parsedTextIsNullWhenMandatoryThrowsException() {

        context.checking(new Expectations() {
            {
                one(mockParseableFacet).parseTextEntry(mockChild, "foo", null);
                will(returnValue(null));

                atLeast(1).of(mockOtoa).isAssociationValid(mockParent, null);
                will(returnValue(Allow.DEFAULT));

                one(mockOtoa).isMandatory();
                will(returnValue(true));
            }
        });

        fieldImpl.parseTextEntry("foo");
    }

    @Test
    public void parsedTextIsValidAccordingToSpecificationFacet() {

        context.checking(new Expectations() {
            {
                one(mockParseableFacet).parseTextEntry(mockChild, "foo", null);
                will(returnValue(mockParsedText));

                atLeast(1).of(mockOtoa).isAssociationValid(mockParent, mockParsedText);
                will(returnValue(Allow.DEFAULT));

                allowing(mockOtoa).isMandatory();
                will(returnValue(true));
            }
        });

        fieldImpl.parseTextEntry("foo");
    }

    @Test(expected = InvalidEntryException.class)
    public void parsedTextIsInvalidAccordingToSpecification() {

        context.checking(new Expectations() {
            {
                allowing(mockParseableFacet).parseTextEntry(mockChild, "foo", null);
                will(returnValue(mockParsedText));

                atLeast(1).of(mockOtoa).isAssociationValid(mockParent, mockParsedText);
                will(returnValue(Veto.DEFAULT));

                allowing(mockOtoa).isMandatory();
                will(returnValue(true));
            }
        });

        fieldImpl.parseTextEntry("foo");
    }

    @Test(expected = InvalidEntryException.class)
    public void parsedTextIsInvalidAccordingToAssociation() {

        context.checking(new Expectations() {
            {
                allowing(mockParseableFacet).parseTextEntry(mockChild, "foo", null);
                will(returnValue(mockParsedText));

                one(mockOtoa).isAssociationValid(mockParent, mockParsedText);
                will(returnValue(Veto.DEFAULT));
            }
        });

        fieldImpl.parseTextEntry("foo");
    }

}
