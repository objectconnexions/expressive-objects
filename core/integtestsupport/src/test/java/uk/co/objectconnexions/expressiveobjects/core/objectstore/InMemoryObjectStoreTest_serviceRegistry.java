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

package uk.co.objectconnexions.expressiveobjects.core.objectstore;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.InMemoryObjectStore;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ParentEntityRepository;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.SimpleEntity;

public class InMemoryObjectStoreTest_serviceRegistry {

    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder().build();
    
    protected ObjectAdapter epv2Adapter;
    protected ObjectSpecification epvSpecification;

    protected InMemoryObjectStore getStore() {
        return (InMemoryObjectStore) ExpressiveObjectsContext.getPersistenceSession().getObjectStore();
    }

    @Before
    public void setUpFixtures() throws Exception {
        epv2Adapter = iswf.adapterFor(iswf.fixtures.smpl2);
        epvSpecification = iswf.loadSpecification(SimpleEntity.class);
    }

    @Test
    public void getOidForServices() throws Exception {
        final Oid oidForService = getStore().getOidForService(iswf.loadSpecification(ParentEntityRepository.class));
        assertEquals(RootOidDefault.create(ObjectSpecId.of("ParentEntities"), "1"), oidForService);
    }


//    @Test
//    public void registerService_canBeRetrieved() throws Exception {
//        registerService14();
//
//        final Oid oidForService = store.getOidForService(serviceSpecification);
//        assertEquals(oid14, oidForService);
//    }
//
//    @Test
//    public void testCantRegisterServiceMoreThanOnce() throws Exception {
//        registerService14();
//        try {
//            registerService14();
//            fail();
//        } catch (final ExpressiveObjectsException expected) {
//        }
//    }
//
//    @Test
//    public void testCanRegisterMoreThanOneService() throws Exception {
//        registerService14();
//        registerService15();
//    }
//
//
//    private void resetIdentityMap() {
//        ExpressiveObjectsContext.getPersistenceSession().testReset();
//    }
//
//    protected void addObjectToStore(final ObjectAdapter object) {
//        final PersistenceCommand command = store.createCreateObjectCommand(object);
//        assertEquals(object, command.onObject());
//        store.execute(Collections.<PersistenceCommand> singletonList(command));
//    }
//
//    private RootOidDefault registerService14() {
//        return oid14 = registerService(""+14);
//    }
//
//    private RootOidDefault registerService15() {
//        return registerService(""+15);
//    }
//
//    private RootOidDefault registerService(final String id) {
//        final RootOidDefault oid = RootOidDefault.createPersistent("SVC", id);
//        store.registerService(oid);
//        resetIdentityMap();
//        return oid;
//    }

    
}
