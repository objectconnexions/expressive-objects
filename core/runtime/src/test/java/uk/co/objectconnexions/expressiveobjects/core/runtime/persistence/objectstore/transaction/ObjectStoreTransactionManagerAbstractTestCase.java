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

import org.jmock.Expectations;
import org.junit.Rule;

import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.TransactionalResource;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public abstract class ObjectStoreTransactionManagerAbstractTestCase {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    protected ExpressiveObjectsTransactionManager transactionManager;
    @Mock
    protected ExpressiveObjectsSession mockSession;
    @Mock
    protected PersistenceSession mockPersistenceSession;
    @Mock
    protected TransactionalResource mockObjectStore;


    // //////////////////////////////////////////////////
    // Helpers
    // //////////////////////////////////////////////////

    protected void ignoreCallsToPersistenceSession() {
        context.checking(new Expectations() {
            {
                ignoring(mockPersistenceSession);
            }
        });
    }

    protected void ignoreCallsToObjectStore() {
        context.checking(new Expectations() {
            {
                ignoring(mockObjectStore);
            }
        });
    }

}
