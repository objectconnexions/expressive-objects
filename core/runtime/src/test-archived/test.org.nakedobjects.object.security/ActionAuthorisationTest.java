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


package test.uk.co.objectconnexions.expressiveobjects.object.security;

import junit.framework.TestCase;

import uk.co.objectconnexions.expressiveobjects.nof.core.context.StaticContext;
import uk.co.objectconnexions.expressiveobjects.testing.NullSession;

import test.uk.co.objectconnexions.expressiveobjects.object.reflect.DummyActionPeer;


public class ActionAuthorisationTest extends TestCase {

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ActionAuthorisationTest.class);
    }

    private MockAuthorisationManager manager;
    private ActionAuthorisation action;

    protected void setUp() throws Exception {
        StaticContext.createInstance();
        DummyActionPeer peer = new DummyActionPeer();
        manager = new MockAuthorisationManager();
        action = new ActionAuthorisation(peer, manager);
    }

    public void testIsAccessible() {
        manager.setupUsable(true);
        manager.setupVisible(true);

        assertTrue(action.isVisibleForSession(new NullSession()));
    }

    public void testIsNotAccessible() {
        manager.setupUsable(true);
        manager.setupVisible(false);

        assertFalse(action.isVisibleForSession(new NullSession()));
    }
}
