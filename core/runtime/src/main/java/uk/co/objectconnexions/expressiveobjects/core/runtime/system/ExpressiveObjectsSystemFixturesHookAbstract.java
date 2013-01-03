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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.LogonFixture;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.ClassSubstitutorFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MetaModelRefiner;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.about.AboutExpressiveObjects;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.ExplorationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.awt.TemplateImageLoaderAwt;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.internal.ExpressiveObjectsLocaleInitializer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.internal.ExpressiveObjectsTimeZoneInitializer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.internal.SplashWindow;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileStore;

/**
 * An implementation of {@link ExpressiveObjectsSystem} that has a hook for installing
 * fixtures but does not install them itself.
 */
public abstract class ExpressiveObjectsSystemFixturesHookAbstract implements ExpressiveObjectsSystem {

    public static final Logger LOG = Logger.getLogger(ExpressiveObjectsSystemFixturesHookAbstract.class);

    private static final int SPLASH_DELAY_DEFAULT = 6;

    private final ExpressiveObjectsLocaleInitializer localeInitializer;
    private final ExpressiveObjectsTimeZoneInitializer timeZoneInitializer;
    private final DeploymentType deploymentType;

    private SplashWindow splashWindow;

    private boolean initialized = false;

    private ExpressiveObjectsSessionFactory sessionFactory;

    // ///////////////////////////////////////////
    // Constructors
    // ///////////////////////////////////////////

    public ExpressiveObjectsSystemFixturesHookAbstract(final DeploymentType deploymentType) {
        this(deploymentType, new ExpressiveObjectsLocaleInitializer(), new ExpressiveObjectsTimeZoneInitializer());
    }

    public ExpressiveObjectsSystemFixturesHookAbstract(final DeploymentType deploymentType, final ExpressiveObjectsLocaleInitializer localeInitializer, final ExpressiveObjectsTimeZoneInitializer timeZoneInitializer) {
        this.deploymentType = deploymentType;
        this.localeInitializer = localeInitializer;
        this.timeZoneInitializer = timeZoneInitializer;
    }

    // ///////////////////////////////////////////
    // DeploymentType
    // ///////////////////////////////////////////

    @Override
    public DeploymentType getDeploymentType() {
        return deploymentType;
    }

    // ///////////////////////////////////////////
    // init, shutdown
    // ///////////////////////////////////////////

    @Override
    public void init() {

        if (initialized) {
            throw new IllegalStateException("Already initialized");
        } else {
            initialized = true;
        }

        LOG.info("initialising Expressive Objects System");
        LOG.info("working directory: " + new File(".").getAbsolutePath());
        LOG.info("resource stream source: " + getConfiguration().getResourceStreamSource());

        localeInitializer.initLocale(getConfiguration());
        timeZoneInitializer.initTimeZone(getConfiguration());

        int splashDelay = SPLASH_DELAY_DEFAULT;
        try {
            final TemplateImageLoader splashLoader = obtainTemplateImageLoader();
            splashLoader.init();
            showSplash(splashLoader);

            sessionFactory = doCreateSessionFactory(deploymentType);

            // temporarily make a configuration available
            // REVIEW: would rather inject this, or perhaps even the
            // ConfigurationBuilder
            ExpressiveObjectsContext.setConfiguration(getConfiguration());

            initContext(sessionFactory);
            sessionFactory.init();

            installFixturesIfRequired();

        } catch (final ExpressiveObjectsSystemException ex) {
            LOG.error("failed to initialise", ex);
            splashDelay = 0;
            throw new RuntimeException(ex);
        } finally {
            removeSplash(splashDelay);
        }
    }

    private void initContext(final ExpressiveObjectsSessionFactory sessionFactory) {
        getDeploymentType().initContext(sessionFactory);
    }

    @Override
    public void shutdown() {
        LOG.info("shutting down system");
        ExpressiveObjectsContext.closeAllSessions();
    }

    // ///////////////////////////////////////////
    // Hook:
    // ///////////////////////////////////////////

