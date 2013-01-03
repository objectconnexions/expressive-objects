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

import java.util.List;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.TransactionScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationException;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugList;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.MessageBroker;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.UpdateNotifier;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;

/**
 * Provides singleton <i>access to</i> the current (session scoped)
 * {@link ExpressiveObjectsSession}, along with convenience methods to obtain
 * application-scoped components and also any transaction-scoped components
 * {@link TransactionScopedComponent} s if a {@link ExpressiveObjectsTransaction}
 * {@link ExpressiveObjectsSession#getCurrentTransaction() is in progress}.
 * 
 * <p>
 * Somewhat analogous to (the static methods in) <tt>HibernateUtil</tt>.
 */
public abstract class ExpressiveObjectsContext implements DebuggableWithTitle {

    private static final Logger LOG = Logger.getLogger(ExpressiveObjectsContext.class);

    private static ExpressiveObjectsContext singleton;

    private final ExpressiveObjectsSessionFactory sessionFactory;
    private final ContextReplacePolicy replacePolicy;
    private final SessionClosePolicy sessionClosePolicy;

    private static ExpressiveObjectsConfiguration configuration;

    // ///////////////////////////////////////////////////////////
    // Singleton & Constructor
    // ///////////////////////////////////////////////////////////

    /**
     * Returns the singleton providing access to the set of execution contexts.
     */
    public static ExpressiveObjectsContext getInstance() {
        return singleton;
    }

    /**
     * Whether a singleton has been created using {@link #getInstance()}.
     */
    public static boolean exists() {
        return singleton != null;
    }

    /**
     * Resets the singleton, so another can created.
     * 
     * @see #ExpressiveObjects()
     */
    public static void testReset() {
        singleton = null;
    }

    protected static enum SessionClosePolicy {
        /**
         * Sessions must be explicitly closed.
         */
        EXPLICIT_CLOSE,
        /**
         * Sessions will be automatically closed.
         */
        AUTO_CLOSE;
    }

    /**
     * Whether the {@link ExpressiveObjectsContext#getInstance() singleton} itself may be
     * replaced.
     */
    protected static enum ContextReplacePolicy {
        NOT_REPLACEABLE, REPLACEABLE
    }

    /**
     * Creates a new instance of the {@link ExpressiveObjectsSession} holder.
     * 
     * <p>
     * Will throw an exception if an instance has already been created and is
     * not {@link ContextReplacePolicy#REPLACEABLE}.
     */
    protected ExpressiveObjectsContext(final ContextReplacePolicy replacePolicy, final SessionClosePolicy sessionClosePolicy, final ExpressiveObjectsSessionFactory sessionFactory) {
        if (singleton != null && !singleton.isContextReplaceable()) {
            throw new ExpressiveObjectsException("Expressive Objects Context already set up and cannot be replaced");
        }
        singleton = this;
        this.sessionFactory = sessionFactory;
        this.sessionClosePolicy = sessionClosePolicy;
        this.replacePolicy = replacePolicy;
    }

    // ///////////////////////////////////////////////////////////
    // SessionFactory
    // ///////////////////////////////////////////////////////////

    /**
     * As injected in constructor.
     */
    public final ExpressiveObjectsSessionFactory getSessionFactoryInstance() {
        return sessionFactory;
    }

    // ///////////////////////////////////////////////////////////
    // Policies
    // ///////////////////////////////////////////////////////////

    /**
     * Whether a context singleton can simply be replaced or not.
     */
    public final boolean isContextReplaceable() {
        return replacePolicy == ContextReplacePolicy.REPLACEABLE;
    }

    /**
     * Whether any open session can be automatically
     * {@link #closeSessionInstance() close}d on
     * {@link #openSessionInstance(AuthenticationSession) open}.
     */
    public final boolean isSessionAutocloseable() {
        return sessionClosePolicy == SessionClosePolicy.AUTO_CLOSE;
    }

    /**
     * Helper method for subclasses' implementation of
     * {@link #openSessionInstance(AuthenticationSession)}.
     */
    protected void applySessionClosePolicy() {
        if (getSessionInstance() == null) {
            return;
        }
        if (!isSessionAutocloseable()) {
            throw new IllegalStateException("Session already open and context not configured for autoclose");
        }
        closeSessionInstance();
    }

    // ///////////////////////////////////////////////////////////
    // open / close / shutdown
    // ///////////////////////////////////////////////////////////

    /**
     * Creates a new {@link ExpressiveObjectsSession} and binds into the current context.
     * 
     * @throws IllegalStateException
     *             if already opened.
     */
    public abstract ExpressiveObjectsSession openSessionInstance(AuthenticationSession session);

    /**
     * Closes the {@link ExpressiveObjectsSession} for the current context.
     * 
     * <p>
     * Ignored if already closed.
     * 
     * <p>
     * This method is <i>not</i> marked <tt>final</tt> so it can be overridden
     * if necessarily. Generally speaking this shouldn't be necessary; one case
     * where it might though is if an implementation has multiple concurrent
     * uses of a session, in which case "closing" the session really means just
     * deregistering the usage of it by a particular thread; only when all
     * threads have finished with a session can it really be closed.
     */
    public void closeSessionInstance() {
        if (getSessionInstance() != null) {
            getSessionInstance().close();
            doClose();
        }
    }

