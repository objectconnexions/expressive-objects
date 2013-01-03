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

package uk.co.objectconnexions.expressiveobjects.progmodel.wrapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.events.PropertyModifyEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.PropertyUsabilityEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.PropertyVisibilityEvent;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectPersistor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Allow;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionResult;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Veto;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl.dflt.ObjectSpecificationDefault;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.DisabledFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.staticmethod.DisabledFacetAlwaysEverywhere;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.accessor.PropertyAccessorFacetViaAccessor;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.modify.PropertySetterFacetViaSetterMethod;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.SimpleSession;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.claimapp.employees.Employee;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.applib.DisabledException;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.metamodel.internal.WrapperFactoryDefault;

public class WrappedFactoryDefaultTest_wrappedObject_transient {

    @Rule
    public final JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private AdapterManager mockAdapterManager;
    @Mock
    private AuthenticationSessionProvider mockAuthenticationSessionProvider;
    @Mock
    private ObjectPersistor mockObjectPersistor;
    @Mock
    private SpecificationLoader mockSpecificationLookup;

    private Employee employeeDO;
    @Mock
    private ObjectAdapter mockEmployeeAdapter;
    @Mock
    private ObjectSpecificationDefault mockEmployeeSpec;
    @Mock
    private OneToOneAssociation mockPasswordMember;
    @Mock
    private Identifier mockPasswordIdentifier;

    @Mock
    protected ObjectAdapter mockPasswordAdapter;
    
    private final String passwordValue = "12345678";

    private final SimpleSession session = new SimpleSession("tester", Collections.<String>emptyList());

    private List<Facet> facets;
    private Method getPasswordMethod;
    private Method setPasswordMethod;


    private WrapperFactoryDefault wrapperFactory;
    private Employee employeeWO;


    @Before
    public void setUp() throws Exception {

        // employeeRepository = new EmployeeRepositoryImpl();
        // claimRepository = new ClaimRepositoryImpl();

        employeeDO = new Employee();
        employeeDO.setName("Smith");
        
        getPasswordMethod = Employee.class.getMethod("getPassword");
        setPasswordMethod = Employee.class.getMethod("setPassword", String.class);

        wrapperFactory = new WrapperFactoryDefault();
        wrapperFactory.setAdapterManager(mockAdapterManager);
        wrapperFactory.setAuthenticationSessionProvider(mockAuthenticationSessionProvider);
        wrapperFactory.setObjectPersistor(mockObjectPersistor);
        wrapperFactory.setSpecificationLookup(mockSpecificationLookup);
        
        context.checking(new Expectations() {
            {
                allowing(mockAdapterManager).getAdapterFor(employeeDO);
                will(returnValue(mockEmployeeAdapter));

                allowing(mockAdapterManager).adapterFor(passwordValue);
                will(returnValue(mockPasswordAdapter));

                allowing(mockEmployeeAdapter).getSpecification();
                will(returnValue(mockEmployeeSpec));

                allowing(mockEmployeeAdapter).getObject();
                will(returnValue(employeeDO));

                allowing(mockPasswordAdapter).getObject();
                will(returnValue(passwordValue));

                allowing(mockPasswordMember).getIdentifier();
                will(returnValue(mockPasswordIdentifier));

                allowing(mockSpecificationLookup).loadSpecification(Employee.class);
                will(returnValue(mockEmployeeSpec));
                
                allowing(mockEmployeeSpec).getMember(with(setPasswordMethod));
                will(returnValue(mockPasswordMember));

                allowing(mockEmployeeSpec).getMember(with(getPasswordMethod));
                will(returnValue(mockPasswordMember));

                allowing(mockPasswordMember).getName();
                will(returnValue("password"));

                allowing(mockAuthenticationSessionProvider).getAuthenticationSession();
                will(returnValue(session));
                
                allowing(mockPasswordMember).isOneToOneAssociation();
                will(returnValue(true));

                allowing(mockPasswordMember).isOneToManyAssociation();
                will(returnValue(false));
            }
        });

        employeeWO = wrapperFactory.wrap(employeeDO);
    }

    @Test(expected = DisabledException.class)
    public void shouldNotBeAbleToModifyProperty() {

        // given
        final DisabledFacet disabledFacet = new DisabledFacetAlwaysEverywhere(mockPasswordMember);
        facets = Arrays.asList((Facet)disabledFacet, new PropertySetterFacetViaSetterMethod(setPasswordMethod, mockPasswordMember));

        final Consent visibilityConsent = new Allow(new InteractionResult(new PropertyVisibilityEvent(employeeDO, null)));

        final InteractionResult usabilityInteractionResult = new InteractionResult(new PropertyUsabilityEvent(employeeDO, null));
        usabilityInteractionResult.advise("disabled", disabledFacet);
        final Consent usabilityConsent = new Veto(usabilityInteractionResult);

        context.checking(new Expectations() {
            {
                allowing(mockPasswordMember).getFacets(with(any(Filter.class)));
                will(returnValue(facets));
                
                allowing(mockPasswordMember).isVisible(session, mockEmployeeAdapter, Where.ANYWHERE);
                will(returnValue(visibilityConsent));
                
                allowing(mockPasswordMember).isUsable(session, mockEmployeeAdapter, Where.ANYWHERE);
                will(returnValue(usabilityConsent));
            }
        });
        
        // when
        employeeWO.setPassword(passwordValue);
        
        // then should throw exception
    }

    @Test
    public void canModifyProperty() {
        // given

        final Consent visibilityConsent = new Allow(new InteractionResult(new PropertyVisibilityEvent(employeeDO, mockPasswordIdentifier)));
        final Consent usabilityConsent = new Allow(new InteractionResult(new PropertyUsabilityEvent(employeeDO, mockPasswordIdentifier)));
        final Consent validityConsent = new Allow(new InteractionResult(new PropertyModifyEvent(employeeDO, mockPasswordIdentifier, passwordValue)));

        context.checking(new Expectations() {
            {
                allowing(mockPasswordMember).isVisible(session, mockEmployeeAdapter, Where.ANYWHERE);
                will(returnValue(visibilityConsent));
                
                allowing(mockPasswordMember).isUsable(session, mockEmployeeAdapter, Where.ANYWHERE);
                will(returnValue(usabilityConsent));
                
                allowing(mockPasswordMember).isAssociationValid(mockEmployeeAdapter, mockPasswordAdapter);
                will(returnValue(validityConsent));
            }
        });

        facets = Arrays.asList((Facet)new PropertySetterFacetViaSetterMethod(setPasswordMethod, mockPasswordMember));
        context.checking(new Expectations() {
            {
                one(mockPasswordMember).getFacets(with(any(Filter.class)));
                will(returnValue(facets));
                
                one(mockPasswordMember).set(mockEmployeeAdapter, mockPasswordAdapter);
            }
        });

        // when
        employeeWO.setPassword(passwordValue);


        // and given
        facets = Arrays.asList((Facet)new PropertyAccessorFacetViaAccessor(getPasswordMethod, mockPasswordMember));
        context.checking(new Expectations() {
            {
                one(mockPasswordMember).getFacets(with(any(Filter.class)));
                will(returnValue(facets));
                
                one(mockPasswordMember).get(mockEmployeeAdapter);
                will(returnValue(mockPasswordAdapter));
            }
        });

        // then be allowed
        assertThat(employeeWO.getPassword(), is(passwordValue));
    }
}