    /**
     * Hook method; the returned implementation is expected to use the same
     * general approach as the subclass itself.
     * 
     * <p>
     * So, for example, <tt>ExpressiveObjectsSystemUsingInstallers</tt> uses the
     * {@link InstallerLookup} mechanism to find its components. The
     * corresponding <tt>ExecutionContextFactoryUsingInstallers</tt> object
     * returned by this method should use {@link InstallerLookup} likewise.
     */
    protected abstract ExpressiveObjectsSessionFactory doCreateSessionFactory(final DeploymentType deploymentType) throws ExpressiveObjectsSystemException;

    // ///////////////////////////////////////////
    // Configuration
    // ///////////////////////////////////////////

    /**
     * Populated after {@link #init()}.
     */
    @Override
    public ExpressiveObjectsSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // ///////////////////////////////////////////
    // Configuration
    // ///////////////////////////////////////////

    @Override
    public abstract ExpressiveObjectsConfiguration getConfiguration();

    // ///////////////////////////////////////////
    // TemplateImageLoader
    // ///////////////////////////////////////////

    /**
     * Just returns a {@link TemplateImageLoaderAwt}; subclasses may override if
     * required.
     */
    protected TemplateImageLoader obtainTemplateImageLoader() {
        return new TemplateImageLoaderAwt(getConfiguration());
    }

    // ///////////////////////////////////////////
    // OidMarshaller
    // ///////////////////////////////////////////

    /**
     * Just returns a {@link OidMarshaller}; subclasses may override if
     * required.
     */
    protected OidMarshaller obtainOidMarshaller() {
        return new OidMarshaller();
    }

    // ///////////////////////////////////////////
    // Reflector
    // ///////////////////////////////////////////

    protected abstract SpecificationLoaderSpi obtainSpecificationLoaderSpi(DeploymentType deploymentType, ClassSubstitutorFactory classSubstitutorFactory, Collection<MetaModelRefiner> metaModelRefiners) throws ExpressiveObjectsSystemException;

    // ///////////////////////////////////////////
    // PersistenceSessionFactory
    // ///////////////////////////////////////////

    protected abstract PersistenceSessionFactory obtainPersistenceSessionFactory(DeploymentType deploymentType) throws ExpressiveObjectsSystemException;

    // ///////////////////////////////////////////
    // Fixtures (hooks)
    // ///////////////////////////////////////////

    /**
     * Optional hook for appending debug information pertaining to fixtures
     * installer if required.
     */
    protected void appendFixturesInstallerDebug(final DebugBuilder debug) {
    }

    /**
     * The {@link LogonFixture}, if any, obtained by running fixtures.
     * 
     * <p>
     * Intended to be used when for {@link DeploymentType#EXPLORATION
     * exploration} (instead of an {@link ExplorationSession}) or
     * {@link DeploymentType#PROTOTYPE prototype} deployments (saves logging
     * in). Should be <i>ignored</i> in other {@link DeploymentType}s.
     * 
     * <p>
     * This implementation always returns <tt>null</tt>.
     */
    @Override
    public LogonFixture getLogonFixture() {
        return null;
    }

    /**
     * Optional hook for installing fixtures.
     * 
     * <p>
     * This implementation does nothing.
     */
    protected void installFixturesIfRequired() throws ExpressiveObjectsSystemException {
    }

    // ///////////////////////////////////////////
    // Authentication & Authorization Manager
    // ///////////////////////////////////////////

    protected abstract AuthenticationManager obtainAuthenticationManager(DeploymentType deploymentType) throws ExpressiveObjectsSystemException;

    protected abstract AuthorizationManager obtainAuthorizationManager(final DeploymentType deploymentType);

    // ///////////////////////////////////////////
    // UserProfileLoader
    // ///////////////////////////////////////////

    protected abstract UserProfileStore obtainUserProfileStore();

    // ///////////////////////////////////////////
    // Services
    // ///////////////////////////////////////////

    protected abstract List<Object> obtainServices();

    // ///////////////////////////////////////////
    // Splash
    // ///////////////////////////////////////////

    private void showSplash(final TemplateImageLoader imageLoader) {

        final boolean vetoSplashFromConfig = getConfiguration().getBoolean(SystemConstants.NOSPLASH_KEY, SystemConstants.NOSPLASH_DEFAULT);
        if (!vetoSplashFromConfig && getDeploymentType().shouldShowSplash()) {
            splashWindow = new SplashWindow(imageLoader);
        }
    }