    /**
     * Overridable hook method called from {@link #closeSessionInstance()},
     * allowing subclasses to clean up (for example datastructures).
     * 
     * <p>
     * The {@link #getSessionInstance() current} {@link ExpressiveObjectsSession} will
     * already have been {@link ExpressiveObjectsSession#close() closed}.
     */
    protected void doClose() {
    }

    /**
     * Shutdown the application.
     */
    protected abstract void closeAllSessionsInstance();

    // ///////////////////////////////////////////////////////////
    // getSession()
    // ///////////////////////////////////////////////////////////

    /**
     * Locates the current {@link ExpressiveObjectsSession}.
     * 
     * <p>
     * This might just be a singleton (eg {@link ExpressiveObjectsContextStatic}), or could
     * be retrieved from the thread (eg {@link ExpressiveObjectsContextThreadLocal}).
     */
    public abstract ExpressiveObjectsSession getSessionInstance();

    /**
     * The {@link ExpressiveObjectsSession} for specified {@link ExpressiveObjectsSession#getId()}.
     */
    protected abstract ExpressiveObjectsSession getSessionInstance(String sessionId);

    /**
     * All known session Ids.
     * 
     * <p>
     * Provided primarily for debugging.
     */
    public abstract String[] allSessionIds();

    // ///////////////////////////////////////////////////////////
    // Static Convenience methods (session management)
    // ///////////////////////////////////////////////////////////

    /**
     * Convenience method to open a new {@link ExpressiveObjectsSession}.
     * 
     * @see #openSessionInstance(AuthenticationSession)
     */
    public static ExpressiveObjectsSession openSession(final AuthenticationSession authenticationSession) {
        return getInstance().openSessionInstance(authenticationSession);
    }

    /**
     * Convenience method to close the current {@link ExpressiveObjectsSession}.
     * 
     * @see #closeSessionInstance()
     */
    public static void closeSession() {
        getInstance().closeSessionInstance();
    }

    /**
     * Convenience method to return {@link ExpressiveObjectsSession} for specified
     * {@link ExpressiveObjectsSession#getId()}.
     * 
     * <p>
     * Provided primarily for debugging.
     * 
     * @see #getSessionInstance(String)
     */
    public static ExpressiveObjectsSession getSession(final String sessionId) {
        return getInstance().getSessionInstance(sessionId);
    }

    /**
     * Convenience method to close all sessions.
     */
    public static void closeAllSessions() {
        LOG.info("closing all instances");
        final ExpressiveObjectsContext instance = getInstance();
        if (instance != null) {
            instance.closeAllSessionsInstance();
        }
    }

    // ///////////////////////////////////////////////////////////
    // Static Convenience methods (application scoped)
    // ///////////////////////////////////////////////////////////

