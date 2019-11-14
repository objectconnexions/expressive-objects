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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value;

import static uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.ReturnArgumentJMockAction.returnArgument;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.encodeable.EncodableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodeable.EncodableFacetUsingEncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable.ParseableFacetUsingParser;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderAndFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public abstract class ValueSemanticsProviderAbstractTestCase {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private ValueSemanticsProviderAndFacetAbstract<?> valueSemanticsProvider;
    private EncodableFacetUsingEncoderDecoder encodeableFacet;
    private ParseableFacetUsingParser parseableFacet;

    @Mock
    protected FacetHolder mockFacetHolder;
    @Mock
    protected ExpressiveObjectsConfiguration mockConfiguration;
    @Mock
    protected ValueSemanticsProviderContext mockContext;
    @Mock
    protected ServicesInjector mockDependencyInjector;
    @Mock
    protected AdapterManager mockAdapterManager;
    @Mock
    protected SpecificationLoaderSpi mockSpecificationLoader;
    @Mock
    protected AuthenticationSessionProvider mockAuthenticationSessionProvider;
    @Mock
    protected ObjectAdapter mockAdapter;

    @Before
    public void setUp() throws Exception {
        Locale.setDefault(Locale.UK);

        context.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(with(any(String.class)), with(any(String.class)));
                will(returnArgument(1));

                allowing(mockConfiguration).getBoolean(with(any(String.class)), with(any(Boolean.class)));
                will(returnArgument(1));

                allowing(mockConfiguration).getString("expressive-objects.locale");
                will(returnValue(null));

                allowing(mockDependencyInjector).injectServicesInto(with(any(Object.class)));

                never(mockAuthenticationSessionProvider);

                never(mockAdapterManager);
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        context.assertIsSatisfied();
    }

    protected void allowMockAdapterToReturn(final Object pojo) {
        context.checking(new Expectations() {
            {
                allowing(mockAdapter).getObject();
                will(returnValue(pojo));
            }
        });
    }

    protected void setValue(final ValueSemanticsProviderAndFacetAbstract<?> value) {
        this.valueSemanticsProvider = value;
        this.encodeableFacet = new EncodableFacetUsingEncoderDecoder(value, mockFacetHolder, mockAdapterManager, mockDependencyInjector);
        this.parseableFacet = new ParseableFacetUsingParser(value, mockFacetHolder, DeploymentCategory.PRODUCTION, mockAuthenticationSessionProvider, mockDependencyInjector, mockAdapterManager);
    }

    protected ValueSemanticsProviderAndFacetAbstract<?> getValue() {
        return valueSemanticsProvider;
    }

    protected EncodableFacet getEncodeableFacet() {
        return encodeableFacet;
    }

    protected ParseableFacet getParseableFacet() {
        return parseableFacet;
    }

    protected ObjectAdapter createAdapter(final Object object) {
        return mockAdapter;
    }

    @Test
    public void testParseNull() throws Exception {
        Assume.assumeThat(valueSemanticsProvider.getParser(), is(not(nullValue())));
        try {
            valueSemanticsProvider.parseTextEntry(null, null, null);
            fail();
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testParseEmptyString() throws Exception {
        Assume.assumeThat(valueSemanticsProvider.getParser(), is(not(nullValue())));

        final Object newValue = valueSemanticsProvider.parseTextEntry(null, "", null);
        assertNull(newValue);
    }

    @Test
    public void testDecodeNULL() throws Exception {
        Assume.assumeThat(valueSemanticsProvider.getEncoderDecoder(), is(not(nullValue())));
        
        final Object newValue = encodeableFacet.fromEncodedString(EncodableFacetUsingEncoderDecoder.ENCODED_NULL);
        assertNull(newValue);
    }

    @Test
    public void testEmptyEncoding() {
        Assume.assumeThat(valueSemanticsProvider.getEncoderDecoder(), is(not(nullValue())));

        assertEquals(EncodableFacetUsingEncoderDecoder.ENCODED_NULL, encodeableFacet.toEncodedString(null));
    }

    @Test
    public void testTitleOfForNullObject() {
        assertEquals("", valueSemanticsProvider.displayTitleOf(null, (Localization) null));
    }
    
    protected boolean hasJavaChangedDateFormat() {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String value = format.format(new Date(0));
        return value.equals("01/01/1970, 00:00");
    }
}