    private void removeSplash(final int delay) {
        if (splashWindow != null) {
            if (delay == 0) {
                splashWindow.removeImmediately();
            } else {
                splashWindow.toFront();
                splashWindow.removeAfterDelay(delay);
            }
        }
    }

    // ///////////////////////////////////////////
    // debugging
    // ///////////////////////////////////////////

    private void debug(final DebugBuilder debug, final Object object) {
        if (object instanceof DebuggableWithTitle) {
            final DebuggableWithTitle d = (DebuggableWithTitle) object;
            debug.appendTitle(d.debugTitle());
            d.debugData(debug);
        } else {
            debug.appendln(object.toString());
            debug.appendln("... no further debug information");
        }
    }

    @Override
    public DebuggableWithTitle debugSection(final String selectionName) {
        // DebugInfo deb;
        if (selectionName.equals("Configuration")) {
            return getConfiguration();
        } /*
           * else if (selectionName.equals("Overview")) { debugOverview(debug);
           * } else if (selectionName.equals("Authenticator")) { deb =
           * ExpressiveObjectsContext.getAuthenticationManager(); } else if
           * (selectionName.equals("Reflector")) { deb =
           * ExpressiveObjectsContext.getSpecificationLoader(); } else if
           * (selectionName.equals("Contexts")) { deb =
           * debugListContexts(debug); } else { deb =
           * debugDisplayContext(selectionName, debug); }
           */
        return null;
    }

    private void debugDisplayContext(final String selector, final DebugBuilder debug) {
        final ExpressiveObjectsSession d = ExpressiveObjectsContext.getSession(selector);
        if (d != null) {
            d.debugAll(debug);
        } else {
            debug.appendln("No context: " + selector);
        }
    }

    private void debugListContexts(final DebugBuilder debug) {
        final String[] contextIds = ExpressiveObjectsContext.getInstance().allSessionIds();
        for (final String contextId : contextIds) {
            debug.appendln(contextId);
            debug.appendln("-----");
            final ExpressiveObjectsSession d = ExpressiveObjectsContext.getSession(contextId);
            d.debug(debug);
            debug.appendln();
        }
    }

    @Override
    public String[] debugSectionNames() {
        final String[] general = new String[] { "Overview", "Authenticator", "Configuration", "Reflector", "Requests", "Contexts" };
        final String[] contextIds = ExpressiveObjectsContext.getInstance().allSessionIds();
        final String[] combined = new String[general.length + contextIds.length];
        System.arraycopy(general, 0, combined, 0, general.length);
        System.arraycopy(contextIds, 0, combined, general.length, contextIds.length);
        return combined;
    }

    private void debugOverview(final DebugBuilder debug) {
        try {
            debug.appendln(AboutExpressiveObjects.getFrameworkName());
            debug.appendln(AboutExpressiveObjects.getFrameworkVersion());
            if (AboutExpressiveObjects.getApplicationName() != null) {
                debug.appendln("application: " + AboutExpressiveObjects.getApplicationName());
            }
            if (AboutExpressiveObjects.getApplicationVersion() != null) {
                debug.appendln("version" + AboutExpressiveObjects.getApplicationVersion());
            }

            final String user = System.getProperty("user.name");
            final String system = System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") " + System.getProperty("os.version");
            final String java = System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version");
            debug.appendln("user: " + user);
            debug.appendln("os: " + system);
            debug.appendln("java: " + java);
            debug.appendln("working directory: " + new File(".").getAbsolutePath());

            debug.appendTitle("System Installer");
            appendFixturesInstallerDebug(debug);

            debug.appendTitle("System Components");
            debug.appendln("Authentication manager", ExpressiveObjectsContext.getAuthenticationManager().getClass().getName());
            debug.appendln("Configuration", getConfiguration().getClass().getName());

            final DebuggableWithTitle[] inf = ExpressiveObjectsContext.debugSystem();
            for (final DebuggableWithTitle element : inf) {
                if (element != null) {
                    element.debugData(debug);
                }
            }
        } catch (final RuntimeException e) {
            debug.appendException(e);
        }
    }

}
