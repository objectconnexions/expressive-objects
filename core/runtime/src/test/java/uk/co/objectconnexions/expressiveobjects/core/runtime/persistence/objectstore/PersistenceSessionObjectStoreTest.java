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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.matchers.ExpressiveObjectsMatchers;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapterFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.Version;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServicesInjectorDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.container.DomainObjectContainerDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.app.ExpressiveObjectsMetaModel;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adapter.PojoAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adapter.PojoAdapterFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adaptermanager.AdapterManagerDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adaptermanager.PojoRecreatorDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.internal.RuntimeContextFromSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ObjectStoreSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm.PersistAlgorithm;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.CreateObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.DestroyObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PojoAdapterBuilder;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.SaveObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PojoAdapterBuilder.Persistence;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGeneratorDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.ObjectFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.OidGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.progmodels.dflt.ProgrammingModelFacetsJava5;

public class PersistenceSessionObjectStoreTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private ServicesInjectorDefault servicesInjector;
    private AdapterManagerSpi adapterManager;
    private ObjectAdapterFactory adapterFactory;
    
    
    private PersistenceSession persistenceSession;
    private ExpressiveObjectsTransactionManager transactionManager;
    
    private ObjectAdapter persistentAdapter;
    private PojoAdapter transientAdapter;
    
    @Mock
    private PersistenceSessionFactory mockPersistenceSessionFactory;
    
    @Mock
    private ObjectStoreSpi mockObjectStore;
    @Mock
    private ObjectFactory objectFactory;
    @Mock
    private PersistAlgorithm mockPersistAlgorithm;

    @Mock
    private CreateObjectCommand createObjectCommand;
    @Mock
    private SaveObjectCommand saveObjectCommand;
    @Mock
    private DestroyObjectCommand destroyObjectCommand;

    @Mock
    private Version mockVersion;

    @Mock
    private RuntimeContext mockRuntimeContext;

    @Mock
    private ExpressiveObjectsConfiguration mockConfiguration;
    
    
    private ExpressiveObjectsMetaModel expressiveObjectsMetaModel;



    public static class Customer {
    }

    public static class CustomerRepository {
        public Customer x() {return null;}
    }
    
    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        context.ignoring(mockRuntimeContext);
        context.ignoring(mockConfiguration);

        expressiveObjectsMetaModel = new ExpressiveObjectsMetaModel(mockRuntimeContext, new ProgrammingModelFacetsJava5(), new CustomerRepository());
        expressiveObjectsMetaModel.init();
        
        context.checking(new Expectations() {
            {
                ignoring(mockObjectStore).open();
                ignoring(mockObjectStore).close();
                ignoring(mockObjectStore).name();
                ignoring(mockPersistAlgorithm).name();
                
                ignoring(createObjectCommand);
                ignoring(saveObjectCommand);
                ignoring(destroyObjectCommand);
                ignoring(mockVersion);
            }
        });

        final RuntimeContextFromSession runtimeContext = new RuntimeContextFromSession();
        final DomainObjectContainerDefault container = new DomainObjectContainerDefault();

        runtimeContext.injectInto(container);
        runtimeContext.setContainer(container);

        servicesInjector = new ServicesInjectorDefault();
        servicesInjector.setContainer(container);

        adapterManager = new AdapterManagerDefault(new PojoRecreatorDefault());
        adapterFactory = new PojoAdapterFactory();
        persistenceSession = new PersistenceSession(mockPersistenceSessionFactory, adapterFactory, objectFactory, servicesInjector, new OidGenerator(new IdentifierGeneratorDefault()), adapterManager, mockPersistAlgorithm, mockObjectStore) {
            @Override
            protected SpecificationLoaderSpi getSpecificationLoader() {
                return expressiveObjectsMetaModel.getSpecificationLoader();
            }
            
        };
        
        transactionManager = new ExpressiveObjectsTransactionManager(persistenceSession, mockObjectStore);
        persistenceSession.setTransactionManager(transactionManager);

        servicesInjector.setServices(Collections.emptyList());

        persistentAdapter = PojoAdapterBuilder.create().withOid("CUS|1").withPojo(new Customer()).with(Persistence.PERSISTENT).with(mockVersion).with(expressiveObjectsMetaModel.getSpecificationLoader()).build();
        transientAdapter = PojoAdapterBuilder.create().withOid("CUS|2").withPojo(new Customer()).with(Persistence.TRANSIENT).with(expressiveObjectsMetaModel.getSpecificationLoader()).build();
    }


    @Test
    public void destroyObjectThenAbort() {
        
        final Sequence tran = context.sequence("tran");
        context.checking(new Expectations() {
            {
                one(mockObjectStore).startTransaction();
                inSequence(tran);

                one(mockObjectStore).createDestroyObjectCommand(persistentAdapter);
                inSequence(tran);

                one(mockObjectStore).abortTransaction();
                inSequence(tran);
            }
        });
        
        transactionManager.startTransaction();
        persistenceSession.destroyObject(persistentAdapter);
        transactionManager.abortTransaction();
    }

    @Test
    public void destroyObject_thenCommit() {

        final Sequence tran = context.sequence("tran");
        context.checking(new Expectations() {
            {
                one(mockObjectStore).startTransaction();
                inSequence(tran);

                one(mockObjectStore).createDestroyObjectCommand(persistentAdapter);
                inSequence(tran);
                will(returnValue(destroyObjectCommand));
                
                one(mockObjectStore).execute(with(ExpressiveObjectsMatchers.listContaining((PersistenceCommand)destroyObjectCommand)));
                inSequence(tran);

                one(mockObjectStore).endTransaction();
                inSequence(tran);
            }

        });

        transactionManager.startTransaction();
        persistenceSession.destroyObject(persistentAdapter);
        transactionManager.endTransaction();
    }

    @Test
    public void makePersistent_happyCase() {

        final Sequence tran = context.sequence("tran");
        context.checking(new Expectations() {
            {
                one(mockObjectStore).startTransaction();
                inSequence(tran);
                one(mockPersistAlgorithm).makePersistent(transientAdapter, persistenceSession);
                inSequence(tran);
                one(mockObjectStore).execute(with(equalTo(Collections.<PersistenceCommand>emptyList())));
                inSequence(tran);
                one(mockObjectStore).endTransaction();
                inSequence(tran);
            }
        });

        transactionManager.startTransaction();
        persistenceSession.makePersistent(transientAdapter);
        transactionManager.endTransaction();
    }
}