    /**
     * Convenience method returning the {@link ExpressiveObjectsSessionFactory} of the
     * current {@link #getSession() session}.
     */
    public static ExpressiveObjectsSessionFactory getSessionFactory() {
        return getInstance().getSessionFactoryInstance();
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSessionFactory#getConfiguration()
     */
    public static ExpressiveObjectsConfiguration getConfiguration() {
        if (configuration == null) {
            throw new ExpressiveObjectsConfigurationException("No configuration available");
        }
        return configuration;
    }

    public static void setConfiguration(final ExpressiveObjectsConfiguration configuration) {
        ExpressiveObjectsContext.configuration = configuration;
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSessionFactory#getDeploymentType()
     */
    public static DeploymentType getDeploymentType() {
        return getSessionFactory().getDeploymentType();
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSessionFactory#getSpecificationLoader()
     */
    public static SpecificationLoaderSpi getSpecificationLoader() {
        return getSessionFactory().getSpecificationLoader();
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSessionFactory#getAuthenticationManager()
     */
    public static AuthenticationManager getAuthenticationManager() {
        return getSessionFactory().getAuthenticationManager();
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSessionFactory#getAuthorizationManager()
     */
    public static AuthorizationManager getAuthorizationManager() {
        return getSessionFactory().getAuthorizationManager();
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSessionFactory#getTemplateImageLoader()
     */
    public static TemplateImageLoader getTemplateImageLoader() {
        return getSessionFactory().getTemplateImageLoader();
    }

    public static UserProfileLoader getUserProfileLoader() {
        return getSessionFactory().getUserProfileLoader();
    }

    public static List<Object> getServices() {
        return getSessionFactory().getServices();
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSessionFactory#getOidMarshaller()
     */
    public static OidMarshaller getOidMarshaller() {
        return getSessionFactory().getOidMarshaller();
    }


    // ///////////////////////////////////////////////////////////
    // Static Convenience methods (session scoped)
    // ///////////////////////////////////////////////////////////

    public static boolean inSession() {
        final ExpressiveObjectsSession session = getInstance().getSessionInstance();
        return session != null;
    }

    /**
     * Convenience method returning the current {@link ExpressiveObjectsSession}.
     */
    public static ExpressiveObjectsSession getSession() {
        final ExpressiveObjectsSession session = getInstance().getSessionInstance();
        if (session == null) {
            throw new IllegalStateException("No Session opened for this thread");
        }
        return session;
    }

    /**
     * Convenience method to return the {@link #getSession() current}
     * {@link ExpressiveObjectsSession}'s {@link ExpressiveObjectsSession#getId() id}.
     * 
     * @see ExpressiveObjectsSession#getId()
     */
    public static String getSessionId() {
        return getSession().getId();
    }

    /**
     * @see ExpressiveObjectsSession#getAuthenticationSession()
     */
    public static AuthenticationSession getAuthenticationSession() {
        return getSession().getAuthenticationSession();
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSession#getPersistenceSession()
     */
    public static PersistenceSession getPersistenceSession() {
        return getSession().getPersistenceSession();
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSession#getUserProfile()
     */
    public static UserProfile getUserProfile() {
        return getSession().getUserProfile();
    }

    /**
     * Convenience method.
     * 
     * @see ExpressiveObjectsSession#getUserProfile()
     * @see UserProfile#getLocalization()
     */
    public static Localization getLocalization() {
        return getUserProfile().getLocalization();
    }

    /**
     * Convenience methods
     * 
     * @see ExpressiveObjectsSession#getPersistenceSession()
     * @see PersistenceSession#getTransactionManager()
     */
    public static ExpressiveObjectsTransactionManager getTransactionManager() {
        return getPersistenceSession().getTransactionManager();
    }

    // ///////////////////////////////////////////////////////////
    // Static Convenience methods (transaction scoped)
    // ///////////////////////////////////////////////////////////

    public static boolean inTransaction() {
        return inSession() && getCurrentTransaction() != null && !getCurrentTransaction().getState().isComplete();
    }

    /**
     * Convenience method, returning the current {@link ExpressiveObjectsTransaction
     * transaction} (if any).
     * 
     * <p>
     * Transactions are managed using the {@link ExpressiveObjectsTransactionManager}
     * obtainable from the {@link ExpressiveObjectsSession's} {@link PersistenceSession}.
     * 
     * @see ExpressiveObjectsSession#getCurrentTransaction()
     * @see PersistenceSession#getTransactionManager()
     */
    public static ExpressiveObjectsTransaction getCurrentTransaction() {
        return getSession().getCurrentTransaction();
    }

    /**
     * Convenience method, returning the {@link MessageBroker} of the
     * {@link #getCurrentTransaction() current transaction}.
     */
    public static MessageBroker getMessageBroker() {
        return getCurrentTransaction().getMessageBroker();
    }

    /**
     * Convenience method, returning the {@link UpdateNotifier} of the
     * {@link #getCurrentTransaction() current transaction}.
     */
    public static UpdateNotifier getUpdateNotifier() {
        return getCurrentTransaction().getUpdateNotifier();
    }

    // ///////////////////////////////////////////////////////////
    // Debug
    // ///////////////////////////////////////////////////////////

    public static DebuggableWithTitle[] debugSystem() {
        final DebugList debugList = new DebugList("Expressive Objects System");
        debugList.add("Context", getInstance());
        debugList.add("Expressive Objects session factory", getSessionFactory());
        debugList.add("  Authentication manager", getSessionFactory().getAuthenticationManager());
        debugList.add("  Authorization manager", getSessionFactory().getAuthorizationManager());
        debugList.add("  Persistence session factory", getSessionFactory().getPersistenceSessionFactory());
        debugList.add("User profile loader", getUserProfileLoader());

        debugList.add("Reflector", getSpecificationLoader());
        debugList.add("Template image loader", getTemplateImageLoader());

        debugList.add("Deployment type", getDeploymentType().getDebug());
        debugList.add("Configuration", getConfiguration());

        debugList.add("Services", getServices());
        return debugList.debug();
    }

    public static DebuggableWithTitle[] debugSession() {
        final DebugList debugList = new DebugList("Expressive Objects Session");
        debugList.add("Expressive Objects session", getSession());
        debugList.add("Authentication session", getAuthenticationSession());
        debugList.add("User profile", getUserProfile());

        debugList.add("Persistence Session", getPersistenceSession());
        debugList.add("Transaction Manager", getTransactionManager());

        debugList.add("Service injector", getPersistenceSession().getServicesInjector());
        debugList.add("Adapter factory", getPersistenceSession().getObjectAdapterFactory());
        debugList.add("Object factory", getPersistenceSession().getObjectFactory());
        debugList.add("OID generator", getPersistenceSession().getOidGenerator());
        debugList.add("Adapter manager", getPersistenceSession().getAdapterManager());
        debugList.add("Services", getPersistenceSession().getServices());
        return debugList.debug();
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        debug.appendln("context ", this);
    }

}
