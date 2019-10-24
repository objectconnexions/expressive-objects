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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction;

import java.util.Collections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.matchers.ExpressiveObjectsMatchers;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectPersistenceException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ObjectStoreSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.CreateObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.DestroyObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommandContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.SaveObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PojoAdapterBuilder.Persistence;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.MessageBroker;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.UpdateNotifier;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class TransactionTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    private ExpressiveObjectsTransaction transaction;

    private ObjectAdapter transientAdapter1;
    private ObjectAdapter transientAdapter2;

    private ObjectAdapter persistentAdapter1;
    private ObjectAdapter persistentAdapter2;

    @Mock
    private ObjectStoreSpi mockObjectStore;

    @Mock
    private ExpressiveObjectsTransactionManager mockTransactionManager;
    @Mock
    private MessageBroker mockMessageBroker;
    @Mock
    private UpdateNotifier mockUpdateNotifier;

    private PersistenceCommand command;
    private PersistenceCommand command2;
    private PersistenceCommand command3;

    private CreateObjectCommand createCreateCommand(final ObjectAdapter object, final String name) {
        return new CreateObjectCommand() {

            @Override
            public void execute(final PersistenceCommandContext context) throws ObjectPersistenceException {
            }

            @Override
            public ObjectAdapter onAdapter() {
                return object;
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    private DestroyObjectCommand createDestroyCommand(final ObjectAdapter object, final String name) {
        return new DestroyObjectCommand() {

            @Override
            public void execute(final PersistenceCommandContext context) throws ObjectPersistenceException {
            }

            @Override
            public ObjectAdapter onAdapter() {
                return object;
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    private SaveObjectCommand createSaveCommand(final ObjectAdapter object, final String name) {
        return new SaveObjectCommand() {
            @Override
            public void execute(final PersistenceCommandContext context) throws ObjectPersistenceException {
            }

            @Override
            public ObjectAdapter onAdapter() {
                return object;
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    private SaveObjectCommand createSaveCommandThatAborts(final ObjectAdapter object, final String name) {
        return new SaveObjectCommand() {
            @Override
            public void execute(final PersistenceCommandContext context) throws ObjectPersistenceException {
                throw new ObjectPersistenceException();
            }

            @Override
            public ObjectAdapter onAdapter() {
                return object;
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        transaction = new ExpressiveObjectsTransaction(mockTransactionManager, mockMessageBroker, mockUpdateNotifier, mockObjectStore);
        
        transientAdapter1 = PojoAdapterBuilder.create().with(Persistence.TRANSIENT).withIdentifier("1").build();
        transientAdapter2 = PojoAdapterBuilder.create().with(Persistence.TRANSIENT).withIdentifier("2").build();
        persistentAdapter1 = PojoAdapterBuilder.create().with(Persistence.PERSISTENT).withIdentifier("3").build();
        persistentAdapter2 = PojoAdapterBuilder.create().with(Persistence.PERSISTENT).withIdentifier("4").build();
    }
    
    @Test
    public void abort_neverDelegatesToObjectStore() throws Exception {

        command = createSaveCommand(transientAdapter1, "command 1");
        command2 = createSaveCommand(transientAdapter2, "command 2");

        context.checking(new Expectations() {
            {
                never(mockObjectStore);
            }
        });

        transaction.addCommand(command);
        transaction.addCommand(command2);
        transaction.abort();
    }


    @Test
    public void commit_delegatesToObjectStoreToExecutesAllCommands() throws Exception {

        command = createSaveCommand(transientAdapter1, "command 1");
        command2 = createSaveCommandThatAborts(transientAdapter2, "command 2");

        context.checking(new Expectations() {
            {
                one(mockObjectStore).execute(with(ExpressiveObjectsMatchers.listContainingAll(command, command2)));
            }
        });
        
        transaction.addCommand(command);
        transaction.addCommand(command2);
        transaction.commit();
    }

    @Test
    public void commit_disregardsSecondSaveCommandOnSameAdapter() throws Exception {

        command = createSaveCommand(persistentAdapter1, "command 1");
        command2 = createSaveCommand(persistentAdapter1, "command 2");

        context.checking(new Expectations() {
            {
                one(mockObjectStore).execute(with(ExpressiveObjectsMatchers.listContainingAll(command)));
            }
        });
        
        transaction.addCommand(command);
        transaction.addCommand(command2);
        transaction.commit();
    }


    @Test
    public void commit_disregardsSaveCommandsForObjectBeingCreated() throws Exception {

        command = createCreateCommand(transientAdapter1, "command 1");
        command2 = createSaveCommandThatAborts(transientAdapter1, "command 2");

        context.checking(new Expectations() {
            {
                one(mockObjectStore).execute(with(ExpressiveObjectsMatchers.listContainingAll(command)));
            }
        });
        
        transaction.addCommand(command);
        transaction.addCommand(command2);
        transaction.commit();
    }

    @Test
    public void commit_destroyCausesPrecedingSaveCommandsToBeDisregarded() throws Exception {

        command = createSaveCommand(persistentAdapter1, "command 1");
        command2 = createSaveCommand(persistentAdapter2, "command 2");
        command3 = createDestroyCommand(persistentAdapter1, "command 3");

        context.checking(new Expectations() {
            {
                one(mockObjectStore).execute(with(ExpressiveObjectsMatchers.listContainingAll(command2, command3)));
            }
        });
        
        transaction.addCommand(command);
        transaction.addCommand(command2);
        transaction.addCommand(command3);
        transaction.commit();
    }

    @Test
    public void commit_ignoresBothCreateAndDestroyCommandsWhenForSameObject() throws Exception {

        command = createSaveCommand(persistentAdapter1, "command 1");
        command2 = createSaveCommand(persistentAdapter2, "command 2");
        command3 = createDestroyCommand(persistentAdapter1, "command 3");

        context.checking(new Expectations() {
            {
                one(mockObjectStore).execute(with(ExpressiveObjectsMatchers.listContainingAll(command2)));
            }
        });
        
        transaction.addCommand(command);
        transaction.addCommand(command2);
        transaction.addCommand(command3);
        transaction.commit();
    }


    @Test
    public void commit_testNoCommands() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockObjectStore).execute(with(Collections.<PersistenceCommand>emptyList()));
            }
        });

        transaction.commit();
    }


    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfAttemptToAbortAnAlreadyAbortedTransaction() throws Exception {
        transaction.abort();

        transaction.abort();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfAttemptToCommitAnAlreadyAbortedTransaction() throws Exception {
        transaction.abort();

        transaction.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfAttemptToAbortAnAlreadyCommitedTransaction() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockObjectStore).execute(with(Collections.<PersistenceCommand>emptyList()));
            }
        });

        transaction.commit();

        transaction.abort();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfAttemptToCommitAnAlreadyCommitedTransaction() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockObjectStore).execute(with(Collections.<PersistenceCommand>emptyList()));
            }
        });
        transaction.commit();

        transaction.commit();
    }
}
