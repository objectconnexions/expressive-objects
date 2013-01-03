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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactory;

/**
 * Basic multi-user implementation of Expressive Objects that stores a set of components for
 * each thread in use.
 */
public class ExpressiveObjectsContextThreadLocal extends ExpressiveObjectsContextMultiUser {

    private static final Logger LOG = Logger.getLogger(ExpressiveObjectsContextThreadLocal.class);

    public static ExpressiveObjectsContext createInstance(final ExpressiveObjectsSessionFactory sessionFactory) {
        return new ExpressiveObjectsContextThreadLocal(sessionFactory);
    }

    private final Map<Thread, ExpressiveObjectsSession> sessionsByThread = new HashMap<Thread, ExpressiveObjectsSession>();

    protected ExpressiveObjectsContextThreadLocal(final ExpressiveObjectsSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    // /////////////////////////////////////////////////////////
    // Session
    // /////////////////////////////////////////////////////////

    @Override
    public void closeAllSessionsInstance() {
        shutdownAllThreads();
    }

    protected void shutdownAllThreads() {
        synchronized (sessionsByThread) {
            int i = 0;
            for (final Thread thread : sessionsByThread.keySet()) {
                LOG.info("Shutting down thread: " + i++);
                final ExpressiveObjectsSession data = sessionsByThread.get(thread);
                data.closeAll();
            }
        }
    }

    @Override
    protected void doClose() {
        sessionsByThread.remove(Thread.currentThread());
    }

    // /////////////////////////////////////////////////////////
    // Execution Context Ids
    // /////////////////////////////////////////////////////////

    @Override
    public String[] allSessionIds() {
        final String[] ids = new String[sessionsByThread.size()];
        int i = 0;
        for (final Thread thread : sessionsByThread.keySet()) {
            final ExpressiveObjectsSession data = sessionsByThread.get(thread);
            ids[i++] = data.getId();
        }
        return ids;
    }

    // /////////////////////////////////////////////////////////
    // Debugging
    // /////////////////////////////////////////////////////////

    @Override
    public String debugTitle() {
        return "Expressive Objects (by thread) " + Thread.currentThread().getName();
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        super.debugData(debug);
        debug.appendTitle("Threads based Contexts");
        for (final Thread thread : sessionsByThread.keySet()) {
            final ExpressiveObjectsSession data = sessionsByThread.get(thread);
            debug.appendln(thread.toString(), data);
        }
    }

    @Override
    protected ExpressiveObjectsSession getSessionInstance(final String executionContextId) {
        for (final Thread thread : sessionsByThread.keySet()) {
            final ExpressiveObjectsSession data = sessionsByThread.get(thread);
            if (data.getId().equals(executionContextId)) {
                return data;
            }
        }
        return null;
    }

    // /////////////////////////////////////////////////////////
    // open, close
    // /////////////////////////////////////////////////////////

    /**
     * Is only intended to be called through
     * {@link ExpressiveObjectsContext#openSession(AuthenticationSession)}.
     * 
     * <p>
     * Implementation note: an alternative design would have just been to bind
     * onto a thread local.
     */
    @Override
    public ExpressiveObjectsSession openSessionInstance(final AuthenticationSession authenticationSession) {
        final Thread thread = Thread.currentThread();
        synchronized (sessionsByThread) {
            applySessionClosePolicy();
            final ExpressiveObjectsSession session = getSessionFactoryInstance().openSession(authenticationSession);
            LOG.debug("  opening session " + session + " (count " + sessionsByThread.size() + ") for " + authenticationSession.getUserName());
            saveSession(thread, session);
            session.open();
            return session;
        }
    }

    protected ExpressiveObjectsSession createAndOpenSession(final Thread thread, final AuthenticationSession authenticationSession) {
        final ExpressiveObjectsSession session = getSessionFactoryInstance().openSession(authenticationSession);
        session.open();
        LOG.info("  opening session " + session + " (count " + sessionsByThread.size() + ") for " + authenticationSession.getUserName());
        return session;
    }

    private ExpressiveObjectsSession saveSession(final Thread thread, final ExpressiveObjectsSession session) {
        synchronized (sessionsByThread) {
            sessionsByThread.put(thread, session);
        }
        LOG.debug("  saving session " + session + "; now have " + sessionsByThread.size() + " sessions");
        return session;
    }

    // /////////////////////////////////////////////////////////
    // getCurrent() (Hook)
    // /////////////////////////////////////////////////////////

    /**
     * Get {@link ExpressiveObjectsSession execution context} used by the current thread.
     * 
     * @see #openSessionInstance(AuthenticationSession)
     */
    @Override
    public ExpressiveObjectsSession getSessionInstance() {
        final Thread thread = Thread.currentThread();
        final ExpressiveObjectsSession session = sessionsByThread.get(thread);
        return session;
    }

}
