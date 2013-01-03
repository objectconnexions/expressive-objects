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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system.session;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.SessionScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystem;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;

/**
 * Analogous to a Hibernate <tt>Session</tt>, holds the current set of
 * components for a specific execution context (such as on a thread).
 * 
 * <p>
 * The <tt>ExpressiveObjectsContext</tt> class (in <tt>nof-core</tt>) is responsible for
 * locating the current execution context.
 * 
 * @see ExpressiveObjectsSessionFactory
 */
public interface ExpressiveObjectsSession extends SessionScopedComponent {

    // //////////////////////////////////////////////////////
    // ExecutionContextFactory
    // //////////////////////////////////////////////////////

    /**
     * The creating {@link ExpressiveObjectsSessionFactory factory}.
     * 
     * <p>
     * Note that from the factory we can
     * {@link ExpressiveObjectsSessionFactory#getExpressiveObjectsSystem() get to} the {@link ExpressiveObjectsSystem},
     * and thus other {@link ApplicationScopedComponent}s.
     */
    public ExpressiveObjectsSessionFactory getSessionFactory();

    // //////////////////////////////////////////////////////
    // closeAll
    // //////////////////////////////////////////////////////

    /**
     * Normal lifecycle is managed using callbacks in
     * {@link SessionScopedComponent}. This method is to allow the outer
     * {@link ApplicationScopedComponent}s to shutdown, closing any and all
     * running {@link ExpressiveObjectsSession}s.
     */
    public void closeAll();

    // //////////////////////////////////////////////////////
    // Id
    // //////////////////////////////////////////////////////

    /**
     * A descriptive identifier for this {@link ExpressiveObjectsSession}.
     */
    public String getId();

    // //////////////////////////////////////////////////////
    // Authentication Session
    // //////////////////////////////////////////////////////

    /**
     * Returns the {@link AuthenticationSession} representing this user for this
     * {@link ExpressiveObjectsSession}.
     */
    public AuthenticationSession getAuthenticationSession();

    // //////////////////////////////////////////////////////
    // Persistence Session
    // //////////////////////////////////////////////////////

    /**
     * The {@link PersistenceSession} within this {@link ExpressiveObjectsSession}.
     * 
     * <p>
     * Would have been created by the {@link #getSessionFactory() owning
     * factory}'s
     * 
     */
    public PersistenceSession getPersistenceSession();

    // //////////////////////////////////////////////////////
    // Perspective
    // //////////////////////////////////////////////////////

    /**
     * Returns the {@link ObjectAdapter adapted} <tt>Perspective</tt> for the
     * user who is using this {@link ExpressiveObjectsSession} .
     */

    public UserProfile getUserProfile();

    // //////////////////////////////////////////////////////
    // Transaction (if in progress)
    // //////////////////////////////////////////////////////

    public ExpressiveObjectsTransaction getCurrentTransaction();

    // //////////////////////////////////////////////////////
    // Debugging
    // //////////////////////////////////////////////////////

    public void debugAll(DebugBuilder debug);

    public void debug(DebugBuilder debug);

    public void debugState(DebugBuilder debug);

}
