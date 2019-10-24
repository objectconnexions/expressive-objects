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
package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable;

import java.util.IllegalFormatWidthException;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.ParsingException;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.TextEntryParseException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.value.ValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class ParseableFacetUsingParserTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private FacetHolder mockFacetHolder;
    @Mock
    private AuthenticationSessionProvider mockAuthenticationSessionProvider;
    @Mock
    private ServicesInjector mockDependencyInjector;
    @Mock
    private AdapterManager mockAdapterManager;

    private ParseableFacetUsingParser parseableFacetUsingParser;

    @Before
    public void setUp() throws Exception {

        context.checking(new Expectations() {
            {
                never(mockAuthenticationSessionProvider);
                never(mockAdapterManager);

                allowing(mockFacetHolder).containsFacet(ValueFacet.class);
                will(returnValue(Boolean.FALSE));

                allowing(mockDependencyInjector).injectServicesInto(with(any(Object.class)));
            }
        });

        final Parser<String> parser = new Parser<String>() {
            @Override
            public String parseTextEntry(final Object contextPojo, final String entry, Localization localization) {
                if (entry.equals("invalid")) {
                    throw new ParsingException();
                }
                if (entry.equals("number")) {
                    throw new NumberFormatException();
                }
                if (entry.equals("format")) {
                    throw new IllegalFormatWidthException(2);
                }
                return entry.toUpperCase();
            }

            @Override
            public int typicalLength() {
                return 0;
            }

            @Override
            public String displayTitleOf(final String object, final Localization localization) {
                return null;
            }

            @Override
            public String displayTitleOf(final String object, final String usingMask) {
                return null;
            }

            @Override
            public String parseableTitleOf(final String existing) {
                return null;
            }
        };
        parseableFacetUsingParser = new ParseableFacetUsingParser(parser, mockFacetHolder, DeploymentCategory.PRODUCTION, mockAuthenticationSessionProvider, mockDependencyInjector, mockAdapterManager);
    }

    @Ignore
    @Test
    public void testParseNormalEntry() throws Exception {
        // TODO why is this so complicated to check!!!
        /*
         * final AuthenticationSession session =
         * mockery.mock(AuthenticationSession.class);
         * 
         * mockery.checking(new Expectations(){{
         * one(mockAdapterManager).adapterFor("XXX");
         * will(returnValue(mockAdapter));
         * 
         * one(mockAdapter).getSpecification();
         * will(returnValue(mockSpecification));
         * 
         * one(mockAuthenticationSessionProvider).getAuthenticationSession();
         * will(returnValue(session));
         * 
         * allowing(mockSpecification).createValidityInteractionContext(session,
         * InteractionInvocationMethod.BY_USER, mockAdapter); }}); ObjectAdapter
         * adapter = parseableFacetUsingParser.parseTextEntry(null, "xxx");
         * 
         * adapter.getObject();
         */
    }

    @Test(expected = TextEntryParseException.class)
    public void parsingExceptionRethrown() throws Exception {
        parseableFacetUsingParser.parseTextEntry(null, "invalid", null);
    }

    @Test(expected = TextEntryParseException.class)
    public void numberFormatExceptionRethrown() throws Exception {
        parseableFacetUsingParser.parseTextEntry(null, "number", null);
    }

    @Test(expected = TextEntryParseException.class)
    public void illegalFormatExceptionRethrown() throws Exception {
        parseableFacetUsingParser.parseTextEntry(null, "format", null);
    }
}
