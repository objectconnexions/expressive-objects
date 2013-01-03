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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Persistability;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.NotPersistableException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adapter.PojoAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm.PersistAlgorithm;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm.ToPersistObjectSet;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PojoAdapterBuilder;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PojoAdapterBuilder.Persistence;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PojoAdapterBuilder.Type;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public abstract class PersistAlgorithmContractTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ToPersistObjectSet mockAdder;

    @Mock
    private ObjectSpecification objectSpec;

    @Mock
    private AdapterManager mockObjectAdapterLookup;
    
    private PersistAlgorithm persistAlgorithm;

    

    @Before
    public void setUp() throws Exception {
        persistAlgorithm = createPersistAlgorithm();
    }

    /**
     * Hook for any implementation to implement.
     * 
     * @return
     */
    protected abstract PersistAlgorithm createPersistAlgorithm();

    @Test
    public void makePersistent_skipsValueObjects() {
        
        context.checking(new Expectations() {
            {
                allowing(objectSpec).isParentedOrFreeCollection();
                will(returnValue(false));

                allowing(objectSpec).persistability();
                will(returnValue(Persistability.USER_PERSISTABLE));
                
                never(mockAdder);
            }
        });

        final PojoAdapter valueAdapter = PojoAdapterBuilder.create().with(Type.VALUE).with(objectSpec).build();
        persistAlgorithm.makePersistent(valueAdapter, mockAdder);
    }

    
    @Test(expected=NotPersistableException.class)
    public void makePersistent_failsIfObjectIsAggregated() {
        final PojoAdapter rootAdapter = PojoAdapterBuilder.create().with(Type.ROOT).with(Persistence.TRANSIENT).with(objectSpec).build();
        context.checking(new Expectations() {
            {
                allowing(objectSpec).isService();
                will(returnValue(false));
                
                allowing(objectSpec).isParentedOrFreeCollection();
                will(returnValue(false));

                allowing(objectSpec).persistability();
                will(returnValue(Persistability.USER_PERSISTABLE));
    
                allowing(mockObjectAdapterLookup).getAdapterFor(with(any(Oid.class)));
                will(returnValue(rootAdapter));
                
                never(mockAdder);
            }
        });
        

        final PojoAdapter aggregatedAdapter = PojoAdapterBuilder.create().with(Type.AGGREGATED).with(Persistence.TRANSIENT).with(objectSpec).with(mockObjectAdapterLookup).build();
        persistAlgorithm.makePersistent(aggregatedAdapter, mockAdder);
    }

    @Test(expected=NotPersistableException.class)
    public void makePersistent_failsIfObjectAlreadyPersistent() {
        context.checking(new Expectations() {
            {
                allowing(objectSpec).isService();
                will(returnValue(false));

                allowing(objectSpec).isParentedOrFreeCollection();
                will(returnValue(false));

                allowing(objectSpec).persistability();
                will(returnValue(Persistability.PROGRAM_PERSISTABLE));
                
                never(mockAdder);
            }
        });
        
        final PojoAdapter rootAdapter = PojoAdapterBuilder.create().with(Type.ROOT).with(Persistence.PERSISTENT).with(objectSpec).build();
        persistAlgorithm.makePersistent(rootAdapter, mockAdder);
    }
    
    
    @Test(expected=NotPersistableException.class)
    public void makePersistent_failsIfObjectMustBeTransient() {
        context.checking(new Expectations() {
            {
                allowing(objectSpec).isService();
                will(returnValue(false));

                allowing(objectSpec).isParentedOrFreeCollection();
                will(returnValue(false));

                allowing(objectSpec).persistability();
                will(returnValue(Persistability.TRANSIENT));
                
                never(mockAdder);
            }
        });
        
        final PojoAdapter rootAdapter = PojoAdapterBuilder.create().with(Type.ROOT).with(Persistence.TRANSIENT).with(objectSpec).build();
        persistAlgorithm.makePersistent(rootAdapter, mockAdder);
    }
}
