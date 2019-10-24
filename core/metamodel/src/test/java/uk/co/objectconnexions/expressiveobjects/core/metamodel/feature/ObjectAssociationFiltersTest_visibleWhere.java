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
package uk.co.objectconnexions.expressiveobjects.core.metamodel.feature;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.When;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.hide.HiddenFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

@RunWith(Parameterized.class)
public class ObjectAssociationFiltersTest_visibleWhere {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    @Mock
    private ObjectAssociation mockObjectAssociation;

    @Mock
    private HiddenFacet mockHiddenFacet;

    // given
    private When when;
    private Where where;

    // when
    private Where whereContext;

    // then
    private boolean expectedVisibility;

    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {When.ALWAYS, Where.ANYWHERE, Where.ANYWHERE, false},
                {When.UNTIL_PERSISTED, Where.ANYWHERE, Where.ANYWHERE, true},
                {When.ONCE_PERSISTED, Where.ANYWHERE, Where.ANYWHERE, true},
                {When.NEVER, Where.ANYWHERE, Where.ANYWHERE, true},
                {When.ALWAYS, Where.OBJECT_FORMS, Where.OBJECT_FORMS, false},
                {When.ALWAYS, Where.OBJECT_FORMS, Where.ALL_TABLES, true},
                {When.ALWAYS, Where.OBJECT_FORMS, Where.PARENTED_TABLES, true},
                {When.ALWAYS, Where.OBJECT_FORMS, Where.STANDALONE_TABLES, true},
                {When.ALWAYS, Where.STANDALONE_TABLES, Where.OBJECT_FORMS, true},
                {When.ALWAYS, Where.STANDALONE_TABLES, Where.PARENTED_TABLES, true},
                {When.ALWAYS, Where.STANDALONE_TABLES, Where.STANDALONE_TABLES, false},
                {When.ALWAYS, Where.PARENTED_TABLES, Where.OBJECT_FORMS, true},
                {When.ALWAYS, Where.PARENTED_TABLES, Where.PARENTED_TABLES, false},
                {When.ALWAYS, Where.PARENTED_TABLES, Where.STANDALONE_TABLES, true},
                {When.ALWAYS, Where.ALL_TABLES, Where.OBJECT_FORMS, true},
                {When.ALWAYS, Where.ALL_TABLES, Where.PARENTED_TABLES, false},
                {When.ALWAYS, Where.ALL_TABLES, Where.STANDALONE_TABLES, false},
                });
    }

    public ObjectAssociationFiltersTest_visibleWhere(When when, Where where, Where context, boolean visible) {
        this.when = when;
        this.where = where;
        this.whereContext = context;
        this.expectedVisibility = visible;
    }
    
    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations(){{
            one(mockObjectAssociation).getFacet(HiddenFacet.class);
            will(returnValue(mockHiddenFacet));
            
            allowing(mockHiddenFacet).where();
            will(returnValue(where));

            allowing(mockHiddenFacet).when();
            will(returnValue(when));
        }});
    }
    
    @Test
    public void test() {
        final Filter<ObjectAssociation> filter = ObjectAssociationFilters.staticallyVisible(whereContext);
        assertThat(filter.accept(mockObjectAssociation), is(expectedVisibility));
    }

}
