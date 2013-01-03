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

import java.util.TreeSet;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator.FacetDecorator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.ExpressiveObjectsActions;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class ExpressiveObjectsMetaModelTest_shutdown {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    
    @Mock
    private ExpressiveObjectsConfiguration mockConfiguration;
    @Mock
    private ProgrammingModel mockProgrammingModel;
    @Mock
    private FacetDecorator mockFacetDecorator;
    @Mock
    private ClassSubstitutor mockClassSubstitutor;
    @Mock
    private CollectionTypeRegistry mockCollectionTypeRegistry;
    @Mock
    private RuntimeContext mockRuntimeContext;

    private ExpressiveObjectsMetaModel metaModel;

    @Before
    public void setUp() {
        expectingMetaModelToBeInitialized();
        metaModel = new ExpressiveObjectsMetaModel(mockRuntimeContext, mockProgrammingModel);
    }

    private void expectingMetaModelToBeInitialized() {
        final Sequence initSequence = context.sequence("init");
        context.checking(new Expectations() {
            {
                allowing(mockRuntimeContext).injectInto(with(any(Object.class)));
                will(ExpressiveObjectsActions.injectInto());
                
                one(mockRuntimeContext).setContainer(with(any(DomainObjectContainer.class)));
                inSequence(initSequence);
                
                one(mockProgrammingModel).init();
                inSequence(initSequence);
                
                one(mockProgrammingModel).getList();
                inSequence(initSequence);
                will(returnValue(Lists.newArrayList()));
                
                one(mockRuntimeContext).init();
                inSequence(initSequence);
            }
        });
    }

    @Test
    public void shouldSucceedWithoutThrowingAnyExceptions() {
        metaModel.init();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToChangeConfiguration() {
        metaModel.init();
        metaModel.setConfiguration(mockConfiguration);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToChangeProgrammingModelFacets() {
        metaModel.init();
        metaModel.setProgrammingModelFacets(mockProgrammingModel);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToChangeCollectionTypeRegistry() {
        metaModel.init();
        metaModel.setCollectionTypeRegistry(mockCollectionTypeRegistry);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToChangeClassSubstitutor() {
        metaModel.init();
        metaModel.setClassSubstitutor(mockClassSubstitutor);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToChangeFacetDecorators() {
        metaModel.init();
        metaModel.setFacetDecorators(new TreeSet<FacetDecorator>());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotBeAbleToAddToFacetDecorators() {
        metaModel.init();
        metaModel.getFacetDecorators().add(mockFacetDecorator);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToInitializeAgain() {
        metaModel.init();
        //
        metaModel.init();
    }

}
