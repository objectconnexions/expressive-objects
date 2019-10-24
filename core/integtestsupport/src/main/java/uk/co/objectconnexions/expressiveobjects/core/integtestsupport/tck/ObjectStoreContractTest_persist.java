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

package uk.co.objectconnexions.expressiveobjects.core.integtestsupport.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ObjectStoreSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryFindByTitle;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.SimpleEntity;

public abstract class ObjectStoreContractTest_persist {

    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder()
        .with(createPersistenceMechanismInstaller())
        .with(iswfListener()).build();

    
    /**
     * Mandatory hook.
     */
    protected abstract PersistenceMechanismInstaller createPersistenceMechanismInstaller();

    protected ExpressiveObjectsSystemWithFixtures.Listener iswfListener() {
        return null;
    }

    /**
     * hook method
     */
    protected void resetPersistenceStore() {
    }
    
    protected ObjectAdapter epv2Adapter;
    protected ObjectSpecification epvSpecification;

    protected ObjectStoreSpi getStore() {
        PersistenceSession psos = ExpressiveObjectsContext.getPersistenceSession();
        return (ObjectStoreSpi) psos.getObjectStore();
    }


    @Before
    public void setUpFixtures() throws Exception {
        epv2Adapter = iswf.adapterFor(iswf.fixtures.smpl2);
        epvSpecification = iswf.loadSpecification(SimpleEntity.class);
    }


    @Test
    public void getInstances_usingFindByTitle() throws Exception {

        // given nothing in DB
        resetPersistenceStore();
        
        // when search for any object
        boolean hasInstances = getStore().hasInstances(epvSpecification);
        
        // then find none
        assertFalse(hasInstances);
        
        // given now persisted
        final SimpleEntity epv2 = iswf.fixtures.smpl2;
        epv2.setName("foo");
        epv2.setDate(new Date());
        epv2.setNullable(1234567890L);
        epv2.setSize(123);
        
        iswf.persist(epv2);

        iswf.bounceSystem();
        
        // when search for object
        List<ObjectAdapter> retrievedInstance = getStore().loadInstancesAndAdapt(new PersistenceQueryFindByTitle(epvSpecification, epv2Adapter.titleString()));
        
        // then find
        assertEquals(1, retrievedInstance.size());
        final ObjectAdapter retrievedAdapter = retrievedInstance.get(0);

        assertNotSame(epv2Adapter, retrievedAdapter);
        assertEquals(((SimpleEntity)epv2Adapter.getObject()).getName(), ((SimpleEntity)retrievedAdapter.getObject()).getName());
        assertEquals(epv2Adapter.getOid(), retrievedAdapter.getOid());

        // and when search for some other title
        retrievedInstance = getStore().loadInstancesAndAdapt(new PersistenceQueryFindByTitle(epvSpecification, "some other title"));
        
        // then don't find
        assertEquals(0, retrievedInstance.size());
    }


    @Test
    public void updateInstance() throws Exception {

        // given persisted
        resetPersistenceStore();
        ObjectAdapter adapter = iswf.persist(iswf.fixtures.smpl2);
        final RootOid oid = (RootOid) adapter.getOid();
        iswf.bounceSystem();
        
        // when change
        adapter = iswf.reload(oid);
        
        SimpleEntity epv = (SimpleEntity) adapter.getObject();
        epv.setName("changed");

        iswf.bounceSystem();

        // then found
        List<ObjectAdapter> retrievedInstance = getStore().loadInstancesAndAdapt(new PersistenceQueryFindByTitle(epvSpecification, adapter.titleString()));
        assertEquals(1, retrievedInstance.size());
        
        final ObjectAdapter retrievedAdapter = retrievedInstance.get(0);
        assertNotSame(adapter, retrievedAdapter);
        assertEquals(((SimpleEntity)adapter.getObject()).getName(), ((SimpleEntity)retrievedAdapter.getObject()).getName());
        assertEquals(adapter.getOid(), retrievedAdapter.getOid());
    }

    @Test
    public void removeInstance() throws Exception {

        // given persisted
        resetPersistenceStore();
        ObjectAdapter adapter = iswf.persist(iswf.fixtures.smpl2);
        final RootOid oid = (RootOid) adapter.getOid();
        iswf.bounceSystem();

        // when destroy
        adapter = iswf.reload(oid);
        
        SimpleEntity epv = (SimpleEntity) adapter.getObject();
        iswf.destroy(epv);
        iswf.bounceSystem();

        // then not found
        assertEquals(false, getStore().hasInstances(epvSpecification));
    }
}
