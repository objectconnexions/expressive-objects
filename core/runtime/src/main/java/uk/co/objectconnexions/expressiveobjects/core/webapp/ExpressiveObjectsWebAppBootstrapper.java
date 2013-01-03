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

package uk.co.objectconnexions.expressiveobjects.core.webapp;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderPrimer;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderResourceStreams;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.NotFoundPolicy;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSource;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceComposite;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceContextLoaderClassPath;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceFileSystem;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installers.InstallerLookupDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.logging.ExpressiveObjectsLoggingConfigurer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.ExpressiveObjectsInjectModule;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystem;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.SystemConstants;
import uk.co.objectconnexions.expressiveobjects.core.webapp.config.ResourceStreamSourceForWebInf;

/**
 * Initialize the {@link ExpressiveObjectsSystem} when the web application starts, and
 * destroys it when it ends.
 * <p>
 * Implementation note: we use a number of helper builders to keep this class as
 * small and focused as possible. The builders are available for reuse by other
 * bootstrappers.
 */
public class ExpressiveObjectsWebAppBootstrapper implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(ExpressiveObjectsWebAppBootstrapper.class);
    private final ExpressiveObjectsLoggingConfigurer loggingConfigurer = new ExpressiveObjectsLoggingConfigurer();
    private Injector injector;

    /**
     * Convenience for servlets that need to obtain the {@link ExpressiveObjectsSystem}.
     */
    public static ExpressiveObjectsSystem getSystemBoundTo(final ServletContext servletContext) {
        final Object system = servletContext.getAttribute(WebAppConstants.EXPRESSIVE_OBJECTS_SYSTEM_KEY);
        return (ExpressiveObjectsSystem) system;
    }

    // /////////////////////////////////////////////////////
    // Initialization
    // /////////////////////////////////////////////////////

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        try {
            final ServletContext servletContext = servletContextEvent.getServletContext();

            final String webappDir = servletContext.getRealPath("/");
            final String webInfDir = servletContext.getRealPath("/WEB-INF");
            loggingConfigurer.configureLogging(webInfDir, new String[0]);

            String configLocation = servletContext.getInitParameter(WebAppConstants.CONFIG_DIR_PARAM);
            ResourceStreamSourceComposite compositeSource = null;
            if ( configLocation == null ) {
              LOG.info( "Config override location: No override location configured!" );
              compositeSource = new ResourceStreamSourceComposite(ResourceStreamSourceContextLoaderClassPath.create(), new ResourceStreamSourceForWebInf(servletContext));
            } else {
              LOG.info( "Config override location: " + configLocation );
              ResourceStreamSource configSourceStream = ResourceStreamSourceFileSystem.create(configLocation);
              compositeSource = new ResourceStreamSourceComposite(ResourceStreamSourceContextLoaderClassPath.create(), new ResourceStreamSourceForWebInf(servletContext), configSourceStream);
            }
            // will load either from WEB-INF, from the classpath or from config directory.
            final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder = new ExpressiveObjectsConfigurationBuilderResourceStreams(compositeSource);

            primeConfigurationBuilder(expressiveObjectsConfigurationBuilder, servletContext);

            expressiveObjectsConfigurationBuilder.addDefaultConfigurationResources();

            final DeploymentType deploymentType = determineDeploymentType(expressiveObjectsConfigurationBuilder, servletContext);

            addConfigurationResourcesForWebApps(expressiveObjectsConfigurationBuilder);
            addConfigurationResourcesForDeploymentType(expressiveObjectsConfigurationBuilder, deploymentType);
            addConfigurationResourcesForViewers(expressiveObjectsConfigurationBuilder, servletContext);

            expressiveObjectsConfigurationBuilder.add(WebAppConstants.WEB_APP_DIR, webappDir);
            expressiveObjectsConfigurationBuilder.add(SystemConstants.NOSPLASH_KEY, "true");

            final InstallerLookup installerLookup = new InstallerLookupDefault();

            injector = createGuiceInjector(expressiveObjectsConfigurationBuilder, deploymentType, installerLookup);

            final ExpressiveObjectsSystem system = injector.getInstance(ExpressiveObjectsSystem.class);

            expressiveObjectsConfigurationBuilder.lockConfiguration();
            expressiveObjectsConfigurationBuilder.dumpResourcesToLog();

            servletContext.setAttribute(WebAppConstants.EXPRESSIVE_OBJECTS_SYSTEM_KEY, system);
        } catch (final RuntimeException e) {
            LOG.error("startup failed", e);
            throw e;
        }
        LOG.info("server started");
    }

    private Injector createGuiceInjector(final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder, final DeploymentType deploymentType, final InstallerLookup installerLookup) {
        final ExpressiveObjectsInjectModule expressiveObjectsModule = new ExpressiveObjectsInjectModule(deploymentType, expressiveObjectsConfigurationBuilder, installerLookup);
        return Guice.createInjector(expressiveObjectsModule);
    }

    @SuppressWarnings("unchecked")
    private void primeConfigurationBuilder(final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder, final ServletContext servletContext) {
        LOG.info("loading properties from option handlers");
        final List<ExpressiveObjectsConfigurationBuilderPrimer> expressiveObjectsConfigurationBuilderPrimers =
                (List<ExpressiveObjectsConfigurationBuilderPrimer>) servletContext.getAttribute(WebAppConstants.CONFIGURATION_PRIMERS_KEY);
        if (expressiveObjectsConfigurationBuilderPrimers == null) {
            return;
        }
        for (final ExpressiveObjectsConfigurationBuilderPrimer expressiveObjectsConfigurationBuilderPrimer : expressiveObjectsConfigurationBuilderPrimers) {
            LOG.debug("priming configurations for " + expressiveObjectsConfigurationBuilderPrimer);
            expressiveObjectsConfigurationBuilderPrimer.primeConfigurationBuilder(expressiveObjectsConfigurationBuilder);
        }
    }

    /**
     * Checks {@link ExpressiveObjectsConfigurationBuilder configuration} for
     * {@value SystemConstants#DEPLOYMENT_TYPE_KEY}, (that is, from the command
     * line), but otherwise searches in the {@link ServletContext}, first for
     * {@value WebAppConstants#DEPLOYMENT_TYPE_KEY} and also
     * {@value SystemConstants#DEPLOYMENT_TYPE_KEY}.
     * <p>
     * If no setting is found, defaults to
     * {@value WebAppConstants#DEPLOYMENT_TYPE_DEFAULT}.
     */
    private DeploymentType determineDeploymentType(final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder, final ServletContext servletContext) {
        String deploymentTypeStr = null;
        deploymentTypeStr = servletContext.getInitParameter(WebAppConstants.DEPLOYMENT_TYPE_KEY);
        if (deploymentTypeStr == null) {
            deploymentTypeStr = servletContext.getInitParameter(SystemConstants.DEPLOYMENT_TYPE_KEY);
        }
        if (deploymentTypeStr == null) {
            deploymentTypeStr = expressiveObjectsConfigurationBuilder.getConfiguration().getString(SystemConstants.DEPLOYMENT_TYPE_KEY);
        }
        if (deploymentTypeStr == null) {
            deploymentTypeStr = WebAppConstants.DEPLOYMENT_TYPE_DEFAULT;
        }
        return DeploymentType.lookup(deploymentTypeStr);
    }

    private void addConfigurationResourcesForDeploymentType(final ExpressiveObjectsConfigurationBuilder configurationLoader, final DeploymentType deploymentType) {
        final String type = deploymentType.name().toLowerCase();
        configurationLoader.addConfigurationResource(type + ".properties", NotFoundPolicy.CONTINUE);
    }

    private void addConfigurationResourcesForWebApps(final ExpressiveObjectsConfigurationBuilder configurationLoader) {
        for (final String config : (new String[] { "web.properties", "war.properties" })) {
            if (config != null) {
                configurationLoader.addConfigurationResource(config, NotFoundPolicy.CONTINUE);
            }
        }
    }

    private void addConfigurationResourcesForViewers(final ExpressiveObjectsConfigurationBuilder configurationLoader, final ServletContext servletContext) {
        addConfigurationResourcesForContextParam(configurationLoader, servletContext, "expressive-objects.viewers");
        addConfigurationResourcesForContextParam(configurationLoader, servletContext, "expressive-objects.viewer");
    }

    private void addConfigurationResourcesForContextParam(final ExpressiveObjectsConfigurationBuilder configurationLoader, final ServletContext servletContext, final String name) {
        final String viewers = servletContext.getInitParameter(name);
        if (viewers == null) {
            return;
        }
        for (final String viewer : viewers.split(",")) {
            configurationLoader.addConfigurationResource("viewer_" + viewer + ".properties", NotFoundPolicy.CONTINUE);
        }
    }

    // /////////////////////////////////////////////////////
    // Destroy
    // /////////////////////////////////////////////////////

    @Override
    public void contextDestroyed(final ServletContextEvent ev) {
        LOG.info("server shutting down");
        final ServletContext servletContext = ev.getServletContext();

        try {
            final ExpressiveObjectsSystem system = (ExpressiveObjectsSystem) servletContext.getAttribute(WebAppConstants.EXPRESSIVE_OBJECTS_SYSTEM_KEY);
            if (system != null) {
                LOG.info("calling system shutdown");
                system.shutdown();
            }
        } finally {
            servletContext.removeAttribute(WebAppConstants.EXPRESSIVE_OBJECTS_SYSTEM_KEY);
            LOG.info("server shut down");
        }
    }

}
