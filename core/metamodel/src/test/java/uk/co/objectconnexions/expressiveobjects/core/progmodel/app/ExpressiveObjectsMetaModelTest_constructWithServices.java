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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.app;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.matchers.ExpressiveObjectsMatchers;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class ExpressiveObjectsMetaModelTest_constructWithServices {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private RuntimeContext mockContext;

    @Mock
    private ProgrammingModel mockProgrammingModel;

    @Mock
    private SomeRepo mockService1;

    @Mock
    private SomeOtherRepo mockService2;

    private ExpressiveObjectsMetaModel metaModel;

    private static class SomeRepo {}
    private static class SomeOtherRepo {}
    
    @Test
    public void shouldSucceedWithoutThrowingAnyExceptions() {
        metaModel = new ExpressiveObjectsMetaModel(mockContext, mockProgrammingModel);
    }

    @Test
    public void shouldBeAbleToRegisterServices() {
        metaModel = new ExpressiveObjectsMetaModel(mockContext, mockProgrammingModel, mockService1, mockService2);
        final List<Object> services = metaModel.getServices();
        assertThat(services.size(), is(2));
        assertThat(services, ExpressiveObjectsMatchers.containsObjectOfType(SomeRepo.class));
        assertThat(services, ExpressiveObjectsMatchers.containsObjectOfType(SomeOtherRepo.class));
    }


}
