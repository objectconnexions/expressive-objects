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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ident.title.annotation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Title;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.LocalizationProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodFinderUtils;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.title.annotation.TitleFacetViaTitleAnnotation;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.title.annotation.TitleFacetViaTitleAnnotation.TitleComponent;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class TitleFacetViaTitleAnnotationTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);

    @Mock
    private FacetHolder mockFacetHolder;

    @Mock
    private ObjectAdapter mockObjectAdapter;

    @Mock
    private AdapterManager mockAdapterManager;
    
    @Mock
    private LocalizationProvider mockLocalizationProvider;

    protected static class DomainObjectWithProblemInItsAnnotatedTitleMethod {

        @Title
        public String screwedTitle() {
            throw new NullPointerException();
        }

    }

    protected static class NormalDomainObject {

        @Title(sequence = "1.0")
        public String titleElement1() {
            return "Normal";
        }

        @Title(sequence = "2.0")
        public String titleElement2() {
            return "Domain";
        }

        @Title(sequence = "3.0")
        public String titleElement3() {
            return "Object";
        }

    }

    @Test
    public void testTitle() throws Exception {
        final List<Method> methods = Arrays.asList(NormalDomainObject.class.getMethod("titleElement1"), NormalDomainObject.class.getMethod("titleElement2"), NormalDomainObject.class.getMethod("titleElement3"));

        final List<TitleComponent> components = Lists.transform(methods, TitleComponent.FROM_METHOD);
        final TitleFacetViaTitleAnnotation facet = new TitleFacetViaTitleAnnotation(components, mockFacetHolder, mockAdapterManager, mockLocalizationProvider);
        final NormalDomainObject normalPojo = new NormalDomainObject();
        final Sequence sequence = context.sequence("in-title-element-order");
        context.checking(new Expectations() {
            {
                allowing(mockObjectAdapter).getObject();
                will(returnValue(normalPojo));

                allowing(mockAdapterManager).adapterFor("Normal");
                inSequence(sequence);

                allowing(mockAdapterManager).adapterFor("Domain");
                inSequence(sequence);

                allowing(mockAdapterManager).adapterFor("Object");
                inSequence(sequence);
            }
        });

        final String title = facet.title(mockObjectAdapter, null);
        assertThat(title, is("Normal Domain Object"));
    }

    @Test
    public void titleThrowsException() {
        final List<Method> methods = MethodFinderUtils.findMethodsWithAnnotation(DomainObjectWithProblemInItsAnnotatedTitleMethod.class, MethodScope.OBJECT, Title.class);

        final List<TitleComponent> components = Lists.transform(methods, TitleComponent.FROM_METHOD);
        final TitleFacetViaTitleAnnotation facet = new TitleFacetViaTitleAnnotation(components, mockFacetHolder, mockAdapterManager, mockLocalizationProvider);
        final DomainObjectWithProblemInItsAnnotatedTitleMethod screwedPojo = new DomainObjectWithProblemInItsAnnotatedTitleMethod();
        context.checking(new Expectations() {
            {
                allowing(mockObjectAdapter).getObject();
                will(returnValue(screwedPojo));
            }
        });

        final String title = facet.title(mockObjectAdapter, null);
        assertThat(title, is("Failed Title"));
    }

}
