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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system.context;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactory;

/**
 * A specialised ExpressiveObjectsContext implementation that provides two sets of
 * components: one for the server; and one for the client. This simply
 * determines the current thread and if that thread is the server thread then it
 * provides server data. For any other thread the client data is used.
 */
public class ExpressiveObjectsContextPipe extends ExpressiveObjectsContextMultiUser {

    public static ExpressiveObjectsContext createInstance(final ExpressiveObjectsSessionFactory sessionFactory) {
        return new ExpressiveObjectsContextPipe(sessionFactory);
    }

    private ExpressiveObjectsSession clientSession;
    private ExpressiveObjectsSession serverSession;

    private Thread server;

    // ///////////////////////////////////////////////////
    // Constructor
    // ///////////////////////////////////////////////////

    private ExpressiveObjectsContextPipe(final ExpressiveObjectsSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    // ///////////////////////////////////////////////////
    // Server (not API)
    // ///////////////////////////////////////////////////

    public void setServer(final Thread server) {
        this.server = server;
    }

    private boolean isCurrentThreadServer() {
        return Thread.currentThread() == server;
    }

    // ///////////////////////////////////////////////////
    // getCurrent() Hook
    // ///////////////////////////////////////////////////

    @Override
    protected ExpressiveObjectsSession getSessionInstance(final String sessionId) {
        return null;
    }

    @Override
    public ExpressiveObjectsSession getSessionInstance() {
        if (isCurrentThreadServer()) {
            return serverSession;
        } else {
            return clientSession;
        }
    }

    @Override
    public ExpressiveObjectsSession openSessionInstance(final AuthenticationSession authenticationSession) {
        applySessionClosePolicy();
        final ExpressiveObjectsSession newSession = getSessionFactoryInstance().openSession(authenticationSession);
        if (isCurrentThreadServer()) {
            serverSession = newSession;
        } else {
            clientSession = newSession;
        }
        return newSession;
    }

    // ///////////////////////////////////////////////////
    // shutdown
    // ///////////////////////////////////////////////////

    @Override
    public void closeAllSessionsInstance() {
    }

    // ///////////////////////////////////////////////////
    // Execution Context Ids
    // ///////////////////////////////////////////////////

    @Override
    public String[] allSessionIds() {
        return new String[] { clientSession.getId(), serverSession.getId() };
    }

    // ///////////////////////////////////////////////////
    // Debugging
    // ///////////////////////////////////////////////////

    @Override
    public String debugTitle() {
        return "Expressive Objects (pipe) " + Thread.currentThread().getName();
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        super.debugData(debug);
        debug.appendln("Server thread", server);
    }

}
