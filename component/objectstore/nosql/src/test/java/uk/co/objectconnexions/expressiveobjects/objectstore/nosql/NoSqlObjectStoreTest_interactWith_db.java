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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.OidGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceQuery;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlIdentifierGenerator;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlObjectStore;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.NoSqlDataDatabase;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.encryption.DataEncryption;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.versions.VersionCreator;

public class NoSqlObjectStoreTest_interactWith_db {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    @Mock
    private NoSqlDataDatabase db;
    
    @Mock
    private VersionCreator versionCreator;

    @Mock
    private PersistenceCommand command;

    @Mock
    private ObjectSpecification cusSpecification;

    @Mock
    private ObjectSpecification serviceSpecification;

    @Mock
    private ObjectAdapter cusAdapter;

    private final ObjectSpecId cusSpecId = ObjectSpecId.of("CUS");
    private RootOid cusRootOid = RootOidDefault.create(cusSpecId, "3");

    final ObjectSpecId serviceSpecId = ObjectSpecId.of("service");

    private Map<String, DataEncryption> dataEncrypter = Maps.newHashMap();

    private NoSqlObjectStore store;





    @Before
    public void setup() {
        Logger.getRootLogger().setLevel(Level.OFF);

        context.checking(new Expectations() {
            {
                // in the constructor of the object store
                one(db).open();
                one(db).containsData();
                will(returnValue(false));
                one(db).close();

                allowing(cusAdapter).getOid();
                will(returnValue(cusRootOid));

                allowing(cusAdapter).getSpecification();
                will(returnValue(cusSpecification));

                allowing(cusSpecification).getSpecId();
                will(returnValue(cusSpecId));

                allowing(serviceSpecification).getSpecId();
                will(returnValue(serviceSpecId));
            }
        });

        store = new NoSqlObjectStore(db, new OidGenerator(new NoSqlIdentifierGenerator(db)), versionCreator, null, dataEncrypter);
    }

    @Test
    public void open() throws Exception {
        context.checking(new Expectations() {
            {
                one(db).open();
            }
        });
        store.open();
    }

    @Test
    public void close() throws Exception {
        context.checking(new Expectations() {
            {
                one(db).close();
            }
        });
        store.close();
    }

    @Test
    public void registerService() throws Exception {
        context.checking(new Expectations() {
            {
                one(db).addService(serviceSpecId, "4");
            }
        });
        store.registerService(RootOidDefault.create(serviceSpecId, "4"));
    }

    @Test
    public void oidForService() throws Exception {
        context.checking(new Expectations() {
            {
                one(db).getService(serviceSpecId);
                will(returnValue("4"));
            }
        });
        store.getOidForService(serviceSpecification);
    }

    @Test
    public void hasInstances() throws Exception {
        context.checking(new Expectations() {
            {
                one(db).hasInstances(cusSpecification.getSpecId());
                will(returnValue(true));
            }
        });
        store.hasInstances(cusSpecification);
    }

    @Test
    public void execute() throws Exception {
        final List<PersistenceCommand> commands = new ArrayList<PersistenceCommand>();
        commands.add(command);

        context.checking(new Expectations() {
            {
                // Hone(command).execute(null); // REVIEW: DKH ... how was this expectation ever met?
                one(db).write(commands);
            }
        });

        store.execute(commands);
    }

    @Test
    public void instances() throws Exception {
        final PersistenceQuery persistenceQuery = context.mock(PersistenceQuery.class);
        context.checking(new Expectations() {
            {
                one(persistenceQuery).getSpecification();
                will(returnValue(cusSpecification));

                one(db).instancesOf(cusSpecification.getSpecId());
                will(returnIterator());
                
                allowing(cusSpecification).subclasses();
            }
        });

        store.loadInstancesAndAdapt(persistenceQuery);
    }

    @Test
    public void resolve() throws Exception {
        final Sequence changingState = context.sequence("changingState");
        context.checking(new Expectations() {
            {
                one(db).getInstance("3", cusSpecId);

                allowing(cusAdapter).getResolveState();
                inSequence(changingState);
                will(returnValue(ResolveState.GHOST));

                ignoring(cusSpecification);
                
                one(cusAdapter).changeState(ResolveState.RESOLVING);
                inSequence(changingState);
                one(cusAdapter).changeState(ResolveState.RESOLVED);
                inSequence(changingState);
                
                one(cusAdapter).setVersion(null);
            }
        });
        store.resolveImmediately(cusAdapter);
    }
    
}
