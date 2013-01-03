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

package uk.co.objectconnexions.expressiveobjects.viewer.html.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.ActionException;

public class ContextTest_mapAction {

    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder().build();

    private Context context;


    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);
        context = new Context(null);
    }


    @Test
    public void getMappedAction_forNonExistentId() {
        try {
            assertNull(context.getMappedAction("NON-EXISTENT-ID"));
            fail();
        } catch (final ActionException ex) {
            // expected
        }
    }


    @Test
    public void mapAction_then_getMappedAction() {
        final ObjectAction action = new ObjectActionNoop();
        final String id = context.mapAction(action);
        assertEquals(action, context.getMappedAction(id));
    }


    @Test
    public void mapAction_returnsSameIdForSameAction() {
        final ObjectAction action = new ObjectActionNoop();
        final String id = context.mapAction(action);
        final String id2 = context.mapAction(action);
        assertEquals(id, id2);
    }

    @Test
    public void mapAction_returnsDifferentIdsForDifferentActions() {
        final String id = context.mapAction(new ObjectActionNoop());
        final String id2 = context.mapAction(new ObjectActionNoop());
        assertNotSame(id, id2);
    }

    
    private PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    private AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }

}
